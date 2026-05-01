package com.glamgest.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GlamgestApplication {

	private static final Logger logger = LoggerFactory.getLogger(GlamgestApplication.class);

	public static void main(String[] args) {
		logger.info("Iniciando aplicación GlamGest...");
		SpringApplication.run(GlamgestApplication.class, args);
		logger.info("Aplicación GlamGest iniciada exitosamente.");
	}

}
