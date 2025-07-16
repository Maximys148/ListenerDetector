package com.stupor.listenerdetector.config;


import com.stupor.listenerdetector.client.DetectorClient;
import com.stupor.listenerdetector.client.WebSocketHandler;
//import com.stupor.listenerdetector.handlers.JobHandler;
import com.stupor.listenerdetector.handlers.VersionHandler;
import com.stupor.listenerdetector.services.DetectorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс Spring для настройки бинов приложения
 */
@Configuration
public class AppConfig {
    
    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler();
    }
    
    @Bean
    public VersionHandler versionHandler() {
        return new VersionHandler();
    }
    
    /*@Bean
    public JobHandler jobHandler() {
        return new JobHandler();
    }*/
    
    @Bean
    public DetectorService detectorService() {return new DetectorService();}
    
    @Bean
    public DetectorClient detectorClient(WebSocketHandler webSocketHandler,
                                         VersionHandler versionHandler
                                         /*JobHandler jobHandler*/) {
        return new DetectorClient(webSocketHandler, versionHandler /*jobHandler*/);
    }
}