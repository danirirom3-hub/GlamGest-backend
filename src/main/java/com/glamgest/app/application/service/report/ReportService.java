package com.glamgest.app.application.service.report;

import com.glamgest.app.application.dto.dashboard.DashboardEmployeeMetricDTO;
import com.glamgest.app.application.dto.dashboard.DashboardServiceMetricDTO;
import com.glamgest.app.application.dto.dashboard.DashboardSummaryDTO;
import com.glamgest.app.application.service.dashboard.DashboardService;
import com.glamgest.app.common.exception.InvalidDashboardPeriodException;
import com.glamgest.app.infrastructure.persistence.repository.JpaAppointmentRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaSaleDetailsRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaSalesRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DashboardService dashboardService;
    private final JpaSalesRepository salesRepository;
    private final JpaSaleDetailsRepository saleDetailsRepository;
    private final JpaAppointmentRepository appointmentRepository;

    public ReportService(DashboardService dashboardService,
                         JpaSalesRepository salesRepository,
                         JpaSaleDetailsRepository saleDetailsRepository,
                         JpaAppointmentRepository appointmentRepository) {
        this.dashboardService = dashboardService;
        this.salesRepository = salesRepository;
        this.saleDetailsRepository = saleDetailsRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public byte[] executivePdf(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        DashboardSummaryDTO summary = dashboardService.summary(period.fromDate(), period.toDate());
        List<DashboardServiceMetricDTO> services = dashboardService.services(period.fromDate(), period.toDate());
        List<DashboardEmployeeMetricDTO> employees = dashboardService.employees(period.fromDate(), period.toDate());

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, output);
            document.open();
            addTitle(document, "Resumen ejecutivo", period);
            addSummaryTable(document, summary);
            addServiceTable(document, services);
            addEmployeeTable(document, employees);
            document.close();
            return output.toByteArray();
        } catch (IOException | DocumentException exception) {
            throw new IllegalStateException("No se pudo generar el reporte ejecutivo", exception);
        }
    }

    public byte[] appointmentsPdf(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        List<Object[]> appointments = appointmentRepository.detailBetween(period.from(), period.to());
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 24, 24, 30, 30);
            PdfWriter.getInstance(document, output);
            document.open();
            addTitle(document, "Reporte de citas", period);
            PdfPTable table = table(7, new float[]{1.2f, 1.5f, 1.5f, 1.5f, 1f, .8f, 2f});
            addHeaders(table, "Fecha", "Cliente", "Empleado", "Servicio", "Estado", "Minutos", "Observaciones");
            for (Object[] row : appointments) {
                addCells(table, formatDateTime(row[0]), text(row[1]), text(row[2]), text(row[3]),
                        text(row[4]), text(row[5]), text(row[6]));
            }
            document.add(table);
            document.close();
            return output.toByteArray();
        } catch (IOException | DocumentException exception) {
            throw new IllegalStateException("No se pudo generar el reporte de citas", exception);
        }
    }

    public byte[] salesExcel(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        List<Object[]> rows = salesRepository.activeDetailBetween(period.from(), period.to());
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Detalle de ventas");
            ExcelStyles styles = new ExcelStyles(workbook);
            addExcelTitle(sheet, "Detalle de ventas", period, 9, styles);
            addExcelHeaders(sheet, 2, styles.header(), "Fecha", "Venta", "Cliente", "Empleado", "Servicio",
                    "Cantidad", "Precio unitario", "Subtotal", "Método de pago");
            int index = 3;
            for (Object[] row : rows) {
                Row excelRow = sheet.createRow(index++);
                textCell(excelRow, 0, formatDateTime(row[0]));
                numberCell(excelRow, 1, row[1]);
                textCell(excelRow, 2, text(row[2]));
                textCell(excelRow, 3, text(row[3]));
                textCell(excelRow, 4, text(row[4]));
                numberCell(excelRow, 5, row[5]);
                numberCell(excelRow, 6, row[6]);
                numberCell(excelRow, 7, row[7]);
                textCell(excelRow, 8, text(row[8]));
            }
            finishSheet(sheet, 9);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo generar el reporte de ventas", exception);
        }
    }

    public byte[] servicesExcel(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        List<Object[]> rows = saleDetailsRepository.detailedRevenueByServiceBetween(period.from(), period.to());
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Servicios");
            ExcelStyles styles = new ExcelStyles(workbook);
            addExcelTitle(sheet, "Servicios más vendidos", period, 6, styles);
            addExcelHeaders(sheet, 2, styles.header(), "Servicio", "Categoría", "Unidades", "Ingresos", "% del total", "ID");
            long total = rows.stream().mapToLong(row -> number(row[4])).sum();
            int index = 3;
            for (Object[] row : rows) {
                Row excelRow = sheet.createRow(index++);
                textCell(excelRow, 0, text(row[1]));
                textCell(excelRow, 1, text(row[2]));
                numberCell(excelRow, 2, row[3]);
                numberCell(excelRow, 3, row[4]);
                double percentage = total == 0 ? 0 : (number(row[4]) * 100d / total);
                excelRow.createCell(4).setCellValue(percentage / 100d);
                excelRow.getCell(4).setCellStyle(styles.percentage());
                numberCell(excelRow, 5, row[0]);
            }
            finishSheet(sheet, 6);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo generar el reporte de servicios", exception);
        }
    }

    public byte[] employeesExcel(LocalDate from, LocalDate to) {
        Period period = period(from, to);
        List<DashboardEmployeeMetricDTO> appointments = dashboardService.employees(period.fromDate(), period.toDate());
        List<Object[]> sales = saleDetailsRepository.detailedRevenueByEmployeeBetween(period.from(), period.to());
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Empleados");
            ExcelStyles styles = new ExcelStyles(workbook);
            addExcelTitle(sheet, "Rendimiento de empleados", period, 4, styles);
            addExcelHeaders(sheet, 2, styles.header(), "Empleado", "Citas atendidas", "Ventas asociadas", "Ingresos");
            int index = 3;
            for (DashboardEmployeeMetricDTO row : appointments) {
                Row excelRow = sheet.createRow(index++);
                textCell(excelRow, 0, row.employeeName());
                numberCell(excelRow, 1, row.appointments());
                Object[] salesRow = sales.stream()
                        .filter(candidate -> number(candidate[0]) == row.employeeId())
                        .findFirst().orElse(null);
                numberCell(excelRow, 2, salesRow == null ? 0 : salesRow[2]);
                numberCell(excelRow, 3, salesRow == null ? row.revenue() : salesRow[3]);
            }
            finishSheet(sheet, 4);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo generar el reporte de empleados", exception);
        }
    }

    private void addTitle(Document document, String title, Period period) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(42, 42, 42));
        Paragraph heading = new Paragraph(title, titleFont);
        heading.setAlignment(Element.ALIGN_CENTER);
        document.add(heading);
        Paragraph periodText = new Paragraph("Período: " + period.fromDate().format(DATE_FORMAT) + " - "
                + period.toDate().format(DATE_FORMAT));
        periodText.setAlignment(Element.ALIGN_CENTER);
        document.add(periodText);
        document.add(new Paragraph(" "));
    }

    private void addSummaryTable(Document document, DashboardSummaryDTO summary) throws DocumentException {
        PdfPTable table = table(2, new float[]{2, 1});
        addHeaders(table, "Indicador", "Valor");
        addCells(table, "Ingresos totales", String.valueOf(summary.totalRevenue()));
        addCells(table, "Ventas", String.valueOf(summary.totalSales()));
        addCells(table, "Ticket promedio", summary.averageTicket().toPlainString());
        addCells(table, "Nuevos clientes", String.valueOf(summary.newClients()));
        addCells(table, "Citas", String.valueOf(summary.totalAppointments()));
        document.add(table);
        document.add(new Paragraph(" "));
        addStatusTable(document, summary);
    }

    private void addStatusTable(Document document, DashboardSummaryDTO summary) throws DocumentException {
        PdfPTable table = table(2, new float[]{2, 1});
        addHeaders(table, "Citas por estado", "Cantidad");
        summary.appointmentsByStatus().forEach(status -> addCells(table, status.label(), String.valueOf(status.value())));
        document.add(table);
    }

    private void addServiceTable(Document document, List<DashboardServiceMetricDTO> services) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Servicios más vendidos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        PdfPTable table = table(3, new float[]{2, 1, 1});
        addHeaders(table, "Servicio", "Unidades", "Ingresos");
        services.forEach(row -> addCells(table, row.serviceName(), String.valueOf(row.units()), String.valueOf(row.revenue())));
        document.add(table);
    }

    private void addEmployeeTable(Document document, List<DashboardEmployeeMetricDTO> employees) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Rendimiento de empleados", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        PdfPTable table = table(3, new float[]{2, 1, 1});
        addHeaders(table, "Empleado", "Citas", "Ingresos");
        employees.forEach(row -> addCells(table, row.employeeName(), String.valueOf(row.appointments()), String.valueOf(row.revenue())));
        document.add(table);
    }

    private PdfPTable table(int columns, float[] widths) {
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(100);
        try {
            table.setWidths(widths);
        } catch (DocumentException exception) {
            throw new IllegalStateException("No se pudo configurar la tabla del reporte", exception);
        }
        return table;
    }

    private void addHeaders(PdfPTable table, String... headers) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(new Color(75, 85, 99));
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addCells(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value == null ? "" : value));
            cell.setPadding(4);
            table.addCell(cell);
        }
    }

    private void addExcelTitle(org.apache.poi.ss.usermodel.Sheet sheet, String title, Period period,
                               int columns, ExcelStyles styles) {
        Row titleRow = sheet.createRow(0);
        Cell cell = titleRow.createCell(0);
        cell.setCellValue(title + " | " + period.fromDate().format(DATE_FORMAT) + " - "
                + period.toDate().format(DATE_FORMAT));
        cell.setCellStyle(styles.title());
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, columns - 1));
    }

    private void addExcelHeaders(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex, CellStyle style,
                                 String... headers) {
        Row row = sheet.createRow(rowIndex);
        for (int index = 0; index < headers.length; index++) {
            Cell cell = row.createCell(index);
            cell.setCellValue(headers[index]);
            cell.setCellStyle(style);
        }
    }

    private void finishSheet(org.apache.poi.ss.usermodel.Sheet sheet, int columns) {
        sheet.createFreezePane(0, 3);
        sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(2, Math.max(2, sheet.getLastRowNum()), 0, columns - 1));
        for (int index = 0; index < columns; index++) {
            sheet.autoSizeColumn(index);
        }
    }

    private void textCell(Row row, int column, String value) {
        row.createCell(column).setCellValue(value == null ? "" : value);
    }

    private void numberCell(Row row, int column, Object value) {
        row.createCell(column).setCellValue(number(value));
    }

    private Period period(LocalDate from, LocalDate to) {
        LocalDate resolvedFrom = from == null ? LocalDate.now().withDayOfMonth(1) : from;
        LocalDate resolvedTo = to == null ? LocalDate.now() : to;
        if (resolvedTo.isBefore(resolvedFrom)) {
            throw new InvalidDashboardPeriodException("La fecha final no puede ser anterior a la fecha inicial");
        }
        return new Period(resolvedFrom.atStartOfDay(), resolvedTo.plusDays(1).atStartOfDay(), resolvedFrom, resolvedTo);
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static long number(Object value) {
        return value == null ? 0 : ((Number) value).longValue();
    }

    private static String formatDateTime(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime.format(DATE_TIME_FORMAT);
        }
        if (value instanceof Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DATE_TIME_FORMAT);
        }
        return String.valueOf(value);
    }

    private record Period(LocalDateTime from, LocalDateTime to, LocalDate fromDate, LocalDate toDate) {
    }

    private record ExcelStyles(CellStyle title, CellStyle header, CellStyle percentage) {
        private ExcelStyles(Workbook workbook) {
            this(createTitle(workbook), createHeader(workbook), createPercentage(workbook));
        }

        private static CellStyle createTitle(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);
            style.setFont(font);
            return style;
        }

        private static CellStyle createHeader(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            style.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            return style;
        }

        private static CellStyle createPercentage(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
            return style;
        }
    }
}
