package com.programacao.web.webstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class WebstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebstoreApplication.class, args);
	}

}
