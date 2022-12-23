package com.will.kafkareplaydemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.will.kafkareplaydemo.model.ExampleEntity;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@RestController
public class KafkaBrokerController {

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping(path = "/entity")
    public void publishEntity(@RequestBody ExampleEntity exampleEntity) throws IOException {
        kafkaTemplate.send(topic, exampleEntity);
    }

    @GetMapping(path = "/replay-all")
    public List<ExampleEntity> listenGroupMessage() throws JsonProcessingException, InterruptedException {
        // set properties for our new consumer that listens to our exiting topic
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:29092");
        props.setProperty("group.id", "replay-group");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("auto.offset.reset", "earliest");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        TopicPartition t = new TopicPartition("exampleTopic", 0);
        List <TopicPartition> topics = Arrays.asList(t);
        consumer.assign(topics);
        consumer.seek(t, 0L);
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

        List <ExampleEntity> allExampleEntities = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records) {
                // ObjectMapper instantiation
                ObjectMapper objectMapper = new ObjectMapper();
                // Handle LocalDate parsing
                objectMapper.registerModule(new JavaTimeModule());
                // Deserialization into the `ExampleEntity` class
                ExampleEntity exampleEntity = objectMapper.readValue(record.value(), ExampleEntity.class);
                // Print information
                allExampleEntities.add(exampleEntity);
            }
            Collections.sort(allExampleEntities);
            return allExampleEntities;
        }

    // TODO: 23/12/2022 investigate Kafka stream to map to objects and sort instead
    // https://developer.confluent.io/tutorials/creating-first-apache-kafka-streams-application/confluent.html
    // https://www.baeldung.com/spring-boot-kafka-streams
    static Topology buildTopology(String inputTopic, String outputTopic) {
        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder builder = new StreamsBuilder();
        builder
                .stream(inputTopic, Consumed.with(stringSerde, stringSerde))
                .peek((k,v) -> System.out.println("Observed event: {} " + v))
                .mapValues(s -> s.toUpperCase())
                .peek((k,v) -> System.out.println("Transformed event: {} " + v))
                .to(outputTopic, Produced.with(stringSerde, stringSerde));
        return builder.build();
    }
}

