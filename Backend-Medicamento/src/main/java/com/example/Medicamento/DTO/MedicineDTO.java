package com.example.Medicamento.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDTO {
    private Long id;
    private String name;
    private String dose;              // e.g., "1 tablet", "5 ml"
    private Integer frequencyHours;  // Cambiado a Integer
    private String instructions;
    private LocalTime startTime;
    private Integer treatmentDurationDays; // Cambiado a Integer
}
