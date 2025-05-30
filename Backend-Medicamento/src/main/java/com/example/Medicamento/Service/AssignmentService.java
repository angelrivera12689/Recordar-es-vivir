package com.example.Medicamento.Service;

import com.example.Medicamento.DTO.AssignmentDTO;
import com.example.Medicamento.DTO.ResponseDTO;
import com.example.Medicamento.Model.Assignment;
import com.example.Medicamento.Model.Medication;
import com.example.Medicamento.Model.Patient;
import com.example.Medicamento.Repository.MedicineAssignmentRepository;
import com.example.Medicamento.Repository.MedicineRepository;
import com.example.Medicamento.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    @Autowired
    private MedicineAssignmentRepository assignmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    // Crear Assignment
    public ResponseDTO createAssignment(AssignmentDTO dto) {
        try {
            Assignment assignment = convertToEntity(dto);
            assignmentRepository.save(assignment);
            return new ResponseDTO(HttpStatus.OK.toString(), "Assignment created successfully");
        } catch (Exception e) {
            return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), "Error creating assignment: " + e.getMessage());
        }
    }

    // Obtener todos los assignments como DTO
    public List<AssignmentDTO> getAllAssignmentsDTO() {
        List<Assignment> assignments = assignmentRepository.findAll();
        return assignments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Obtener assignment por id como DTO
    public Optional<AssignmentDTO> getAssignmentByIdDTO(Long id) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        return assignmentOpt.map(this::convertToDTO);
    }

    // Actualizar assignment
    public ResponseDTO updateAssignment(Long id, AssignmentDTO updatedDTO) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isEmpty()) {
            return new ResponseDTO(HttpStatus.NOT_FOUND.toString(), "Assignment not found");
        }

        Assignment assignment = assignmentOpt.get();

        // Actualizar paciente si existe
        Optional<Patient> patientOpt = patientRepository.findById(updatedDTO.getPatientId());
        if (patientOpt.isEmpty()) {
            return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), "Patient not found");
        }

        // Actualizar medicamento si existe
        Optional<Medication> medicationOpt = medicineRepository.findById(updatedDTO.getMedicineId());
        if (medicationOpt.isEmpty()) {
            return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), "Medication not found");
        }

        // Actualizar todos los campos
        assignment.setPatient(patientOpt.get());
        assignment.setMedication(medicationOpt.get());
        assignment.setFrequencyMinutes(updatedDTO.getFrequencyMinutes());
        assignment.setStartTime(updatedDTO.getStartTime());

        assignmentRepository.save(assignment);
        return new ResponseDTO(HttpStatus.OK.toString(), "Assignment updated successfully");
    }

    // Eliminar assignment
    public ResponseDTO deleteAssignment(Long id) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isEmpty()) {
            return new ResponseDTO(HttpStatus.NOT_FOUND.toString(), "Assignment not found");
        }
        assignmentRepository.deleteById(id);
        return new ResponseDTO(HttpStatus.OK.toString(), "Assignment deleted successfully");
    }

    // Convertir entidad a DTO
    private AssignmentDTO convertToDTO(Assignment assignment) {
        return new AssignmentDTO(
                assignment.getId(),
                assignment.getPatient() != null ? assignment.getPatient().getId() : null,
                assignment.getMedication() != null ? assignment.getMedication().getId() : null,
                assignment.getFrequencyMinutes(),
                assignment.getStartTime()
        );
    }

    // Convertir DTO a entidad (CORREGIDO)
    private Assignment convertToEntity(AssignmentDTO dto) throws Exception {
        Assignment assignment = new Assignment();

        if (dto.getId() != null) {
            assignment.setId(dto.getId());
        }

        Optional<Patient> patientOpt = patientRepository.findById(dto.getPatientId());
        if (patientOpt.isEmpty()) {
            throw new Exception("Patient not found");
        }

        Optional<Medication> medicationOpt = medicineRepository.findById(dto.getMedicineId());
        if (medicationOpt.isEmpty()) {
            throw new Exception("Medication not found");
        }

        // Asignar todos los campos (CORRECCIÓN PRINCIPAL)
        assignment.setPatient(patientOpt.get());
        assignment.setMedication(medicationOpt.get());
        assignment.setFrequencyMinutes(dto.getFrequencyMinutes());
        assignment.setStartTime(dto.getStartTime());

        return assignment;
    }
}