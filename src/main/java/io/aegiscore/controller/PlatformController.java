package io.aegiscore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform")
public class PlatformController {

    @GetMapping
    public Map<String, String> getPlatformInfo() {
        return Map.of(
            "name", "AegisCore",
            "version", "0.1.0",
            "description", "Cloud Security and Operations Platform",
            "status", "operational"
        );
    }
}


