package com.demo.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchDemo4Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchDemo4Application.class, args);
	}

}
