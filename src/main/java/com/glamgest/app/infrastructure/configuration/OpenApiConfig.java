package com.glamgest.app.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI glamgestOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GlamGest API")
                        .description("API para gestión de salón de belleza")
                        .version("1.0.0"));
    }
}