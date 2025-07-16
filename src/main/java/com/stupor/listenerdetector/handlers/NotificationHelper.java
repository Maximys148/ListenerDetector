package com.stupor.listenerdetector.handlers;

import Scanner.Net.Api.Packet.ApiPacket;
import Scanner.Net.Api.Connectors.Notifiers;
import Scanner.Net.Api.Packet.ApiTypes;

import java.util.List;

public class NotificationHelper {
    /*public static ApiPacket.Message createSubscription(String jobUid, List<ApiTypes.Types> notificationTypes) {
        Notifiers.PacketRegisterJobNotifier.Builder notifier = Notifiers.PacketRegisterJobNotifier.newBuilder()
            .setJobUid(jobUid)
            .addAllTypes(notificationTypes);

        return ApiPacket.Message.newBuilder()
            .setType(ApiTypes.Types.CONNECTORS_REGISTER_JOB_NOTIFIER)
            .setData(notifier.build().toByteString())
            .build();
    }*/
}