package com.example.Medicamento.Controller;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Medicamento.Config.EmailConfig;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class EmailConfigController {
    
    private final EmailConfig emailConfig; // La clase de configuración que creamos antes

    @GetMapping("/email-status")
    public ResponseEntity<Map<String, Boolean>> getEmailStatus() {
        return ResponseEntity.ok(Collections.singletonMap("enabled", emailConfig.isEnabled()));
    }

    @PostMapping("/toggle-email")
    public ResponseEntity<Map<String, String>> toggleEmail(@RequestBody Map<String, Boolean> request) {
        boolean enabled = request.getOrDefault("enabled", false);
        emailConfig.setEnabled(enabled);
        
        return ResponseEntity.ok(Collections.singletonMap("message", 
            "Correos electrónicos " + (enabled ? "activados" : "desactivados")));
    }
}
