package com.producer.handl;

public class SenderImpl implements ISenderHandler{
    @Override
    public void onSuccess(String topic, String key, Integer partition, Long offset) {
        System.out.println("Sent message topic [" + topic + "] key=[" + key + "] partition [" + partition + "] with offset=[" + offset + "]");
    }

    @Override
    public void onError(Throwable ex, String topic, String key) {
        System.out.println("Unable to send message with key =[" +
                key + "] due to : " + ex.getMessage());
    }
}
