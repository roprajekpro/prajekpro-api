package com.prajekpro.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfigs {

	@Bean(name = "restTemplateClient")
	public RestTemplate getRestTemplateClient() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
}
