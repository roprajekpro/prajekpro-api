package com.prajekpro.api.config;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("pp-api")
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.prajekpro.api.controllers"))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        "Rest API for Prajek Pro Customer Mobile App API's",
        "API for use of Customer Mobile App of PrajekPro",
        "1.0",
        "Terms of service",
        new Contact("Safalyatech IT team", "www.safalyatech.com", "piyush.jadhav@safalyatech.com"),
        "License of API",
        "https://www.safalyatech.com",
        Collections.emptyList());
  }
}
