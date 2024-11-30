package com.producer.registry;

import com.producer.errors.SchemaRegistryException;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.OptionalInt;

@Service
public class RegistryService {

    @Value("${spring.kafka.producer.properties.schema.registry.url}")
    private String schemaRegistryUrl;
    private final CachedSchemaRegistryClient cachedSchemaRegistryClient;
    private final RestTemplate registryRestTemplate;

    public RegistryService(CachedSchemaRegistryClient cachedSchemaRegistryClient, RestTemplate registryRestTemplate) {
        this.cachedSchemaRegistryClient = cachedSchemaRegistryClient;
        this.registryRestTemplate = registryRestTemplate;
    }

    /**
     * Try to retrieve avro schema from the registry cache first. And if the schema doesn't exist in the cache a call is
     * made to schema registry
     *
     * @param topicName the subject
     * @return avro schema as string
     */
    public String getLastSchemaAsString(String topicName) {
        try {
            // First we will try to get the schema from the cache
            SchemaMetadata schemaMetadata = this.cachedSchemaRegistryClient.getLatestSchemaMetadata(topicName+"-value");
            if (schemaMetadata != null) {
                return schemaMetadata.getSchema();
            }

            // else we make a call to schema registry
            // url to get subject versions : baseUrl/subjects/subjectName-value/versions / response [1,2]
            Integer[] subjectVersions = this.registryRestTemplate.getForEntity(schemaRegistryUrl  + "/subjects/" +
                    topicName+"-value/versions/", Integer[].class).getBody();
            // get last subject schema
            // url to get the last schema baseUrl/subjects/subjectName-value/versions/lastVersion
            assert subjectVersions != null;
            OptionalInt lastVersion = Arrays.stream(subjectVersions).mapToInt(Integer::intValue).max();
            String lastSchemaUrl = this.schemaRegistryUrl + "/subjects/" + topicName+"-value/versions/" + lastVersion.getAsInt();
            SchemaRegistryResponse response = this.registryRestTemplate.getForEntity(lastSchemaUrl, SchemaRegistryResponse.class)
                    .getBody();

            return response.schema();
        } catch (Exception e) {
            throw new SchemaRegistryException(e.getMessage());
        }
    }
}

record SchemaRegistryResponse(String subject, int version, int id, String schema) {}
