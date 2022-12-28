package com.prajekpro.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan(basePackages = { 
		"com.prajekpro.api", 
		"com.safalyatech.common", 
		"com.safalyatech.emailUtility" })
@PropertySource(value = {
		"classpath:validation-messages.properties",
		"classpath:notification-messages.properties"
})
@EnableScheduling
public class PPApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PPApiApplication.class, args);
	}

}
