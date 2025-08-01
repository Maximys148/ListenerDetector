package com.stupor.listenerdetector.services;

import Scanner.Net.Api.Connectors.Notifiers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class MessageServiceTest {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceTest.class);

    @Test
    void tryToCreateRequest(){
        Notifiers.PacketRegisterJobNotifier subscription = Notifiers.PacketRegisterJobNotifier.newBuilder()
                .addJobsNotifiers(Notifiers.Notifier.newBuilder()
                        .setJobUid("test uid")
                        .addTypes(Notifiers.Type.JOB_STARTED)
                        .addTypes(Notifiers.Type.JOB_SPECTRUM_CHANGED)
                        .build())
                .build();

        //sendMessage(subscription.toByteArray());
        log.info("{}", new String(subscription.toByteArray()));
    }
}
