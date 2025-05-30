package com.example.Medicamento.Repository;

import com.example.Medicamento.Model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medication, Long> {
}
