package com.example.Medicamento.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogDto {
    private Long id;
    private Long patientId;
    private Long medicineId;
    private LocalDateTime reminderSentAt;
    private String message;
}
