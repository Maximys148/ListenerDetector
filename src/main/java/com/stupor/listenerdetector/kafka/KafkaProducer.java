package com.stupor.listenerdetector.kafka;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service

public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger log = LogManager.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Object message) {
        log.debug("Отправка сообщения в топик {} | тело сообщения {}", topic , message);
        kafkaTemplate.send(topic,  message);
    }
}