package com.quest.loganalyzer.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = { "com.quest.loganalyzer" }) // same as @Configuration
																		// @EnableAutoConfiguration @ComponentScan
																		// combined
@EnableConfigurationProperties({ FileStorageProperties.class })
public class SpringBootRestApiApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestApiApp.class, args);
	}

}
