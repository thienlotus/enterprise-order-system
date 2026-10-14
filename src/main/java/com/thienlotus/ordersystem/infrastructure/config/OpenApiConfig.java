package com.thienlotus.ordersystem.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Order & Inventory System API")
                        .version("1.0.0")
                        .description("High-throughput, concurrent order processing and real-time inventory reservation engine. " +
                                "Designed with Clean Architecture (DDD), Optimistic Locking, and Redis caching.")
                        .contact(new Contact()
                                .name("Lê Vũ Thiên")
                                .email("thienchip1310@gmail.com")
                                .url("https://github.com/thienlotus"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
