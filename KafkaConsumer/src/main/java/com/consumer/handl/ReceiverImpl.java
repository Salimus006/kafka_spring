package com.consumer.handl;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ReceiverImpl implements IReceiverHandler {
    @Override
    public void onMessage(ConsumerRecord record) {
        System.out.println("Topic in: " + record.topic() + ">>> Received Message [" + record.value() + "] key [" +record.key()+ "] offset ["
                + record.offset()+"] partition [" + record.partition() +"] headers [" + record.headers()+"]");
    }
}
