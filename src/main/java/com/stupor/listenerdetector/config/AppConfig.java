package com.stupor.listenerdetector.config;


import com.stupor.listenerdetector.client.DetectorClient;
import com.stupor.listenerdetector.services.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс Spring для настройки бинов приложения
 */
@Configuration
public class AppConfig {
    
    @Bean
    public MessageService messageProcessor() {
        return new MessageService();
    }
    
    @Bean
    public DetectorClient detectorClient() {
        return new DetectorClient();
    }
}