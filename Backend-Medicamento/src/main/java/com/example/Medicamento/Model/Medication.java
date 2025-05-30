package com.example.Medicamento.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String dose; // e.g., "1 tablet", "5 ml"
    private int frequencyHours; // cada cuántas horas

    private String instructions;

    private LocalTime startTime;

    private int treatmentDurationDays; // duración en días
}
