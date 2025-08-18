package com.stupor.listenerdetector.services;

import Scanner.Net.Api.Common.Time.LinuxTimeWithMs;
import Scanner.Net.Api.Connectors.Notifiers;
import Scanner.Net.Api.Data.Jobs;
import Scanner.Net.Api.Data.Notifies;
import Scanner.Net.Api.Data.Notifies.PacketJobsNotifies;
import Scanner.Net.Api.Data.SignalsInfo;
import Scanner.Net.Api.Packet.ApiPacket;
import Scanner.Net.Api.Packet.ApiTypes;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stupor.listenerdetector.client.DetectorClient;
import com.stupor.listenerdetector.dto.DeviceState;
import com.stupor.listenerdetector.dto.Location;
import com.stupor.listenerdetector.dto.SignalDto;
import com.stupor.listenerdetector.dto.enums.DeviceType;
import com.stupor.listenerdetector.exceptions.DetectorException;
//import com.stupor.listenerdetector.kafka.KafkaProducer;
import com.stupor.listenerdetector.kafka.KafkaProducer;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class MessageService {
    private final Map<String, JobInfo> activeSubscriptions = new ConcurrentHashMap<>();
    private KafkaProducer kafkaProducer;
    @Setter
    private WebSocketClient webSocketClient;
    private final Logger log = LogManager.getLogger(MessageService.class);

    public MessageService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    // Получает сообщение от форватора, определяет его тип и перенаправляет в нужный обработчик
    public void processRawMessage(byte[] data) throws InvalidProtocolBufferException {
        ApiPacket.Message message = ApiPacket.Message.parseFrom(data);

        switch (message.getType()) {
            case DATA_LOADED_JOBS_LIST:
                processJobsList(message.getData().toByteArray());
                break;
            case DATA_NOTIFIES:
                processNotifies(message.getData().toByteArray());
                break;
            case COMMON_VERSION:
                log.info("Получена информация о версии протокола");
                break;
            default:
                log.warn("Неизвестный тип сообщения: {}", message.getType());
        }
    }

    private void processJobsList(byte[] data) throws InvalidProtocolBufferException {
        log.info("Полученное тело: {}", new String(data));
        Jobs.PacketLoadedJobsList jobsList = Jobs.PacketLoadedJobsList.parseFrom(data);
        jobsList.getJobsList().forEach(job -> {
            if (!activeSubscriptions.containsKey(job.getUid())) {
                log.info("Получено задание {}", job.getUid());
                subscribeToJob(job.getUid());
                activeSubscriptions.put(job.getUid(),
                        new JobInfo(job.getUid(), job.getDevice().getDeviceName(), job.getParams().getCfMHz()));
            }
        });
    }

    private void processNotifies(byte[] data) throws InvalidProtocolBufferException {
        PacketJobsNotifies notifies = PacketJobsNotifies.parseFrom(data);
        log.info("Пакет получен: {}", notifies);
        notifies.getJobsNotifiesList().forEach(jobNotify -> {
            jobNotify.getNotifiesList().forEach(notify -> {
                if (notify.hasJobSignalsWithMaxLevelChanged()) {
                    processMaxLevelSignal(
                        jobNotify.getJobUid(),
                        notify.getJobSignalsWithMaxLevelChanged()
                    );
                    log.info("Нотификация обработана: {}", jobNotify.getJobUid());
                }
            });
        });
    }

    private void processMaxLevelSignal(String jobUid, Notifies.JobSignalsWithMaxLevelChanged signal) {
        JobInfo job = activeSubscriptions.get(jobUid);
        log.info("Проверяем максимальный сигнал: {}", job.toString());
        if (job == null) return;

        SignalDto signalDto = convertToDto(signal);
        kafkaProducer.sendMessage("ocusyncs", signalDto);
        log.info("Обработанный сигнал: {}", signalDto);
    }

    /*
     Оформляется "подписка" на задание, т.е. слушаем определённую частоту на изменение или появление новых данных (которые находятся в src/main/proto),
     после осуществлении подписки мы будем получать данные с этой частоты, подробнее про типы данных можно прочитать в документации, прикрепленной в .README этого проекта
    */
    private void subscribeToJob(String jobUid) {
        // 1. Создаем подписку
        Notifiers.PacketRegisterJobNotifier subscription = Notifiers.PacketRegisterJobNotifier.newBuilder()
                .addJobsNotifiers(Notifiers.Notifier.newBuilder()
                        .setJobUid(jobUid)
                        .addTypes(Notifiers.Type.JOB_SIGNALS_WITH_MAX_LEVEL_CHANGED)
                        .addTypes(Notifiers.Type.JOB_SPECTRUM_CHANGED)
                        .build())
                .build();

        // 2. Упаковываем в ApiPacket.Message
        ApiPacket.Message message = ApiPacket.Message.newBuilder()
                .setType(ApiTypes.Types.CONNECTORS_REGISTER_JOB_NOTIFIER)
                .setData(subscription.toByteString())
                .build();

        // 3. Отправляем сериализованное сообщение
        sendMessage(message.toByteArray());
        log.info("Оформлена подписка на задание: {}", jobUid);
    }

    private SignalDto convertToDto(Notifies.JobSignalsWithMaxLevelChanged signal) {
        log.info("Начата конвертация сигнала");
        LinuxTimeWithMs time = signal.getNotifiedAt();
        double cfMHz = signal.getSignalInfo().getActiveSignal().getCfMHz();
        SignalDto dto = new SignalDto();
        dto.setDeviceImei("shtilFarvater-1");
        dto.setFrequency(String.format("%.2f MHz", cfMHz));
        dto.setTimestamp(time.getSec() * 1000 + time.getMs());
        dto.setMaxSignalLevel((double) signal.getSignalInfo().getActiveSignal().getLevelDb());
        dto.setPrivateId("signal " + String.format("%.2f MHz", cfMHz));
        dto.setPublicId("signal " + String.format("%.2f MHz", cfMHz));
        dto.setDirection(signal.getSignalInfo().getAntennaInfo().getDirectionName());
        log.info("Закончили конвертацию сигнала");
        return dto;
    }

    // Отправка сообщения по WebSocket
    public void sendMessage(byte[] data) {
        log.info("Отправляемое тело: {}", new String(data));
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(data);
            log.info("Сообщение отправлено {}", data);
        } else {
            log.error("Не удалось отправить сообщение - соединение не активно");
            throw new DetectorException("WebSocket соединение не установлено");
        }
    }

    @Data
    @AllArgsConstructor
    private static class JobInfo {
        private String uid;
        private String deviceName;
        private double frequency;
    }
}