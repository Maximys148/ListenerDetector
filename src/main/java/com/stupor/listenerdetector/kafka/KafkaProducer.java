/*
package com.stupor.listenerdetector.kafka;

import com.stupor.listenerdetector.dto.DeviceState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private final DeviceState deviceState;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger log = LogManager.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate, DeviceState deviceState) {
        this.kafkaTemplate = kafkaTemplate;
        this.deviceState = deviceState;
    }

    public void sendMessage(String topic, Object message) {
        log.debug("Отправка сообщения в топик {} | тело сообщения {}", topic , message);
        kafkaTemplate.send(topic,  message);
    }

    @Scheduled(fixedRate = 5000)
    public void sendDeviceStatesToKafka() {
        log.debug("Отправлено состояние устройства: {}", deviceState);
        kafkaTemplate.send("device", deviceState);
    }
}
*/
