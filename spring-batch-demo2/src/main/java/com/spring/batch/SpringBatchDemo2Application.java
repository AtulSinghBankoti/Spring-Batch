package com.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchDemo2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchDemo2Application.class, args);
	}

}
