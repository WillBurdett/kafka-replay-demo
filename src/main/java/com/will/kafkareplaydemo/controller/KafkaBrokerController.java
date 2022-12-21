package com.will.kafkareplaydemo.controller;

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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@RestController
public class KafkaBrokerController {

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping(path = "/publish/{message}")
    public void publishMessage(@PathVariable String message){
        kafkaTemplate.send(topic, message);
    }

    @GetMapping(path = "/entity")
    public ExampleEntity getEntity(){
        return new ExampleEntity(List.of(Days.MONDAY));
    }
    @PostMapping(path = "/entity")
    public void publishEntity(@RequestBody ExampleEntity exampleEntity) throws IOException {
        kafkaTemplate.send(topic, exampleEntity);
    }

    @GetMapping(path = "/replay-all")
    public String listenGroupMessage() {
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
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record);
            }
            return null;
        }
}
