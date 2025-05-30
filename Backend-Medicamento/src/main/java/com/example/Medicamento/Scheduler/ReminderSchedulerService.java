package com.example.Medicamento.Scheduler;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Medicamento.Model.Assignment;
import com.example.Medicamento.Model.ReminderLog;
import com.example.Medicamento.Repository.MedicineAssignmentRepository;
import com.example.Medicamento.Repository.ReminderLogRepository;
import com.example.Medicamento.Service.EmailService;
import com.example.Medicamento.Config.EmailConfig; // Añade esta importación

@Service
public class ReminderSchedulerService {

    @Autowired
    private MedicineAssignmentRepository assignmentRepo;

    @Autowired
    private ReminderLogRepository logRepo;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private EmailConfig emailConfig; // Añade esta inyección

    @Scheduled(cron = "0 21 17 29 5 ?")
    @Transactional
    public void resetDailyConfirmations() {
        ZoneId zone = ZoneId.of("America/Mexico_City");
        LocalDate today = LocalDate.now(zone);
        
        System.out.println("⏰ Hora actual del sistema: " + LocalDateTime.now(zone));
        
        if (today.getYear() == 2025) {
            List<ReminderLog> confirmedLogs = logRepo.findByConfirmedTrueAndLogDate(today);
            
            System.out.println("🔍 Registros encontrados: " + confirmedLogs.size());
            
            confirmedLogs.forEach(log -> {
                System.out.println("🔄 Reiniciando log ID: " + log.getId() + 
                                 " | Confirmado: " + log.isConfirmed() + 
                                 " | Fecha: " + log.getLogDate());
                log.setConfirmed(false);
                log.setConfirmationTime(null);
            });
            
            logRepo.saveAll(confirmedLogs);
            System.out.println("✅ Confirmaciones reiniciadas: " + confirmedLogs.size());
        } else {
            System.out.println("⏸ No es 2025, ejecución ignorada.");
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndSend() {
        // Verificación inicial de estado de correos
        if (!emailConfig.isEnabled()) {
            System.out.println("✉️ Correos desactivados. No se enviarán recordatorios.");
            return;
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault()).withSecond(0).withNano(0);
        List<Assignment> assignments = assignmentRepo.findBySuspendedFalse();

        for (Assignment a : assignments) {
            if (a.getStartTime() == null) {
                System.err.println("Assignment " + a.getId() + " has no start time. Skipping.");
                continue;
            }
            LocalDateTime assignmentStartTime = a.getStartTime().withSecond(0).withNano(0);

            if (now.isBefore(assignmentStartTime)) {
                continue;
            }

            long minutesSinceStart = Duration.between(assignmentStartTime, now).toMinutes();

            if (a.getFrequencyMinutes() <= 0) {
                System.err.println("Assignment " + a.getId() + " has invalid frequency (" + a.getFrequencyMinutes() + "). Skipping.");
                continue;
            }

            if (minutesSinceStart % a.getFrequencyMinutes() != 0) {
                continue;
            }

            LocalDateTime startOfToday = now.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime endOfToday = now.toLocalDate().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toLocalDateTime();

            if (logRepo.existsByAssignmentAndConfirmedTrueAndConfirmationTimeBetween(a, startOfToday, endOfToday)) {
                System.out.println("Recordatorio para " + a.getMedication().getName() + " del paciente " + a.getPatient().getFirstName() + " ya CONFIRMADO para hoy. Saltando.");
                continue;
            }

            if (logRepo.existsByAssignmentAndReminderSentAt(a, now)) {
                System.out.println("Recordatorio para " + a.getMedication().getName() + " del paciente " + a.getPatient().getFirstName() + " ya enviado para este minuto. Saltando.");
                continue;
            }

            try {
                // Verificación adicional antes del envío
                if (!emailConfig.isEnabled()) {
                    System.out.println("⏸ Correo suprimido (configuración desactivada en el momento del envío)");
                    return;
                }

                String email = a.getPatient().getEmail();
                String name = a.getPatient().getFirstName();
                String med = a.getMedication().getName();
                String confirmationToken = UUID.randomUUID().toString();

                ReminderLog log = new ReminderLog();
                log.setAssignment(a);
                log.setReminderSentAt(now);
                log.setConfirmed(false);
                log.setConfirmationToken(confirmationToken);

                log = logRepo.save(log);

                emailService.sendReminderEmailWithConfirmation(email, name, med, now, confirmationToken);

                System.out.println("Recordatorio enviado para " + med + " a " + email + " en " + now + " con token: " + confirmationToken);

            } catch (Exception e) {
                System.err.println("Error enviando recordatorio a: " + a.getPatient().getEmail() + " para medicamento " + a.getMedication().getName() + " en " + now);
                e.printStackTrace();
            }
        }
    }
}