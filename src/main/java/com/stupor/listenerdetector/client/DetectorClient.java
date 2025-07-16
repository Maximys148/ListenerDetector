package com.stupor.listenerdetector.client;

import com.stupor.listenerdetector.exceptions.DetectorException;
//import com.stupor.listenerdetector.handlers.JobHandler;
import com.stupor.listenerdetector.handlers.VersionHandler;
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
    private final WebSocketHandler webSocketHandler;
    private WebSocketClient client;
    private final CountDownLatch connectionLatch = new CountDownLatch(1);
    private final Logger log = LogManager.getLogger(DetectorClient.class);

    @Value("${websocket.server.url}")
    private String serverUrl;

    @Autowired
    public DetectorClient(WebSocketHandler webSocketHandler,
                          VersionHandler versionHandler/*,
                          JobHandler jobHandler*/) {
        this.webSocketHandler = webSocketHandler;
        this.webSocketHandler.registerHandler(versionHandler);
        /*this.webSocketHandler.registerHandler(jobHandler);*/
    }

    @PostConstruct
    public void init() {
        log.info("Initializing connection to WebSocket server...");
        connect();
    }

    @PreDestroy
    public void cleanup() {
        log.info("Closing WebSocket connection...");
        disconnect();
    }

    public void connect() {
        try {
            log.info("Connecting to WebSocket server at: {}", serverUrl);
            URI serverUri = new URI(serverUrl);

            client = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("WebSocket connection established");
                    connectionLatch.countDown();
                }

                @Override
                public void onMessage(String message) {
                    log.warn("Unexpected text message received: {}", message);
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    log.debug("Binary message received, processing...");
                    webSocketHandler.handleMessage(bytes.array());
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("Connection closed. Reason: {}", reason);
                    scheduleReconnect();
                }

                @Override
                public void onError(Exception ex) {
                    log.error("WebSocket error", ex);
                }
            };

            client.connect();

            if (!connectionLatch.await(10, TimeUnit.SECONDS)) {
                throw new DetectorException("Connection timeout (10 seconds)");
            }

            log.info("Successfully connected to WebSocket server");

        } catch (Exception e) {
            log.error("WebSocket connection error", e);
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        log.info("Attempting to reconnect in 5 seconds...");
        try {
            Thread.sleep(5000);
            connect();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("Reconnect attempt interrupted", ie);
        }
    }

    public void disconnect() {
        if (client != null) {
            client.close();
            log.info("WebSocket connection closed");
        }
    }

    public void sendMessage(byte[] message) {
        if (client != null && client.isOpen()) {
            client.send(message);
            log.debug("Message sent successfully");
        } else {
            throw new DetectorException("WebSocket connection not established");
        }
    }
}