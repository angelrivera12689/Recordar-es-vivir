package com.example.Medicamento.Controller;

import com.example.Medicamento.DTO.LogDto;
import com.example.Medicamento.DTO.ResponseDTO;
import com.example.Medicamento.Service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    // Crear un log
    @PostMapping
    public ResponseEntity<ResponseDTO> createLog(@RequestBody LogDto logDto) {
        ResponseDTO response = logService.createLog(logDto);
        if (response.getStatus().equals("200 OK")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Obtener todos los logs
    @GetMapping
    public ResponseEntity<List<LogDto>> getAllLogs() {
        List<LogDto> logs = logService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    // Obtener un log por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getLogById(@PathVariable Long id) {
        Optional<LogDto> logOpt = logService.getLogById(id);
        if (logOpt.isPresent()) {
            return ResponseEntity.ok(logOpt.get());
        } else {
            return ResponseEntity.status(404).body(new ResponseDTO("404 NOT_FOUND", "Log not found"));
        }
    }

    // Eliminar un log por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteLog(@PathVariable Long id) {
        ResponseDTO response = logService.deleteLog(id);
        if (response.getStatus().equals("200 OK")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(response);
        }
    }
}
