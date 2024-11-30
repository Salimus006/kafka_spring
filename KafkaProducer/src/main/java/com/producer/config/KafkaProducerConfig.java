package com.producer.config;

import com.producer.handl.ISenderHandler;
import com.producer.handl.SenderImpl;
import com.producer.sender.AsynchronousProducer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;

    @Value("${spring.kafka.producer.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    /**
     * Ues bean to handle the the sending message (success or error)
     * @return
     */
    @Bean
    ISenderHandler createHandler(){
        return new SenderImpl();
    }

    /**
     * Bean to produce string messages
     *
     * @return AsynchronousProducer
     */
    @Bean("stringProducer")
    public AsynchronousProducer<String> stringProducer() {
        return new AsynchronousProducer<>(stringKafkaTemplate(), createHandler());
    }

    /**
     * Bean to produce json messages
     *
     * @return AsynchronousProducer
     */
    @Bean("jsonProducer")
    public AsynchronousProducer<Object> jsonProducer() {
        return new AsynchronousProducer<>(jsonKafkaTemplate(), createHandler());
    }

    /**
     * Bean to produce avro messages
     *
     * @return
     */
    @Bean("avroProducer")
    public AsynchronousProducer<Object> avroProducer() {
        return new AsynchronousProducer<>(avroKafkaTemplate(), createHandler());
    }

    /**
     * Common kafka producer props (like kafka brokers address, class to use to serialize message and key)
     *
     * @return
     */
    private Map<String, Object> defaultProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        // StringSerializer is used by default to serialize key and record
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return configProps;
    }

    /**
     * Build a kafka template for string producer
     *
     * @return KafkaTemplate
     */
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        // build default props
        Map<String, Object> configProps = defaultProps();
        // 2 set message serialization class
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

    /**
     * Build a kafka template for json producer
     *
     * @return KafkaTemplate
     */
    public KafkaTemplate<String, Object> jsonKafkaTemplate() {
        // build default props
        Map<String, Object> configProps = defaultProps();
        // use JsonSerializer class to serialize a record
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

    /**
     * Build a kafka template for avro producer
     *
     * @return KafkaTemplate
     */
    public KafkaTemplate<String, Object> avroKafkaTemplate() {
        // build default props
        Map<String, Object> configProps = defaultProps();
        // use KafkaAvroSerializer class to serialize a record
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        configProps.put("schema.registry.url", schemaRegistryUrl);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }
}
