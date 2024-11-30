package com.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class Produce {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final List<String> months = List.of("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
    private final List<String> days = List.of("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday");

    public Produce(KafkaTemplate<String, String> kafkaTemplate, @Value(value = "${stream.input.topic-name}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;

        startProducingMessages();
    }

    public void startProducingMessages() {

        // chaque 2 seconde on envoie un message (Le message est une concaténation d'un mois tiré auhasard de la liste + deux jours de la semaine tirés au hasard aussi)
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            String messageValue = RandomText.randomFromList(months) + " " +  RandomText.randomFromList(days) + " " + RandomText.randomFromList(days);
            String key = RandomText.generateRandomWord();

            kafkaTemplate.send(topic, key, messageValue).whenComplete((result, ex) -> {
                if (ex == null) {
                    RecordMetadata sendResult = result.getRecordMetadata();
                    System.out.println("Sent message [" + messageValue + "] topic [" + topic + "] key=[" + key + "] partition " +
                            "[" + sendResult.partition() + "] with offset=[" + sendResult.offset() + "]");

                } else {
                    System.out.println("Unable to send message with key =[" +
                            key + "] due to : " + ex.getMessage());
                }
            });
        }, 1000, 2000, TimeUnit.MILLISECONDS);
    }

    public static class RandomText {

        static int leftLimit = 97; // letter 'a'
        static int rightLimit = 122; // letter 'z'
        static int targetStringLength = 10;
        static Random random = new Random();

        public static String randomFromList(List<String> words) {
            return words.get(random.nextInt(words.size()));
        }

        public static String generateRandomText(int length){
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < length; i++) {
                text.append(generateRandomWord());
                if (i != (length-1)) {
                    text.append(" ");
                }
            }
            return text.toString();
        }

        public static String generateRandomWord() {

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            System.out.println(generatedString);

            return generatedString;
        }

    }
}
