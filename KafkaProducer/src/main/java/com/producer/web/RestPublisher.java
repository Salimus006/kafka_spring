package com.producer.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.producer.dto.UserRecord;
import com.producer.registry.RegistryService;
import com.producer.sender.AsynchronousProducer;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/send")
public class RestPublisher {
    private final AsynchronousProducer<String> stringProducer;
    private final AsynchronousProducer<Object> jsonProducer;
    private final AsynchronousProducer<Object> avroProducer;
    private final RegistryService registryService;

    public RestPublisher(@Qualifier("jsonProducer") AsynchronousProducer<Object> jsonProducer,
                         @Qualifier("stringProducer") AsynchronousProducer<String> stringProducer,
                         @Qualifier("avroProducer") AsynchronousProducer<Object> avroProducer,
                         RegistryService registryService) {
        this.jsonProducer = jsonProducer;
        this.avroProducer = avroProducer;
        this.registryService = registryService;
        this.stringProducer = stringProducer;
    }

    /**
     * /api/send/string/{topic}/{key}
     * Endpoint to send a string message with it's key in a specified topic
     *
     * @param topic in which the message will be sent
     * @param key to use
     * @param message to send
     *
     * @return
     */
    @PostMapping("/string/{topic}/{key}")
    @Operation(summary = "Publish a string message")
    public ResponseEntity<String> sendStringMessage(@PathVariable String topic,
                                                    @PathVariable String key, @RequestBody String message) {
        this.stringProducer.send(topic, key, message);
        return ResponseEntity.ok("OK");
    }


    /**
     * /api/send/json/{topic}/{key}
     * Endpoint to send a json message with its key in a specified topic
     *
     * @param topic target topic
     * @param key event key
     * @param message record to publish
     * @return
     */
    @PostMapping("/json/{topic}/{key}")
    @Operation(summary = "Publish a json message")
    public ResponseEntity<String> sendJsonMessage(@PathVariable String topic, @PathVariable String key,
                                          @RequestBody JsonNode message) {
        this.jsonProducer.send(topic, key, message);
        return ResponseEntity.ok("OK");
    }

    /**
     * /api/send/avro/user/{topic}/{key}
     * To publish a UserRecord into a specified topic
     *
     * @param topic target topic
     * @param key key
     * @param user record (message)
     * @return ResponseEntity
     */
    @PostMapping("/avro/user/{topic}/{key}")
    @Operation(summary = "Publish a UserRecord message")
    public ResponseEntity<String> sendUserMessage(@PathVariable String topic, @PathVariable String key,
    @RequestBody UserRecord user) {
        this.avroProducer.send(topic, key, user);
        return ResponseEntity.ok("OK");
    }

    /**
     * /api/send/avro/generic/{topic}/{key
     * Generic endpoint to publish an avro message
     * Nb : the topic must already have a schema in the schema registry
     *
     * @param topic target topic
     * @param key key
     * @param record record (message as jsonNode)
     * @return ResponseEntity
     * @throws IOException
     */
    @PostMapping("/avro/generic/{topic}/{key}")
    @Operation(summary = "Publish a avro message (topic's avro schema must already exists in schema registry)")
    public ResponseEntity<String> sendAvroMessage(@PathVariable String topic, @PathVariable String key,
                                                  @RequestBody JsonNode record) throws IOException {

        // retrieve the topic's avro schema from schema registry
        String subjectSchema = this.registryService.getLastSchemaAsString(topic);

        // serialize the record with the retrieved avro schema and publish it into the target topic
        this.avroProducer.send(topic, key, AvroSchemaUtils.toObject(record, new AvroSchema(subjectSchema)));
        return ResponseEntity.ok("OK");
    }
}
