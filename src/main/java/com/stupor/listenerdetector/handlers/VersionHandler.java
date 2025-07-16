package com.stupor.listenerdetector.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Scanner.Net.Api.Common.ProtoInfo;
import Scanner.Net.Api.Packet.ApiPacket;
import Scanner.Net.Api.Packet.ApiTypes;

/**
 * Обработчик сообщений с версией протокола (COMMON_VERSION)
 */
public class VersionHandler implements MessageHandler {
    private final Logger log = LogManager.getLogger(VersionHandler.class);
    
    public boolean canHandle(ApiTypes.Types type) {
        return type == ApiTypes.Types.COMMON_VERSION;
    }
    
    public void handle(ApiPacket.Message message) {
        try {
            ProtoInfo.PacketVersion version = ProtoInfo.PacketVersion.parseFrom(message.getData());
            log.info("Версия протокола детектора: {}", version.getVersion().getInString());
        } catch (Exception e) {
            log.error("Не удалось проанализировать версию протокола", e);
        }
    }
}