package com.glamgest.app.application.service.email;

import com.glamgest.app.application.dto.email.EmailRequestDTO;
import com.glamgest.app.application.dto.email.EmailResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Service
public class EmailClientService {

    private static final Logger logger = LoggerFactory.getLogger(EmailClientService.class);

    @Value("${email.service.url:http://localhost:8081/api/email/send}")
    private String emailServiceUrl;

    private final RestTemplate restTemplate;

    public EmailClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Envía un correo electrónico usando el servicio de email externo
     * 
     * @param emailRequest DTO con recipient, subject y body
     * @return EmailResponseDTO con el resultado del envío
     */
    public EmailResponseDTO sendEmail(EmailRequestDTO emailRequest) {
        try {
            logger.info("Enviando correo a: {}", emailRequest.getRecipient());
            
            EmailResponseDTO response = restTemplate.postForObject(
                emailServiceUrl,
                emailRequest,
                EmailResponseDTO.class
            );
            
            if (response != null && response.isSuccess()) {
                logger.info("Correo enviado exitosamente a: {}", emailRequest.getRecipient());
            } else {
                logger.warn("Error al enviar correo: {}", response != null ? response.getError() : "Unknown error");
            }
            
            return response;
        } catch (RestClientException e) {
            logger.error("Error al conectar con el servicio de email: {}", e.getMessage(), e);
            EmailResponseDTO errorResponse = new EmailResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setError("Error al conectar con el servicio de email: " + e.getMessage());
            return errorResponse;
        }
    }
}
