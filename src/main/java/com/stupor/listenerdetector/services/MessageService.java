package com.stupor.listenerdetector.services;

import Scanner.Net.Api.Common.Time.LinuxTimeWithMs;
import Scanner.Net.Api.Connectors.Notifiers;
import Scanner.Net.Api.Data.Jobs;
import Scanner.Net.Api.Data.Notifies;
import Scanner.Net.Api.Data.Notifies.PacketJobsNotifies;
import Scanner.Net.Api.Packet.ApiPacket;
import Scanner.Net.Api.Packet.ApiTypes;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stupor.listenerdetector.client.DetectorClient;
import com.stupor.listenerdetector.dto.DeviceState;
import com.stupor.listenerdetector.dto.Location;
import com.stupor.listenerdetector.dto.SignalDto;
import com.stupor.listenerdetector.dto.enums.DeviceType;
import com.stupor.listenerdetector.kafka.KafkaProducer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final Map<String, JobInfo> activeSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, JobInfo> jobCache = new HashMap<>();
    private DeviceState deviceState;
    private KafkaProducer kafkaProducer;
    private DetectorClient client;
    private final Logger log = LogManager.getLogger(MessageService.class);

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

        notifies.getJobsNotifiesList().forEach(jobNotify -> {
            jobNotify.getNotifiesList().forEach(notify -> {
                if (notify.hasJobSignalsWithMaxLevelChanged()) {
                    processMaxLevelSignal(
                        jobNotify.getJobUid(),
                        notify.getJobSignalsWithMaxLevelChanged()
                    );
                }
            });
        });
    }

    private void processMaxLevelSignal(String jobUid, Notifies.JobSignalsWithMaxLevelChanged signal) {
        JobInfo job = jobCache.get(jobUid);
        if (job == null) return;

        SignalDto signalDto = convertToDto(jobUid, signal);

        kafkaProducer.sendMessage("signal", signalDto);

        log.info("Обработанный сигнал: {}", signalDto);
    }

    private void subscribeToJob(String jobUid) {
        Notifiers.PacketRegisterJobNotifier subscription = Notifiers.PacketRegisterJobNotifier.newBuilder()
                .addJobsNotifiers(Notifiers.Notifier.newBuilder()
                        .setJobUid(jobUid)
                        .addTypes(Notifiers.Type.JOB_SIGNALS_WITH_MAX_LEVEL_CHANGED)
                        .addTypes(Notifiers.Type.JOB_SPECTRUM_CHANGED)
                        .build())
                .build();

        client.sendMessage(subscription.toByteArray());
        log.info("Оформлена подписка на задание: {}", jobUid);
    }

    private SignalDto convertToDto(String jobUid, Notifies.JobSignalsWithMaxLevelChanged signal) {
        JobInfo job = activeSubscriptions.get(jobUid);
        LinuxTimeWithMs time = signal.getNotifiedAt();
        SignalDto dto = new SignalDto();
        dto.setDeviceImei(job.getDeviceName());
        dto.setFrequency(String.format("%.2f MHz", job.getFrequency()));
        dto.setTimestamp(time.getSec() * 1000 + time.getMs());
        dto.setMaxSignalLevel((double) signal.getSignalInfo().getActiveSignal().getLevelDb());
        dto.setPrivateId(String.valueOf(signal.getSignalInfo().getActiveSignal().getUid()));
        dto.setDirection(signal.getSignalInfo().getAntennaInfo().getDirectionName());
        return dto;
    }

    @Data
    @AllArgsConstructor
    private static class JobInfo {
        private String uid;
        private String deviceName;
        private double frequency;
    }
}