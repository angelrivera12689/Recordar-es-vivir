package com.example.Medicamento.Service;

import com.example.Medicamento.DTO.LogDto;
import com.example.Medicamento.DTO.ResponseDTO;
import com.example.Medicamento.Model.ReminderLog;
import com.example.Medicamento.Repository.ReminderLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LogService {

    @Autowired
    private ReminderLogRepository logRepository;

    // Crear log
    public ResponseDTO createLog(LogDto dto) {
        try {
            ReminderLog log = convertToEntity(dto);
            logRepository.save(log);
            return new ResponseDTO(HttpStatus.OK.toString(), "Log created successfully");
        } catch (Exception e) {
            return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), "Error creating log");
        }
    }

    // Obtener todos los logs como DTOs
    public List<LogDto> getAllLogs() {
        List<ReminderLog> logs = logRepository.findAll();
        return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Obtener log por ID como DTO
    public Optional<LogDto> getLogById(Long id) {
        Optional<ReminderLog> logOpt = logRepository.findById(id);
        return logOpt.map(this::convertToDTO);
    }

    // Eliminar log
    public ResponseDTO deleteLog(Long id) {
        Optional<ReminderLog> logOpt = logRepository.findById(id);
        if (logOpt.isEmpty()) {
            return new ResponseDTO(HttpStatus.NOT_FOUND.toString(), "Log not found");
        }
        logRepository.deleteById(id);
        return new ResponseDTO(HttpStatus.OK.toString(), "Log deleted successfully");
    }

    // Convertir entidad a DTO
    private LogDto convertToDTO(ReminderLog log) {
        return new LogDto(
                log.getId(),
                log.getAssignment() != null ? log.getAssignment().getPatient().getId() : null,
                log.getAssignment() != null ? log.getAssignment().getMedication().getId() : null,
                log.getReminderSentAt(),
                log.isConfirmed() ? "Confirmed" : "Pending"
        );
    }

    // Convertir DTO a entidad
    private ReminderLog convertToEntity(LogDto dto) {
        ReminderLog log = new ReminderLog();
        log.setId(dto.getId());
        
        // Aquí debes implementar la lógica para asignar la Assignment 
        // basada en patientId y medicineId, probablemente con otro repo o servicio.
        // Por ahora, lo dejamos null o lanzar excepción si es necesario.
        log.setAssignment(null);

        log.setReminderSentAt(dto.getReminderSentAt());
        log.setConfirmed("Confirmed".equalsIgnoreCase(dto.getMessage()));
        // No incluiste confirmationTime en el DTO, si quieres agregarlo, puedes.
        return log;
    }
}
