package com.consumer.listeners;

import com.consumer.handl.IReceiverHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public abstract class AbstractListener<T,D> {

    protected final IReceiverHandler handler;

    public AbstractListener(IReceiverHandler handler) {
        this.handler = handler;
    }

    abstract void listen(ConsumerRecord<T, D> record);

}
