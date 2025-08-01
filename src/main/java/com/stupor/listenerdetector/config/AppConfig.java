package com.stupor.listenerdetector.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.stupor.listenerdetector.client.DetectorClient;
import com.stupor.listenerdetector.dto.DeviceState;
import com.stupor.listenerdetector.dto.Location;
import com.stupor.listenerdetector.dto.enums.DeviceType;
import com.stupor.listenerdetector.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Конфигурационный класс Spring для настройки бинов приложения
 */
@Configuration
public class AppConfig {

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public DeviceState deviceModel() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:SEH.json");
        InputStream inputStream = resource.getInputStream();
        DeviceState deviceState = objectMapper().readValue(inputStream, DeviceState.class);
        deviceState.setPublicId("Штиль Фарватер-1");
        deviceState.setPrivateId("shtilFarvater-1");
        deviceState.setPoint(new Location(56.075101, 36.763000));
        deviceState.setType(DeviceType.RF_SCANNER);
        deviceState.setConnected(false);
        HashMap<String, String> character = new HashMap<>();
        character.put("ip", "192.168.1.205");
        character.put("port", "7777");
        character.put("radius", "8000");
        deviceState.setCharacter(character);
        return deviceState;
    }
    
    /*@Bean
    public MessageService messageProcessor() {
        return new MessageService();
    }
    
    @Bean
    public DetectorClient detectorClient() {
        return new DetectorClient();
    }*/
}