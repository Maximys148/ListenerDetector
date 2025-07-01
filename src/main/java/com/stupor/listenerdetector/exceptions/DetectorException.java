package com.stupor.listenerdetector.exceptions;

/**
 * Пользовательское исключение для ошибок работы с API "Обнаружителя"
 */
public class DetectorException extends RuntimeException {
    public DetectorException(String message) {
        super(message);
    }
    
    public DetectorException(String message, Throwable cause) {
        super(message, cause);
    }
}