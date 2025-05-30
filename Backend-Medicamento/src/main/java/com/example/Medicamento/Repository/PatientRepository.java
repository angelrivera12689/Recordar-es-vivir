package com.example.Medicamento.Repository;

import com.example.Medicamento.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByStatus(Patient.Status status);
    Optional<Patient> findByIdAndStatus(Long id, Patient.Status status);
}

