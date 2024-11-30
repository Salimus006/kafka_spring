package com.consumer.config;

import com.consumer.dto.JsonEvent;
import com.consumer.handl.ReceiverImpl;
import com.consumer.listeners.JsonListener;
import com.consumer.listeners.StringListener;
import com.consumer.listeners.UserRecordListener;
import com.producer.dto.UserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@EnableKafka
@Configuration
public class KafkaListenersConfig extends AbstractListenerConfig {

    public KafkaListenersConfig(@Value("${spring.kafka.bootstrap-servers}") String servers,
                                @Value("${spring.kafka.consumer.properties.schema.registry.url}") String schemaRegistryUrl) {
        super(servers, schemaRegistryUrl);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stringConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JsonEvent> jsonListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, JsonEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(jsonConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRecord> avroListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserRecord> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(avroConsumerFactory());
        return factory;
    }

    @Bean
    public StringListener stringListener() {
        return new StringListener(new ReceiverImpl());
    }

    @Bean
    public JsonListener jsonListener() {
        return new JsonListener(new ReceiverImpl());
    }

    @Bean
    public UserRecordListener userRecordListener() {
        return new UserRecordListener(new ReceiverImpl());
    }
}
