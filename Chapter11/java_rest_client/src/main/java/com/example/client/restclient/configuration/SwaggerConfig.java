package com.example.client.restclient.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	public static final ApiInfo DEFAULT_API_INFO = new ApiInfoBuilder().title("Elasticsearch Java REST API")
			.description("DEMO High/Low Level REST API")
			.contact(new Contact("Wai Tak Wong", "https://www.packt.com/about", "wtwong316@gmail.com"))
			.license("Apache License Version 2.0")
			.version("0.1")
			.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
			.build();
			
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.example.client.restclient.controller"))
				.paths(regex("/api.*"))
				.build()
				.apiInfo(DEFAULT_API_INFO);
	}
	
}
