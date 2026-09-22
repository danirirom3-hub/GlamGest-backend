# GlamGest Backend

API REST para la gestión de clientes, citas, servicios, empleados y ventas.

## Requisitos

- Java 21
- MySQL 8
- Maven Wrapper

## Configuración

Definir estas variables antes de ejecutar la aplicación:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/glamgest_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=glamgest_app
SPRING_DATASOURCE_PASSWORD=<password>
JWT_SECRET=<secret-con-al-menos-32-caracteres>
CORS_ALLOWED_ORIGINS=http://localhost:4200
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
```

Crear la base de datos con `glamgest_db.sql`. Para una base existente, limpiar duplicados de email y aplicar las migraciones de `database/migration` en orden antes de activar `validate`.

## Autenticación de clientes

`POST /api/auth/register` crea un usuario con rol `CLIENT` y crea o enlaza su perfil en `clients` usando el email. Si un administrador ya creó el cliente, el registro lo vincula sin duplicar sus citas.

`POST /api/auth/login` devuelve el token Bearer, el rol, el identificador del usuario y el identificador del cliente cuando existe.

Los clientes autenticados pueden usar:

- `GET /api/clients/me`
- `POST /api/appointments` sin enviar `clientId`
- `GET /api/appointments/me`
- `PUT` y `DELETE` de sus propias citas

La creación de usuarios internos permanece restringida a `ADMIN`. El rol de un registro público siempre es `CLIENT`.

## Ejecución

```text
./mvnw spring-boot:run
```

Las pruebas usan H2 y se ejecutan con:

```text
./mvnw test
```

## Duración de servicios y citas

`services.duration_minutes` es la duración base y debe ser mayor que cero y múltiplo de 15. Las nuevas duraciones válidas son `15`, `30`, `45`, `60`, `75`, etc.

Las citas guardan su duración efectiva en `appointments.duration_minutes`. Si el campo no se envía al crear una cita, se copia la duración base del servicio. Una duración enviada para una cita no modifica el servicio.

Ejemplo de cita con duración personalizada:

```json
{
  "appointmentDatetime": "2026-09-25T10:00:00",
  "clientId": 1,
  "employeeId": 2,
  "serviceId": 3,
  "durationMinutes": 90,
  "notes": "Cabello largo"
}
```

La duración de cada cita también debe ser múltiplo de 15. El backend rechaza solapamientos del mismo empleado con HTTP `409`; las citas `CANCELLED` y `NO_SHOW` no bloquean horarios.

Para bases existentes, ejecutar `database/migration/V5__add_appointment_effective_duration.sql`. La migración reporta duraciones de servicios inválidas y no modifica silenciosamente datos históricos.

## Reactivación de servicios

`DELETE /api/services/{id}` desactiva el servicio sin eliminarlo físicamente. Para reactivarlo o actualizarlo, usar `PUT /api/services/{id}`; la búsqueda incluye servicios inactivos.

Los administradores pueden consultar activos e inactivos con `GET /api/services/admin`. El listado normal de `GET /api/services` continúa devolviendo únicamente servicios activos.

Ejemplo de reactivación:

```json
{
  "active": true
}
```

Al crear un servicio con un nombre que ya pertenece a un servicio inactivo, el backend actualiza y reactiva el mismo registro conservando su identificador. Si el nombre pertenece a un servicio activo, devuelve `409 Conflict`.
