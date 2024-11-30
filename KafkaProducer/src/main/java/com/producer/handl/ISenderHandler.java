package com.producer.handl;

public interface ISenderHandler {

    void onSuccess(String topic, String key, Integer partition, Long offset);

    void onError(Throwable ex, String topic, String key);
}
