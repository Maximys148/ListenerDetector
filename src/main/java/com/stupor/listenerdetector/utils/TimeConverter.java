package com.stupor.listenerdetector.utils;


import Scanner.Net.Api.Common.Time;

import java.time.Instant;

/**
 * Утилиты для преобразования времени
 */
public class TimeConverter {
    public static Instant toInstant(Time.LinuxTimeWithMs linuxTime) {
        return Instant.ofEpochSecond(linuxTime.getSec(), linuxTime.getMs() * 1_000_000);
    }
}