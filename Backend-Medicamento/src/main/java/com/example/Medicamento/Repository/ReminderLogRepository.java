package com.example.Medicamento.Repository;

import com.example.Medicamento.Model.Assignment;
import com.example.Medicamento.Model.ReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {

    // Métodos existentes que deben mantenerse...
    boolean existsByAssignmentAndReminderSentAt(Assignment assignment, LocalDateTime reminderSentAt);
    Optional<ReminderLog> findTopByAssignment_IdOrderByReminderSentAtDesc(Long assignmentId);
    Optional<ReminderLog> findByConfirmationToken(String confirmationToken);
    Optional<ReminderLog> findByAssignmentAndReminderSentAt(Assignment assignment, LocalDateTime reminderSentAt);
    boolean existsByAssignmentAndConfirmedTrueAndConfirmationTimeBetween(Assignment assignment, LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    @Query("SELECT r FROM ReminderLog r WHERE r.assignment.id = :assignmentId ORDER BY r.reminderSentAt DESC LIMIT 1")
    Optional<ReminderLog> findLatestByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT r FROM ReminderLog r WHERE r.assignment.id = :assignmentId AND r.confirmed = true ORDER BY r.reminderSentAt DESC LIMIT 1")
    Optional<ReminderLog> findLatestConfirmedByAssignmentId(@Param("assignmentId") Long assignmentId);

    // ===== MÉTODOS NUEVOS PARA EL REINICIO DIARIO =====
    
    /**
     * Encuentra todos los logs confirmados de una fecha específica
     * (Para el reinicio diario de confirmaciones)
     */
    @Query("SELECT r FROM ReminderLog r WHERE r.logDate = :date AND r.confirmed = true")
    List<ReminderLog> findByConfirmedTrueAndLogDate(@Param("date") LocalDate date);
    
    /**
     * Verifica si existe un log para una asignación en una fecha específica
     * (Para evitar duplicados en el recordatorio diario)
     */
    boolean existsByAssignmentAndLogDate(Assignment assignment, LocalDate date);
    
    /**
     * Encuentra logs por fecha (para el reinicio diario)
     */
    List<ReminderLog> findByLogDate(LocalDate date);
    
    /**
     * Encuentra el último log no confirmado para una asignación en la fecha actual
     * (Para enviar recordatorios de seguimiento)
     */
    @Query("SELECT r FROM ReminderLog r WHERE r.assignment.id = :assignmentId AND r.logDate = CURRENT_DATE AND r.confirmed = false ORDER BY r.reminderSentAt DESC LIMIT 1")
    Optional<ReminderLog> findTodayUnconfirmedByAssignmentId(@Param("assignmentId") Long assignmentId);
}