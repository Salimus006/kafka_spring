package com.consumer.handl;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IReceiverHandler {

    void onMessage(ConsumerRecord record);
}
