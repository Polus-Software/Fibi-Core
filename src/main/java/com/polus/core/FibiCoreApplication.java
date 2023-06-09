package com.polus.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories
@ComponentScan
@EnableScheduling
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class FibiCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(FibiCoreApplication.class, args);
	}

}
