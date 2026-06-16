package com.employee.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger config for employee-service.
 * Aggregated at gateway: GET /v3/api-docs/employee-service
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("EMS Employee Service API")
                .description(
                    "Employee self-service endpoints:\n" +
                    "- GET  /api/employees/{code}/dashboard\n" +
                    "- GET  /api/employees/{code}/attendance\n" +
                    "- GET  /api/employees/{code}/leave\n" +
                    "- GET  /api/employees/{code}/salary\n" +
                    "- GET  /api/employees/{code}/performance\n" +
                    "- GET  /api/employees/{code}/documents\n" +
                    "- GET  /api/employees/{code}/notifications\n" +
                    "- GET/POST /api/employee-profiles/{code}"
                )
                .version("1.0.0")
                .contact(new Contact().name("EMS Team").email("dev@company.com"))
            )
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            );
    }
}
