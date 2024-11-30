package com.producer.sender;

import com.producer.handl.ISenderHandler;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * class that allows to send messages asynchronously
 *
 * @param <V>
 */
public class AsynchronousProducer<V> {

    private final KafkaTemplate<String, V> kafkaTemplate;

    private final ISenderHandler handler;

    public AsynchronousProducer(KafkaTemplate<String, V> kafkaTemplate, ISenderHandler handler) {
        this.kafkaTemplate = kafkaTemplate;
        this.handler = handler;
    }

    public void send(String topic, String key, V value) {
        CompletableFuture<SendResult<String, V>> future = kafkaTemplate.send(topic, key, value);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata sendResult = result.getRecordMetadata();
                handler.onSuccess(sendResult.topic(), key, sendResult.partition(), sendResult.offset());
            } else {
                handler.onError(ex, topic, key);
            }
        });
    }
}
