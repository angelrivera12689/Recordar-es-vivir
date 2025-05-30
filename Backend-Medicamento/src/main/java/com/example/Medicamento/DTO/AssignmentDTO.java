package com.example.Medicamento.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private Long id;
    private Long patientId;
    private Long medicineId;
    private Integer frequencyMinutes;
    private LocalDateTime startTime;
}
