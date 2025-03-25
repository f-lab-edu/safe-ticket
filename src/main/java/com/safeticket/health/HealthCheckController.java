package com.safeticket.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("server is healthy");
    }
}
