package com.stupor.listenerdetector.services;

import com.stupor.listenerdetector.dto.DeviceState;
import com.stupor.listenerdetector.dto.FarvaterDTO;
import com.stupor.listenerdetector.dto.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class MoveService {
    private final DeviceState deviceState;
    private final Logger log = LogManager.getLogger(MoveService.class);


    public MoveService(DeviceState deviceState) {
        this.deviceState = deviceState;
    }

    public void moveFarvater(FarvaterDTO farvaterDTO) {
        Location location = new Location();
        location.setLatitude(Double.valueOf(farvaterDTO.getLatitude()));
        location.setLongitude(Double.valueOf(farvaterDTO.getLongitude()));
        deviceState.setPoint(location);
        log.info("Смена координат у фарватера " + farvaterDTO);
    }
}
