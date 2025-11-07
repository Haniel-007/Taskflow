package com.taskflow.taskflow.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "TaskFlow Project Management API",
        version = "v1.0",
        description = "Documentação completa da API de Gerenciamento de Tarefas (TaskFlow), incluindo autenticação JWT.",
        contact = @Contact(
            name = "Mylle", // Altere se desejar
            email = "mylle@taskflow.com" // Altere se desejar
        ),
        license = @License(name = "MIT License")
    ),
    // Define que todas as rotas (exceto as públicas) requerem o esquema de segurança "BearerAuth"
    security = @SecurityRequirement(name = "BearerAuth") 
)
@SecurityScheme(
    name = "BearerAuth", // Nome do esquema de segurança. Usado no @SecurityRequirement acima.
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer", // Define o formato "Bearer <token>"
    description = "Insira o token JWT no campo de valor, no formato: Bearer <seu-token-aqui>"
)
public class OpenApiConfig {
    // Esta classe não precisa de métodos, ela funciona puramente através de suas anotações.
}