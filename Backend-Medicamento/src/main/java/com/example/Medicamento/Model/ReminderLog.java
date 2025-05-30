package com.example.Medicamento.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(nullable = false)
    private LocalDateTime reminderSentAt;

    @Column(nullable = false)
    private boolean confirmed;

    private LocalDateTime confirmationTime;

    @Column(unique = true)
    private String confirmationToken;

    // Nuevo campo para controlar el reinicio diario
  @Column(nullable = true) // Cambiado temporalmente a true
private LocalDate logDate;// Fecha específica del recordatorio (no de creación)

    @PrePersist
    protected void onCreate() {
        if (logDate == null) {
            logDate = LocalDate.now();
        }
        if (reminderSentAt == null) {
            reminderSentAt = LocalDateTime.now();
        }
    }
}