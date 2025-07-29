package com.stupor.listenerdetector.client;

import Scanner.Net.Api.Connectors.Notifiers;
import Scanner.Net.Api.Packet.ApiPacket;
import Scanner.Net.Api.Packet.ApiTypes;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stupor.listenerdetector.exceptions.DetectorException;
import com.stupor.listenerdetector.services.MessageService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class DetectorClient {
    private final Logger log = LogManager.getLogger(DetectorClient.class);
    private WebSocketClient client;
    private MessageService messageService;
    private final CountDownLatch connectionLatch = new CountDownLatch(1);

    @Value("${websocket.server.url}")
    private String serverUrl;

    @PostConstruct
    public void connect() {
        try {
            URI serverUri = new URI(serverUrl);
            client = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("Успешное подключение к WebSocket серверу");
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
                        onBinaryMessage(data);
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.warn("Соединение закрыто. Код: {}, Причина: {}", code, reason);
                    scheduleReconnect();
                }

                @Override
                public void onError(Exception ex) {
                    log.error("Ошибка WebSocket соединения", ex);
                }
            };

            log.info("Подключаемся к WebSocket серверу: {}", serverUrl);
            client.connect();

            if (!connectionLatch.await(10, TimeUnit.SECONDS)) {
                throw new DetectorException("Таймаут подключения к WebSocket");
            }
        } catch (Exception e) {
            log.error("Ошибка при подключении к WebSocket", e);
            scheduleReconnect();
        }
    }

    /**
     * Отправка бинарного сообщения через WebSocket
     */
    public void sendMessage(byte[] data) {
        if (client != null && client.isOpen()) {
            client.send(data);
            log.debug("Сообщение отправлено {}", data);
        } else {
            log.error("Не удалось отправить сообщение - соединение не активно");
            throw new DetectorException("WebSocket соединение не установлено");
        }
    }

    /**
     * Метод для подписки на задание
     */
    public void subscribeToJob(String jobUid) {
        Notifiers.PacketRegisterJobNotifier subscription = Notifiers.PacketRegisterJobNotifier.newBuilder()
                .addJobsNotifiers(Notifiers.Notifier.newBuilder()
                        .setJobUid(jobUid)
                        .addTypes(Notifiers.Type.JOB_SIGNALS_WITH_MAX_LEVEL_CHANGED)
                        .addTypes(Notifiers.Type.JOB_SPECTRUM_CHANGED)
                        .build())
                .build();

        sendProtobufMessage(ApiTypes.Types.CONNECTORS_REGISTER_JOB_NOTIFIER, subscription);
    }

    /**
     * Универсальный метод для отправки protobuf-сообщений
     */
    public void sendProtobufMessage(ApiTypes.Types messageType, com.google.protobuf.Message message) {
        try {
            byte[] data = ApiPacket.Message.newBuilder()
                    .setType(messageType)
                    .setData(message.toByteString())
                    .build()
                    .toByteArray();

            sendMessage(data);
        } catch (Exception e) {
            log.error("Ошибка формирования protobuf сообщения", e);
            throw new DetectorException("Ошибка отправки сообщения");
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
        if (client != null) {
            log.info("Закрытие WebSocket соединения");
            client.close();
        }
    }

    // Метод для обработки бинарных сообщений (может быть переопределен)
    protected void onBinaryMessage(byte[] data) throws InvalidProtocolBufferException {
        messageService.processRawMessage(data);
    }
}