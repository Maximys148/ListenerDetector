package com.stupor.listenerdetector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sun.misc.Signal;

import java.util.List;

/**
 * Модель данных для задания
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private String id;
    private String deviceId;
    private boolean active;
    private long centerFrequency;
    private long bandwidth;
    private List<Signal> signals;

}