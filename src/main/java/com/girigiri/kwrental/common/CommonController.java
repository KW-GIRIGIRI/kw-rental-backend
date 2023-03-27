package com.girigiri.kwrental.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @GetMapping("/")
    public ResponseEntity<?> responseHealthCheck() {
        return ResponseEntity.ok("Health check success");
    }
}
