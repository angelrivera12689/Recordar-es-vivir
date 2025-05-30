package com.example.Medicamento.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationDTO {
    private Long id;
    private Long patientId;
    private Long medicineId;
    private LocalDateTime confirmedAt;
    private boolean confirmed;  // true if patient confirmed taking the medicine
}
