package com.example.Medicamento.Service;

import com.example.Medicamento.DTO.MedicineDTO;
import com.example.Medicamento.DTO.ResponseDTO;
import com.example.Medicamento.Model.Medication;
import com.example.Medicamento.Repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicationService {

    @Autowired
    private MedicineRepository medicationRepository;

    // Crear medicamento con validación básica
    public ResponseDTO createMedication(MedicineDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), "Medication name is required");
        }
        // Agrega más validaciones según necesites...

        Medication medication = convertToEntity(dto);
        medicationRepository.save(medication);
        return new ResponseDTO(HttpStatus.CREATED.toString(), "Medication created successfully");
    }

    // Obtener todos los medicamentos como DTOs
    public List<MedicineDTO> getAllMedicationsDTO() {
        List<Medication> medications = medicationRepository.findAll();
        return medications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Obtener medicamento por ID como DTO
    public Optional<MedicineDTO> getMedicationByIdDTO(Long id) {
        return medicationRepository.findById(id).map(this::convertToDTO);
    }

    // Actualizar medicamento, solo campos no nulos
    public ResponseDTO updateMedication(Long id, MedicineDTO updatedDTO) {
        Optional<Medication> medOpt = medicationRepository.findById(id);
        if (medOpt.isEmpty()) {
            return new ResponseDTO(HttpStatus.NOT_FOUND.toString(), "Medication not found");
        }

        Medication medication = medOpt.get();

        if (updatedDTO.getName() != null && !updatedDTO.getName().trim().isEmpty()) {
            medication.setName(updatedDTO.getName());
        }
        if (updatedDTO.getFrequencyHours() != null && updatedDTO.getFrequencyHours() > 0) {
            medication.setFrequencyHours(updatedDTO.getFrequencyHours());
        }
        if (updatedDTO.getInstructions() != null) {
            medication.setInstructions(updatedDTO.getInstructions());
        }
        if (updatedDTO.getStartTime() != null) {
            medication.setStartTime(updatedDTO.getStartTime());
        }
        if (updatedDTO.getTreatmentDurationDays() != null && updatedDTO.getTreatmentDurationDays() > 0) {
            medication.setTreatmentDurationDays(updatedDTO.getTreatmentDurationDays());
        }

        medicationRepository.save(medication);
        return new ResponseDTO(HttpStatus.OK.toString(), "Medication updated successfully");
    }

    // Eliminar medicamento
    public ResponseDTO deleteMedication(Long id) {
        Optional<Medication> medOpt = medicationRepository.findById(id);
        if (medOpt.isEmpty()) {
            return new ResponseDTO(HttpStatus.NOT_FOUND.toString(), "Medication not found");
        }
        medicationRepository.deleteById(id);
        return new ResponseDTO(HttpStatus.OK.toString(), "Medication deleted successfully");
    }

    // Convertir entidad a DTO
    private MedicineDTO convertToDTO(Medication medication) {
        return new MedicineDTO(
                medication.getId(),
                medication.getName(),
                medication.getDose(),
                medication.getFrequencyHours(),
                medication.getInstructions(),
                medication.getStartTime(),
                medication.getTreatmentDurationDays()
        );
    }

    // Convertir DTO a entidad
    private Medication convertToEntity(MedicineDTO dto) {
        Medication medication = new Medication();
        medication.setId(dto.getId());
        medication.setName(dto.getName());
        medication.setDose(dto.getDose());
        medication.setFrequencyHours(dto.getFrequencyHours());
        medication.setInstructions(dto.getInstructions());
        medication.setStartTime(dto.getStartTime());
        medication.setTreatmentDurationDays(dto.getTreatmentDurationDays());
        return medication;
    }
}
