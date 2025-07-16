package com.stupor.listenerdetector.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.stupor.listenerdetector.handlers.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Scanner.Net.Api.Packet.ApiPacket;
import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler {
    private final List<MessageHandler> handlers = new ArrayList<>();
    private final Logger log = LogManager.getLogger(WebSocketHandler.class);

    public void registerHandler(MessageHandler handler) {
        handlers.add(handler);
        //log.info("Registered handler for type: {}", handler.getSupportedType());
    }

    public void handleMessage(byte[] message) {
        try {
            ApiPacket.Message packet = ApiPacket.Message.parseFrom(message);
            log.debug("Received packet type: {}", packet.getType());

            handlers.stream()
                    .filter(handler -> handler.canHandle(packet.getType()))
                    .forEach(handler -> {
                        log.debug("Routing packet to {} handler", handler.getClass().getSimpleName());
                        handler.handle(packet);
                    });

        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse incoming message", e);
        }
    }
}