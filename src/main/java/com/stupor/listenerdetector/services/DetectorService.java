package com.stupor.listenerdetector.services;

import com.stupor.listenerdetector.kafka.KafkaProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
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
    }
}