package com.ems.hr.settings;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/hr/settings")
public class SettingsController {

    private final SettingsService service;

    public SettingsController(SettingsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSettings() {
        return ResponseEntity.ok(service.grouped());
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateSettings(@RequestBody SettingsPayload payload) {
        return ResponseEntity.ok(service.save(payload));
    }

    @PostMapping(value = "/company-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(Map.of("companyLogoUrl", service.updateLogo(file)));
    }
}
