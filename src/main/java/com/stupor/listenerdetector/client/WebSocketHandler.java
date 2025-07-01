package com.stupor.listenerdetector.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.stupor.listenerdetector.handlers.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scanner.net.api.packet.ApiPacket;
import java.util.ArrayList;
import java.util.List;

/**
 * Диспетчер сообщений, распределяющий входящие сообщения по соответствующим обработчикам
 */
public class  WebSocketHandler {
    private final List<MessageHandler> handlers = new ArrayList<>();

    private final Logger log = LogManager.getLogger(WebSocketHandler.class);

    public void registerHandler(MessageHandler handler) {
        handlers.add(handler);
    }

    /**
     * Обработка входящего сообщения
     */
    public void handleMessage(byte[] message) {
        try {
            ApiPacket.Message packet = ApiPacket.Message.parseFrom(message);

            // Передаем сообщение всем зарегистрированным обработчикам
            for (MessageHandler handler : handlers) {
                if (handler.canHandle(packet.getType())) {
                    handler.handle(packet);
                }
            }

        } catch (InvalidProtocolBufferException e) {
            log.error("Не удалось проанализировать входящее сообщение", e);
        }
    }
}