package ma.oralCare.mvc.controllers.patient.api;

import ma.oralCare.entities.patient.Patient;
import ma.oralCare.service.modules.patient.dto.PatientCreateRequest;
import ma.oralCare.service.modules.patient.dto.PatientUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface PatientController {
    
    void refreshView();
    
    void handleSearchPatient(String searchTerm);
    
    void handleSelectPatient(Long patientId);
    
    void handleCreatePatient(Patient patient);
    
    void handleUpdatePatient(Patient patient);
    
    void handleDeletePatient(Long patientId);
    
    List<Patient> getAllPatients();
    
    Optional<Patient> getPatientById(Long id);
    
    List<Patient> searchPatientsByNomPrenom(String keyword);
    
    // CRUD methods with DTOs
    Patient createPatient(PatientCreateRequest request);
    
    Patient updatePatient(Long patientId, PatientUpdateRequest request);
    
    void deletePatient(Long patientId);
    
    // Export/Import methods
    String exportPatientsToCSV(List<Patient> patients);
    
    List<Patient> importPatientsFromCSV(String csvContent);
}
