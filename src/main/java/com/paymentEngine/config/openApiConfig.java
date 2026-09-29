package com.paymentEngine.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Payment Processing & Fraud Detection API",
                version = "1.0",
                description = """
                        High-Throughput Payment Processing and
                        Fraud Detection Engine.

                        Features:
                        - Payment processing
                        - Kafka event streaming
                        - Rule-based fraud detection
                        - AI-based fraud analysis
                        - Redis caching
                        - Payment settlement
                        - JWT authentication
                        """
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)

public class openApiConfig {


}
