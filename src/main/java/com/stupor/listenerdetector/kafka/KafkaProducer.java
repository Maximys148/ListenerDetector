package com.stupor.listenerdetector.kafka;

import com.stupor.listenerdetector.dto.DeviceState;
import com.stupor.listenerdetector.dto.SignalDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class KafkaProducer {
    private final DeviceState deviceState;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger log = LogManager.getLogger(KafkaProducer.class);
    private final List<String> directions = List.of("С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ");

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
        log.info("Отправлено состояние устройства: {}", deviceState);
        kafkaTemplate.send("devices", deviceState);
    }

    @Scheduled(fixedRate = 5000)
    public void sendSignalToKafka() {
        SignalDto signalDto = generateRandomSignal();
        log.info("Отправка сигнала: {}", generateRandomSignal());
        kafkaTemplate.send("ocusyncs", signalDto);
    }

    private SignalDto generateRandomSignal() {
        SignalDto signalDto = new SignalDto();
        int rndFreq = new Random().nextInt(500) + 500;
        signalDto.setPublicId("Сигнал-" + rndFreq + "MHz");
        signalDto.setPrivateId("signal-" + rndFreq + "MHz");
        signalDto.setDeviceImei("shtilFarvater-1");
        signalDto.setFrequency(rndFreq + "");
        signalDto.setMaxSignalLevel((double) (new Random().nextInt(1000) + 500));
        signalDto.setTimestamp(System.currentTimeMillis());
        signalDto.setDirection(directions.get(new Random().nextInt(directions.size())));
        return signalDto;
    }
}
