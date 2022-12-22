package com.will.kafkareplaydemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.will.kafkareplaydemo.enums.Days;
import com.will.kafkareplaydemo.model.ExampleEntity;
import com.will.kafkareplaydemo.utils.JsonUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
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
    public String listenGroupMessage() throws JsonProcessingException {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:29092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        TopicPartition tp = new TopicPartition("exampleTopic", 0);
        List<TopicPartition> topics = Arrays.asList(tp);
        consumer.assign(topics);
        consumer.seek(tp, 0L);

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
            for (ExampleEntity e : allExampleEntities){
                System.out.println(e.toString());
            }
            return null;
        }
}
