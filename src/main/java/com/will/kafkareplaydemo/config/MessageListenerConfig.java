package com.will.kafkareplaydemo.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListenerConfig {

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.groupId}", containerFactory = "kafkaListenerContainerFactory")
    public void listenGroupMessage(final Object message) {
        System.out.println(message);
    }
}
