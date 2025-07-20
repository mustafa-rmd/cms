package com.thmanyah.services.cms;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@OpenAPIDefinition(
    info =
        @Info(
            title = "Content Management System (CMS) service",
            description =
                "This application is used to manage shows and content for the CMS platform. "
                    + "It provides CRUD operations for shows including podcasts and documentaries.",
            license = @License(name = "Apache 2.0", url = "https://thmanyah.io"),
            contact =
                @Contact(
                    name = "Thmanyah Solutions",
                    email = "contact@thmanyah.io",
                    url = "https://thmanyah.io")))
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "JWT Bearer token authentication. Use the login endpoint to get a token.")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
@ComponentScan(basePackages = {"com.thmanyah.services.cms", "com.thmanyah.diagnostics"})
public class CmsApplication {

  public static void main(String[] args) {
    SpringApplication.run(CmsApplication.class, args);
  }
}
