package com.example.Medicamento.Service;

import com.example.Medicamento.DTO.PatientDTO;
import com.example.Medicamento.DTO.ResponseDTO;
import com.example.Medicamento.Model.Patient;
import com.example.Medicamento.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    private PatientDTO convertToDTO(Patient patient) {
        return new PatientDTO(
            patient.getId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getEmail(),
            patient.getPhone(),
            patient.getStatus() != null ? patient.getStatus().name() : null,
            patient.getRegistrationDate()
        );
    }

    private Patient convertToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        // Si quieres permitir actualizar el estado, puedes agregarlo aquí:
        if (dto.getStatus() != null) {
            try {
                patient.setStatus(Patient.Status.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                // Status inválido, puedes manejar el error si quieres
                patient.setStatus(Patient.Status.ACTIVE);
            }
        }
        return patient;
    }

   public PatientDTO createPatient(PatientDTO patientDTO) {
    Patient patient = convertToEntity(patientDTO);
    patient.setStatus(Patient.Status.ACTIVE);
    patient.setRegistrationDate(LocalDate.now()); // Fecha automática
    Patient savedPatient = patientRepository.save(patient);
    return convertToDTO(savedPatient);  // Devuelve el paciente guardado como DTO
}


    public List<PatientDTO> getAllPatients() {
        return patientRepository.findByStatus(Patient.Status.ACTIVE)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PatientDTO> getPatientById(Long id) {
        return patientRepository.findByIdAndStatus(id, Patient.Status.ACTIVE)
                .map(this::convertToDTO);
    }

    public ResponseDTO updatePatient(Long id, PatientDTO updatedPatientDTO) {
        Optional<Patient> patientOpt = patientRepository.findById(id);

        if (patientOpt.isEmpty()) {
            return new ResponseDTO(HttpStatus.NOT_FOUND.toString(), "Patient not found");
        }

        Patient patient = patientOpt.get();

        if (patient.getStatus() == Patient.Status.SUSPENDED) {
            return new ResponseDTO(HttpStatus.FORBIDDEN.toString(), "Cannot update a suspended patient");
        }

        if (updatedPatientDTO.getFirstName() != null) patient.setFirstName(updatedPatientDTO.getFirstName());
        if (updatedPatientDTO.getLastName() != null) patient.setLastName(updatedPatientDTO.getLastName());
        if (updatedPatientDTO.getEmail() != null) patient.setEmail(updatedPatientDTO.getEmail());
        if (updatedPatientDTO.getPhone() != null) patient.setPhone(updatedPatientDTO.getPhone());
        // Opcional: actualizar estado si se pasa (asegúrate que sea un valor válido)
        if (updatedPatientDTO.getStatus() != null) {
            try {
                patient.setStatus(Patient.Status.valueOf(updatedPatientDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), "Invalid status value");
            }
        }

        patientRepository.save(patient);
        return new ResponseDTO(HttpStatus.OK.toString(), "Patient updated successfully");
    }

    public ResponseDTO deletePatient(Long id) {
        Optional<Patient> patientOpt = patientRepository.findById(id);

        if (patientOpt.isEmpty()) {
            return new ResponseDTO(HttpStatus.NOT_FOUND.toString(), "Patient not found");
        }

        Patient patient = patientOpt.get();

        if (patient.getStatus() == Patient.Status.SUSPENDED) {
            return new ResponseDTO(HttpStatus.BAD_REQUEST.toString(), "Patient is already suspended");
        }

        patient.setStatus(Patient.Status.SUSPENDED);
        patientRepository.save(patient);

        return new ResponseDTO(HttpStatus.OK.toString(), "Patient suspended successfully");
    }
}
