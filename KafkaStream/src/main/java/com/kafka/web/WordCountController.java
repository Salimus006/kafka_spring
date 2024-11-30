package com.kafka.web;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WordCountController {

    private final StreamsBuilderFactoryBean factoryBean;
    private final String storeName;

    public WordCountController(StreamsBuilderFactoryBean factoryBean,
                               @Value(value = "${stream.intermediate.store-name}") String storeName) {
        this.factoryBean = factoryBean;
        this.storeName = storeName;
    }

    @GetMapping("/count/{word}")
    public Long getWordCount(@PathVariable String word) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore())
        );
        return counts.get(word);
    }

    @GetMapping("/count")
    public KeyValueIterator<String, Long> getWordsCount() {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, Long> counts = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore())
        );
        return counts.all();
    }
}
