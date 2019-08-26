package com.example.esanalytics.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Autowired
	RestTemplateBuilder restTemplateBuilder;
	
	@Bean
	public RestTemplate restTemplate() {
		return restTemplateBuilder.build();
	}
}
