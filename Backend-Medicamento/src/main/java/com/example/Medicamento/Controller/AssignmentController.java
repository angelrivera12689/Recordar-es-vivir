package com.example.Medicamento.Controller;

import com.example.Medicamento.DTO.AssignmentDTO;
import com.example.Medicamento.DTO.ResponseDTO;
import com.example.Medicamento.Service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/assignments")
@CrossOrigin(origins = "*")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    // Crear una asignación (paciente ↔ medicamento)
    @PostMapping
    public ResponseEntity<ResponseDTO> createAssignment(@RequestBody AssignmentDTO dto) {
        ResponseDTO response = assignmentService.createAssignment(dto);
        if (response.getStatus().contains("200")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Obtener todas las asignaciones
    @GetMapping
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments() {
        List<AssignmentDTO> assignments = assignmentService.getAllAssignmentsDTO();
        return ResponseEntity.ok(assignments);
    }

    // Obtener una asignación por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable Long id) {
        Optional<AssignmentDTO> assignmentOpt = assignmentService.getAssignmentByIdDTO(id);
        return assignmentOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(new ResponseDTO("404", "Assignment not found")));
    }

    // Actualizar una asignación
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateAssignment(@PathVariable Long id, @RequestBody AssignmentDTO dto) {
        ResponseDTO response = assignmentService.updateAssignment(id, dto);
        if (response.getStatus().contains("200")) {
            return ResponseEntity.ok(response);
        } else if (response.getStatus().contains("404")) {
            return ResponseEntity.status(404).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Eliminar una asignación
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteAssignment(@PathVariable Long id) {
        ResponseDTO response = assignmentService.deleteAssignment(id);
        if (response.getStatus().contains("200")) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).body(response);
        }
    }
}
