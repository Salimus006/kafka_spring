package com.producer.init;

import com.producer.dto.UserRecord;
import com.producer.sender.AsynchronousProducer;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class InitEvents implements ApplicationListener<ContextRefreshedEvent> {

    private final ApplicationContext applicationContext;
    private enum MessageType {
        STRING,
        JSON,
        AVRO
    }
    private final String initData;
    private final String stringTopicName;
    private final String jsonTopicName;
    private final String avroTopicName;
    private final Integer messageCount;
    private final Integer sliceCount;
    private final Integer waitMillis;

    public InitEvents(ApplicationContext applicationContext, @Value("${init.data}") String initData,
                      @Value("${init.string.topic-name}") String stringTopicName,
                      @Value("${init.json.topic-name}") String jsonTopicName,
                      @Value("${init.avro.topic-name}") String avroTopicName,
                      @Value("#{new Integer('${init.size}')}") Integer messageCount,
                      @Value("#{new Integer('${init.slice}')}") Integer sliceCount,
                      @Value("#{new Integer('${init.waiting.time}')}") Integer waitMillis) {
        this.applicationContext = applicationContext;
        this.initData = initData;
        this.stringTopicName = stringTopicName;
        this.jsonTopicName = jsonTopicName;
        this.avroTopicName = avroTopicName;
        this.messageCount = messageCount;
        this.sliceCount = sliceCount;
        this.waitMillis = waitMillis;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if("true".equals(initData)) {
            // 1 publish string messages
            publish(MessageType.STRING);

            // publish json message
            publish(MessageType.JSON);

            // publish avro messages
            publish(MessageType.AVRO);

        }
    }

    private void publish(MessageType messageType) throws InterruptedException {
        // get required beans to publish messages
        AsynchronousProducer<String> stringProducer = (AsynchronousProducer<String>) this.applicationContext.getBean("stringProducer");
        AsynchronousProducer<Object> jsonProducer = (AsynchronousProducer<Object>) this.applicationContext.getBean("jsonProducer");
        AsynchronousProducer<Object> avroProducer = (AsynchronousProducer<Object>) this.applicationContext.getBean("avroProducer");
        // number of messages by slice
        int sliceSize = messageCount / sliceCount;

        Random random = new Random();
        String[] userNames = {"Davis", "Cous", "Dady", "Liliane", "Frank"};

        // init json message {"id":3,"owner":"root","pay":1300}
        Map<String, Object> jsonEvent = new HashMap<>();
        jsonEvent.put("owner", "root");

        // init avro message
        UserRecord user = UserRecord.newBuilder()
                .setFirstName("")
                .setSize(1)
                .build();

        for (int i = 0; i < sliceCount; i++) {
            // iterate
            for (int b = 0; b < sliceSize; b++ ) {
                String eventKey = "key-" + i;

                // publish event
                switch (messageType) {
                    case STRING -> stringProducer.send(stringTopicName, eventKey,  "string message");
                    case JSON -> {
                        jsonEvent.put("id", i);
                        // random range random.nextInt((end - start) + 1) + start)
                        jsonEvent.put("pay", random.nextInt((3000 - 1300) + 1) + 1300);
                        jsonProducer.send(jsonTopicName, eventKey, jsonEvent);
                    } case AVRO -> {
                        user.setFirstName(userNames[random.nextInt(userNames.length)]);
                        user.setSize(random.nextInt((200 - 140) + 1) + 140);
                        avroProducer.send(avroTopicName, eventKey, user);
                    } default -> System.out.println("not implemented");
                }
            }

            if (sliceCount > 1 && i < (sliceCount-1)) {
                System.out.println("-------------- waiting before publish next slice !!!! -------------");
                Thread.sleep(waitMillis);
            }
        }

        System.out.println("--------------------------- End publication ---------------------------------");
    }
}
