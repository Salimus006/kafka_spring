package com.producer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                        addList("Kafka producers application"))
                .components(new Components())
                .info(new Info().title("Kafka producers API")
                        .description("Demo to produce a 'String/Json/Avro' message in a kafka topics")
                        .contact(new Contact().name("Salim B")
                                .email( "salimboulakhlas@gmail.com")));
    }
}
