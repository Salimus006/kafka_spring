package com.consumer.listeners;

import com.consumer.dto.JsonEvent;
import com.consumer.handl.IReceiverHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

public class JsonListener extends AbstractListener<String, JsonEvent> {

    public JsonListener(IReceiverHandler handler) {
        super(handler);
    }

    @KafkaListener(topics = "#{'${incoming.json.topic-name}'.split(',')}", /*id ="${incoming.json.consumer-id}",*/
            groupId = "${incoming.json.consumer-group}", containerFactory = "jsonListenerContainerFactory")
    public void listen(ConsumerRecord<String, JsonEvent> record) {
        handler.onMessage(record);
    }
}
