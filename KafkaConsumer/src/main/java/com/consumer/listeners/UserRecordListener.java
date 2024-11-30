package com.consumer.listeners;

import com.consumer.handl.IReceiverHandler;
import com.producer.dto.UserRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

public class UserRecordListener extends AbstractListener<String, UserRecord> {


    public UserRecordListener(IReceiverHandler handler) {
        super(handler);
    }

    @KafkaListener(topics = "#{'${incoming.avro.topic-name}'.split(',')}", /*id = "${incoming.avro.consumer-id}",*/
            groupId = "${incoming.string.consumer-group}", containerFactory = "avroListenerContainerFactory")
    public void listen(ConsumerRecord<String, UserRecord> record) {
        handler.onMessage(record);
    }
}
