package com.stupor.listenerdetector.services;


import com.stupor.listenerdetector.client.DetectorClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import scanner.net.api.connectors.Notifiers;
import scanner.net.api.data.Jobs;

/**
 * Реализация сервиса для работы с API "Обнаружителя"
 */
@Service
public class DetectorService {
    private final Logger log = LogManager.getLogger(DetectorService.class);
    
    public void processJobsList(Jobs.PacketLoadedJobsList jobsList) {
        // Здесь можно добавить логику обработки списка заданий
        log.info("Обработка списка заданий с {} заданиями", jobsList.getJobsCount());
    }
    
    public void handleJobStarted(String jobUid, Notifiers.JobStarted jobStarted) {
        log.info("Задание {} началась в {}", jobUid, jobStarted.getNotifiedAt().getSec());
    }
    
    public void handleJobStopped(String jobUid, Notifiers.JobStopped jobStopped) {
        log.info("Задание {} закончилось в {}", jobUid, jobStopped.getNotifiedAt().getSec());
    }
    
    public void handleSignalsChanged(String jobUid, Notifiers.JobSignalsChanged signalsChanged) {
        log.info("Задание {} сигналы изменены. Найдено {} сигналов",
            jobUid, signalsChanged.getSignalsInfoCount());
        
        // Здесь можно добавить логику обработки изменений сигналов
    }
}