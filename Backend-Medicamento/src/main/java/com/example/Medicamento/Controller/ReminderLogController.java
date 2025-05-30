package com.example.Medicamento.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Medicamento.Model.ReminderLog;
import com.example.Medicamento.Repository.ReminderLogRepository;

@RestController
@RequestMapping("/api/reminders")
public class ReminderLogController {

    @Autowired
    private ReminderLogRepository reminderLogRepository;

    @PostMapping("/confirm/{assignmentId}")
    public ResponseEntity<String> confirmReminder(@PathVariable Long assignmentId) {
        Optional<ReminderLog> latestLogOpt = reminderLogRepository.findTopByAssignment_IdOrderByReminderSentAtDesc(assignmentId);

        if (latestLogOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ No se encontró recordatorio para este assignment.");
        }

        ReminderLog log = latestLogOpt.get();
        log.setConfirmed(true);
        reminderLogRepository.save(log);

        return ResponseEntity.ok("✅ Recordatorio confirmado correctamente.");
    }
}
