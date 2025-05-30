package com.example.Medicamento.Controller;

import com.example.Medicamento.DTO.PatientDTO;
import com.example.Medicamento.DTO.ResponseDTO;
import com.example.Medicamento.Service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // Obtener todos los pacientes
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    // Obtener paciente por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        Optional<PatientDTO> patientOpt = patientService.getPatientById(id);
        if (patientOpt.isPresent()) {
            return ResponseEntity.ok(patientOpt.get());
        }
        return ResponseEntity.status(404).body(new ResponseDTO("404", "Patient not found"));
    }

    // Crear paciente
   @PostMapping
public ResponseEntity<?> createPatient(@RequestBody PatientDTO patientDTO) {
    PatientDTO createdPatient = patientService.createPatient(patientDTO);
    if (createdPatient != null) {
        // Retornar paciente creado con status 201
        return ResponseEntity.status(201).body(createdPatient);
    } else {
        // En caso de error (null), retorna 400 con mensaje
        return ResponseEntity.badRequest()
                .body(new ResponseDTO("400", "Error creating patient"));
    }
}


    // Actualizar paciente
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updatePatient(@PathVariable Long id, @RequestBody PatientDTO patientDTO) {
        ResponseDTO response = patientService.updatePatient(id, patientDTO);
        int httpStatus = parseStatusCode(response.getStatus());
        return ResponseEntity.status(httpStatus).body(response);
    }

    // Suspender (eliminar lógico) paciente
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deletePatient(@PathVariable Long id) {
        ResponseDTO response = patientService.deletePatient(id);
        int httpStatus = parseStatusCode(response.getStatus());
        // Si la operación fue exitosa (ej. status 200), devolver No Content
        if (httpStatus == 200) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(httpStatus).body(response);
    }

    // Helper para convertir string status (ej. "200") a int código HTTP
    private int parseStatusCode(String status) {
        try {
            return Integer.parseInt(status);
        } catch (NumberFormatException e) {
            return 200; // Default OK si algo falla
        }
    }
}
