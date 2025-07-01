package com.stupor.listenerdetector.handlers;


import com.stupor.listenerdetector.client.DetectorClient;
import com.stupor.listenerdetector.services.DetectorService;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scanner.net.api.connectors.Notifiers;
import scanner.net.api.data.Jobs;
import scanner.net.api.packet.ApiPacket;
import scanner.net.api.packet.ApiTypes;



/**
 * Обработчик сообщений, связанных с заданиями и уведомлениями
 */
public class JobHandler implements MessageHandler{

    private final DetectorService detectorService;
    @Setter
    private DetectorClient detectorClient;
    private final Logger log = LogManager.getLogger(JobHandler.class);

    public JobHandler() {
        this.detectorService = null;
    }

    public boolean canHandle(ApiTypes.Types type) {
        return type == ApiTypes.Types.DATA_LOADED_JOBS_LIST ||
               type == ApiTypes.Types.DATA_NOTIFIES;
    }
    
    public void handle(ApiPacket.Message message) {
        try {
            switch (message.getType()) {
                case DATA_LOADED_JOBS_LIST:
                    handleJobsList(Jobs.PacketLoadedJobsList.parseFrom(message.getData()));
                    break;
                case DATA_NOTIFIES:
                    handleNotifies(Notifiers.PacketJobsNotifies.parseFrom(message.getData()));
                    break;
                default:
                    log.warn("Неподдерживаемый тип сообщения: {}", message.getType());
            }
        } catch (Exception e) {
            log.error("Не удалось проанализировать сообщение о задании", e);
        }
    }
    
    private void handleJobsList(Jobs.PacketLoadedJobsList jobsList) {
        log.info("Получен список заданий с {} заданиями", jobsList.getJobsCount());
        
        // Преобразуем Protobuf сообщение в DTO и передаем в сервисный слой
        detectorService.processJobsList(jobsList);
        
        // Подписываемся на уведомления по всем заданиям
        subscribeToJobs(jobsList);
    }
    
    private void subscribeToJobs(Jobs.PacketLoadedJobsList jobsList) {
        Notifiers.PacketRegisterJobNotifier.Builder notifierBuilder = Notifiers.PacketRegisterJobNotifier.newBuilder();
        
        for (var job : jobsList.getJobsList()) {
            var jobNotifier = Notifiers.Notifier.newBuilder()
                .setJobUid(job.getUid())
                .addTypes(Notifiers.Type.JOB_STARTED)
                .addTypes(Notifiers.Type.JOB_STOPPED)
                .addTypes(Notifiers.Type.JOB_SIGNALS_CHANGED)
                .build();
            
            notifierBuilder.addJobsNotifiers(jobNotifier);
        }
        
        ApiPacket.Message message = ApiPacket.Message.newBuilder()
            .setType(ApiTypes.Types.CONNECTORS_REGISTER_JOB_NOTIFIER)
            .setData(notifierBuilder.build().toByteString())
            .build();
        
        detectorClient.sendMessage(message.toByteArray());
    }
    
    private void handleNotifies(Notifiers.PacketJobsNotifies notifies) {
        for (var jobNotify : notifies.getJobsNotifiesList()) {
            for (var notify : jobNotify.getNotifiesList()) {
                switch (notify.getType()) {
                    case JOB_STARTED:
                        detectorService.handleJobStarted(jobNotify.getJobUid(), notify.getJobStarted());
                        break;
                    case JOB_STOPPED:
                        detectorService.handleJobStopped(jobNotify.getJobUid(), notify.getJobStopped());
                        break;
                    case JOB_SIGNALS_CHANGED:
                        detectorService.handleSignalsChanged(jobNotify.getJobUid(), notify.getJobSignalsChanged());
                        break;
                    default:
                        log.debug("Необработанный тип уведомления: {}", notify.getType());
                }
            }
        }
    }

}