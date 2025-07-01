package com.stupor.listenerdetector.client;

import com.stupor.listenerdetector.exceptions.DetectorException;
import com.stupor.listenerdetector.handlers.JobHandler;
import com.stupor.listenerdetector.handlers.VersionHandler;
import com.stupor.listenerdetector.services.DetectorService;
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

/**
 * Основной класс для работы с WebSocket API "Обнаружителя"
 */
@Component
public class DetectorClient {
    private final WebSocketHandler webSocketHandler;
    private WebSocketClient client;
    private final CountDownLatch connectionLatch = new CountDownLatch(1);
    private final Logger log = LogManager.getLogger(DetectorClient.class);

    @Value("${websocket.server.url}")
    private String serverUrl;

    @Autowired
    public DetectorClient(WebSocketHandler webSocketHandler,
                          VersionHandler versionHandler,
                          JobHandler jobHandler) {
        this.webSocketHandler = webSocketHandler;
        this.webSocketHandler.registerHandler(versionHandler);
        this.webSocketHandler.registerHandler(jobHandler);
    }

    @PostConstruct
    public void init() {
        log.info("Инициализация подключения к WebSocket серверу...");
        connect();
    }

    @PreDestroy
    public void cleanup() {
        log.info("Завершение работы. Отключение от WebSocket сервера...");
        disconnect();
    }

    /**
     * Подключение к WebSocket серверу "Обнаружителя"
     */
    public void connect() {
        try {
            log.info("Попытка подключения к WebSocket серверу по адресу: {}", serverUrl);
            URI serverUri = new URI(serverUrl);

            client = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("Успешное подключение к WebSocket серверу");
                    connectionLatch.countDown();
                }

                @Override
                public void onMessage(String message) {
                    log.warn("Получено неожиданное текстовое сообщение: {}", message);
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    log.debug("Получено бинарное сообщение, обработка...");
                    webSocketHandler.handleMessage(bytes.array());
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("Соединение закрыто. Причина: {}", reason);
                    log.info("Попытка переподключения через 5 секунд...");
                    try {
                        Thread.sleep(5000);
                        connect();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("Ошибка при попытке переподключения", e);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    log.error("Ошибка WebSocket соединения", ex);
                }
            };

            log.info("Установка соединения с сервером...");
            client.connect();

            if (!connectionLatch.await(10, TimeUnit.SECONDS)) {
                throw new DetectorException("Таймаут подключения (10 секунд)");
            }

            log.info("Соединение с WebSocket сервером успешно установлено");

        } catch (Exception e) {
            log.error("Ошибка при подключении к WebSocket серверу", e);
            log.info("Повторная попытка подключения через 5 секунд...");
            try {
                Thread.sleep(5000);
                connect();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("Ошибка при попытке переподключения", ie);
            }
        }
    }

    /**
     * Отключение от сервера
     */
    public void disconnect() {
        if (client != null) {
            log.info("Закрытие соединения с WebSocket сервером...");
            client.close();
            log.info("Соединение с WebSocket сервером закрыто");
        }
    }

    /**
     * Отправка сообщения на сервер
     */
    public void sendMessage(byte[] message) {
        if (client != null && client.isOpen()) {
            log.debug("Отправка сообщения на сервер...");
            client.send(message);
            log.debug("Сообщение успешно отправлено");
        } else {
            log.error("Невозможно отправить сообщение: соединение не установлено");
            throw new DetectorException("WebSocket соединение не установлено");
        }
    }
}