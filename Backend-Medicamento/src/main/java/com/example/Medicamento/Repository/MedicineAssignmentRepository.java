package com.example.Medicamento.Repository;

import com.example.Medicamento.Model.Assignment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineAssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findBySuspendedFalse();
}
