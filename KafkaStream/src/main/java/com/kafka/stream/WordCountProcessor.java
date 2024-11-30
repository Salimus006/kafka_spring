package com.kafka.stream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WordCountProcessor {

    private static final Serde<String> STRING_SERDE = Serdes.String();
    private final String incomingTopic;
    private final String outGoingTopic;
    private final String storeName;

    public WordCountProcessor(@Value(value = "${stream.input.topic-name}") String incomingTopic,
                              @Value(value = "${stream.output.topic-name}") String outGoingTopic,
                              @Value(value = "${stream.intermediate.store-name}") String storeName) {

        this.incomingTopic = incomingTopic;
        this.outGoingTopic = outGoingTopic;
        this.storeName = storeName;
    }

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, String> messageStream = streamsBuilder
                .stream(incomingTopic, Consumed.with(STRING_SERDE, STRING_SERDE));

        // On créé une KTable dans laquelle on stocke le nombre d'occurence de chaque mot
        KTable<String, Long> wordCounts = messageStream
                .mapValues(value -> value.toLowerCase())
                //.mapValues((ValueMapper<? super String, ?>) <String, String>) String::toLowerCase)
                .flatMapValues(value -> Arrays.asList(value.split("\\W+"))) // on récupère la liste des mots
                .groupBy((key, word) -> word)
                .count(Materialized.as(storeName)); // je stocke le résultat dans un store

        // Envoi vers un topic en sortie
        wordCounts.toStream().to(outGoingTopic);
    }
}
