package com.stupor.listenerdetector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stupor.listenerdetector.dto.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceState {
    private String publicId; // Название
    private String privateId; // IMEI (лицензия)
    private Location point; // координата
    private final String model = "shtilFarvater";
    private DeviceType type = DeviceType.RF_SCANNER; // тип устройства
    @JsonProperty("isConnected")
    private boolean isConnected; // Статус подключения
    private HashMap<String, String> character; // все остальное
    /**
     * В character добавляется следующая информация:
     * 1. IP адрес устройства - ip
     * 2. Порт устройства - port
     * 4. Азимут, если нужен (радар) - azimuth
     * 5. Углы ротации (радар: 360° или сектор) - start_angle && end_angle
     */
}
