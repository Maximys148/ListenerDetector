package com.stupor.listenerdetector.handlers;


import Scanner.Net.Api.Packet.ApiPacket;
import Scanner.Net.Api.Packet.ApiTypes;

/**
 * Интерфейс для обработки входящих сообщений от WebSocket сервера
 */
public interface MessageHandler {
    
    /**
     * Проверяет, может ли обработчик работать с данным типом сообщения
     * @param type тип сообщения из протокола
     * @return true если обработчик может обработать этот тип сообщения
     */
    boolean canHandle(ApiTypes.Types type);
    
    /**
     * Обрабатывает входящее сообщение
     * @param message сообщение для обработки
     */
    void handle(ApiPacket.Message message);
}