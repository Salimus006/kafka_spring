package com.producer.config;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientRegistryConfig {

    @Value("${spring.kafka.producer.properties.schema.registry.url}")
    private String schemaRegistryUrl;
    @Bean
    public CachedSchemaRegistryClient schemaRegistryClient() {
        return new CachedSchemaRegistryClient(schemaRegistryUrl, 100);
    }

    @Bean
    public RestTemplate registryRestTemplate() {
        return new RestTemplate();
    }
}
