package com.stupor.listenerdetector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignalDto {
    private String publicId;         // Публичный ID сигнала (например, для внешних систем)
    private String privateId;        // Внутренний ID сигнала (уникальный в рамках системы)
    private Long timestamp;          // Время обнаружения сигнала (мс или Unix-время)
    private String frequency;        // Частота сигнала (например, "850.5 MHz")
    private String deviceImei;       // IMEI устройства, обнаружившего сигнал
    private Double maxSignalLevel;   // Максимальный уровень сигнала (в dBm)
    private String direction;        // Направление (если доступно)
}