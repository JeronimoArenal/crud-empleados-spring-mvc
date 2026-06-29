package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.example")
public class CrudEmpleadosSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudEmpleadosSpringApplication.class, args);
	}

}
