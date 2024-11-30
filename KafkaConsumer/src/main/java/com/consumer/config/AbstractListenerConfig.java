package com.consumer.config;

import com.consumer.dto.JsonEvent;
import com.producer.dto.UserRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractListenerConfig {
    protected String servers;
    protected String schemaRegistryUrl;

    public AbstractListenerConfig(String servers, String schemaRegistryUrl) {
        this.servers = servers;
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    protected ConsumerFactory<String, String> stringConsumerFactory() {
        Map<String, Object> props = defaultProps();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    public ConsumerFactory<String, JsonEvent> jsonConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(defaultProps(), new StringDeserializer(), new JsonDeserializer<>(JsonEvent.class));
    }

    protected ConsumerFactory<String, UserRecord> avroConsumerFactory() {
        Map<String, Object> props = defaultProps();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    protected Map<String, Object> defaultProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // earliest
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
}
