package com.example.Medicamento.Controller;

import com.example.Medicamento.DTO.MedicineDTO;
import com.example.Medicamento.DTO.ResponseDTO;
import com.example.Medicamento.Service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medications")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    // Crear medicamento
    @PostMapping
    public ResponseEntity<ResponseDTO> createMedication(@RequestBody MedicineDTO dto) {
        ResponseDTO response = medicationService.createMedication(dto);
        if (response.getStatus().startsWith("200") || response.getStatus().startsWith("201")) {
            return ResponseEntity.status(201).body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // Obtener todos los medicamentos
    @GetMapping
    public ResponseEntity<List<MedicineDTO>> getAllMedications() {
        List<MedicineDTO> medications = medicationService.getAllMedicationsDTO();
        return ResponseEntity.ok(medications);
    }

    // Obtener medicamento por id
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicationById(@PathVariable Long id) {
        Optional<MedicineDTO> medicationOpt = medicationService.getMedicationByIdDTO(id);
        return medicationOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Medication not found"));
    }

    // Actualizar medicamento
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateMedication(@PathVariable Long id, @RequestBody MedicineDTO dto) {
        ResponseDTO response = medicationService.updateMedication(id, dto);
        if (response.getStatus().startsWith("200")) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().startsWith("404")) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // Eliminar medicamento
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteMedication(@PathVariable Long id) {
        ResponseDTO response = medicationService.deleteMedication(id);
        if (response.getStatus().startsWith("200")) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().startsWith("404")) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
