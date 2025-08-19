package com.stupor.listenerdetector.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.stupor.listenerdetector.dto.DeviceState;
import com.stupor.listenerdetector.services.MessageService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class DetectorClient {
    private final Logger log = LogManager.getLogger(DetectorClient.class);
    private WebSocketClient webSocketClient;
    @Autowired
    private MessageService messageService;
    private final CountDownLatch connectionLatch = new CountDownLatch(1);
    @Autowired
    private DeviceState deviceState;

    @Value("${websocket.server.url}")
    private String serverUrl;

    @PostConstruct
    public void connect() {
        try {
            URI serverUri = new URI(serverUrl);
            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("Успешное подключение к WebSocket серверу");
                    deviceState.setConnected(true);
                    connectionLatch.countDown();
                }

                @Override
                public void onMessage(String message) {
                    log.warn("Получено текстовое сообщение (не ожидалось): {}", message);
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    byte[] data = new byte[bytes.remaining()];
                    bytes.get(data);
                    try {
                        messageService.processRawMessage(data);
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.warn("Соединение закрыто. Код: {}, Причина: {}", code, reason);
                    deviceState.setConnected(false);
                    scheduleReconnect();
                }

                @Override
                public void onError(Exception ex) {
                    log.error("Ошибка WebSocket соединения", ex);
                }
            };
            messageService.setWebSocketClient(webSocketClient);
            log.info("Подключаемся к WebSocket серверу: {}", serverUrl);
            webSocketClient.connect();

            if (!connectionLatch.await(10, TimeUnit.SECONDS)) {
                //throw new DetectorException("Таймаут подключения к WebSocket");
            }
        } catch (Exception e) {
            log.error("Ошибка при подключении к WebSocket", e);
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        try {
            log.info("Повторное подключение через 5 секунд...");
            Thread.sleep(5000);
            connect();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Поток переподключения прерван", e);
        }
    }

    @PreDestroy
    public void disconnect() {
        if (webSocketClient != null) {
            log.info("Закрытие WebSocket соединения");
            webSocketClient.close();
        }
    }
}