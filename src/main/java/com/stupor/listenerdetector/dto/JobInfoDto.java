package com.stupor.listenerdetector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для информации о задании
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobInfoDto {
    private String uid;
    private String deviceName;
    private int channelNumber;
    private boolean isRunning;
    private long centerFrequencyMHz;
    private long bandwidthMHz;
    private String comment;
}