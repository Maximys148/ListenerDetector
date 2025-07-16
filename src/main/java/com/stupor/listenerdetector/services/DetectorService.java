package com.stupor.listenerdetector.services;

import com.stupor.listenerdetector.kafka.KafkaProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import Scanner.Net.Api.Connectors.Notifiers;
import Scanner.Net.Api.Data.Jobs;

/**
 * Реализация сервиса для работы с API "Обнаружителя"
 */
@Service
public class DetectorService {
    private KafkaProducer kafkaProducer;
    private final Logger log = LogManager.getLogger(DetectorService.class);
    
    public void processJobsList(Jobs.PacketLoadedJobsList jobsList) {
        // Здесь можно добавить логику обработки списка заданий
        log.info("Обработка списка заданий с {} заданиями", jobsList.getJobsCount());
        kafkaProducer.sendMessage("signal", jobsList);
    }
    
    /*public void handleJobStarted(String jobUid, Notifiers.JobStarted jobStarted) {
        log.info("Задание {} началась в {}", jobUid, jobStarted.getNotifiedAt().getSec());
        kafkaProducer.sendMessage("signal", jobStarted);
    }
    
    public void handleJobStopped(String jobUid, Notifiers.JobStopped jobStopped) {
        log.info("Задание {} закончилось в {}", jobUid, jobStopped.getNotifiedAt().getSec());
        kafkaProducer.sendMessage("signal", jobStopped);
    }
    
    public void handleSignalsChanged(String jobUid, Notifiers.JobSignalsChanged signalsChanged) {
        log.info("Задание {} сигналы изменены. Найдено {} сигналов", jobUid, signalsChanged.getSignalsInfoCount());
        kafkaProducer.sendMessage("signal", signalsChanged);
    }*/
}