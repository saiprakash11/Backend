package com.ems.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI emsOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("EMS Unified API")
                        .description("Employee Management System – all services consolidated into one backend on port 8080.\n\n" +
                                "Services included:\n" +
                                "- **Auth** (`/api/auth/**`) – login, logout, change-password\n" +
                                "- **Employee** (`/api/employees/**`, `/api/employee-profiles/**`) – dashboard, attendance, leave, profile\n" +
                                "- **HR** (`/api/hr/**`, `/api/attendance/**`, `/api/leave/**`, `/api/payroll/**`, etc.)\n" +
                                "- **Management** (`/api/projects/**`, `/api/meetings/**`, `/api/approvals/**`, `/api/performance-reviews/**`)")
                        .version("1.0.0")
                        .contact(new Contact().name("EMS Team")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
