package com.consumer.listeners;

import com.consumer.handl.IReceiverHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

public class StringListener extends AbstractListener<String, String> {


    public StringListener(IReceiverHandler handler) {
        super(handler);
    }

    @KafkaListener(topics = "#{'${incoming.string.topic-name}'.split(',')}", /*id = "${incoming.string.consumer-id}",*/
            groupId = "${incoming.string.consumer-group}", containerFactory = "stringListenerContainerFactory")
    public void listen(ConsumerRecord<String, String> record) {
        handler.onMessage(record);
    }
}
