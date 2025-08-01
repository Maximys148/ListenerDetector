package com.stupor.listenerdetector.controllers;

import com.stupor.listenerdetector.dto.Location;
import com.stupor.listenerdetector.services.MoveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shtilFarvater")
public class MoveController {
    private final MoveService moveService;
    private final Logger log = LogManager.getLogger(MoveController.class);

    public MoveController(MoveService moveService) {
        this.moveService = moveService;
    }

    @CrossOrigin
    @PostMapping("/dashboard_post")
    public ResponseEntity<?> moveFarvater(@RequestBody Location location) {
        log.info("API Post /api/dashboard_post | Запрос на передвижение фарватера, тело запроса: {}", location);
        moveService.moveFarvater(location);
        return ResponseEntity.status(201).body(location);
    }
}
