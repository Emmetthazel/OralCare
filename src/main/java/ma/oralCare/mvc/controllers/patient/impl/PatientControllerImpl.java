package ma.oralCare.mvc.controllers.patient.impl;

import ma.oralCare.entities.enums.Assurance;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.mvc.controllers.patient.api.PatientController;
import ma.oralCare.mvc.ui1.medecin.PatientListView;
import ma.oralCare.service.modules.patient.api.PatientService;
import ma.oralCare.service.modules.patient.dto.PatientCreateRequest;
import ma.oralCare.service.modules.patient.dto.PatientUpdateRequest;
import ma.oralCare.service.modules.patient.impl.PatientServiceImpl;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientControllerImpl implements PatientController {
    
    private final PatientListView view;
    private final PatientService patientService;
    
    public PatientControllerImpl(PatientListView view) {
        this.view = view;
        this.patientService = new PatientServiceImpl();
    }
    
    @Override
    public void refreshView() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            // Mettre à jour la vue avec les patients
            // À implémenter selon PatientListView
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors du chargement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleSearchPatient(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            refreshView();
            return;
        }
        
        try {
            List<Patient> patients = patientService.searchPatientsByNomPrenom(searchTerm);
            // Mettre à jour la vue avec les résultats
            // À implémenter selon PatientListView
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la recherche: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleSelectPatient(Long patientId) {
        if (patientId == null) return;
        
        try {
            Optional<Patient> patientOpt = patientService.getPatientById(patientId);
            if (patientOpt.isPresent()) {
                // Mettre à jour la vue avec le patient sélectionné
                // À implémenter selon PatientListView
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors du chargement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleCreatePatient(Patient patient) {
        if (patient == null) return;
        
        try {
            patientService.createPatient(patient);
            refreshView();
            JOptionPane.showMessageDialog(view, "Patient créé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la création: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public Patient createPatient(PatientCreateRequest request) {
        if (request == null) return null;
        
        try {
            // Convertir PatientCreateRequest en Patient entity
            Patient patient = new Patient();
            patient.setNom(request.getNom());
            patient.setPrenom(request.getPrenom());
            patient.setDateDeNaissance(request.getDateNaissance());
            patient.setTelephone(request.getTelephone());
            patient.setEmail(request.getEmail());
            
            // Construire l'adresse à partir des champs du DTO
            String adresseComplete = "";
            if (request.getNumero() != null) adresseComplete += request.getNumero() + " ";
            if (request.getRue() != null) adresseComplete += request.getRue() + ", ";
            if (request.getCodePostal() != null) adresseComplete += request.getCodePostal() + " ";
            if (request.getVille() != null) adresseComplete += request.getVille() + " ";
            if (request.getPays() != null) adresseComplete += request.getPays();
            patient.setAdresse(adresseComplete.trim());
            
            // Convertir l'assurance String en enum Assurance
            Assurance assuranceEnum = Assurance.NONE;
            if (request.getAssurance() != null) {
                for (Assurance a : Assurance.values()) {
                    if (a.getLibelle().equalsIgnoreCase(request.getAssurance())) {
                        assuranceEnum = a;
                        break;
                    }
                }
            }
            patient.setAssurance(assuranceEnum);
            
            // Le numéro de sécurité sociale n'existe pas dans l'entité Patient
            // On peut l'ignorer ou le stocker dans un autre champ si nécessaire
            Patient createdPatient = patientService.createPatient(patient);
            refreshView();
            JOptionPane.showMessageDialog(view, "Patient créé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
            return createdPatient;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la création: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    @Override
    public void handleDeletePatient(Long patientId) {
        if (patientId == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous vraiment supprimer ce patient ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                patientService.deletePatient(patientId);
                refreshView();
                JOptionPane.showMessageDialog(view, "Patient supprimé avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }
    
    @Override
    public Optional<Patient> getPatientById(Long id) {
        if (id == null) return Optional.empty();
        return patientService.getPatientById(id);
    }
    
    @Override
    public List<Patient> searchPatientsByNomPrenom(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        return patientService.searchPatientsByNomPrenom(keyword);
    }
    
    @Override
    public Patient updatePatient(Long patientId, PatientUpdateRequest request) {
        if (patientId == null) return null;
        if (request == null) return null;
        
        try {
            // Récupérer le patient existant
            Optional<Patient> existingPatientOpt = patientService.getPatientById(patientId);
            if (existingPatientOpt.isEmpty()) {
                throw new IllegalArgumentException("Patient non trouvé avec l'ID: " + patientId);
            }
            
            Patient existingPatient = existingPatientOpt.get();
            
            // Mettre à jour les champs modifiables
            if (request.getNom() != null) existingPatient.setNom(request.getNom());
            if (request.getPrenom() != null) existingPatient.setPrenom(request.getPrenom());
            if (request.getDateNaissance() != null) existingPatient.setDateDeNaissance(request.getDateNaissance());
            if (request.getTelephone() != null) existingPatient.setTelephone(request.getTelephone());
            if (request.getEmail() != null) existingPatient.setEmail(request.getEmail());
            
            // Mettre à jour l'adresse
            String adresseComplete = "";
            if (request.getNumero() != null) adresseComplete += request.getNumero() + " ";
            if (request.getRue() != null) adresseComplete += request.getRue() + ", ";
            if (request.getCodePostal() != null) adresseComplete += request.getCodePostal() + " ";
            if (request.getVille() != null) adresseComplete += request.getVille() + " ";
            if (request.getPays() != null) adresseComplete += request.getPays();
            existingPatient.setAdresse(adresseComplete.trim());
            
            // Mettre à jour l'assurance
            if (request.getAssurance() != null) {
                for (Assurance a : Assurance.values()) {
                    if (a.getLibelle().equalsIgnoreCase(request.getAssurance())) {
                        existingPatient.setAssurance(a);
                        break;
                    }
                }
            }
            
            Patient updatedPatient = patientService.updatePatient(existingPatient);
            refreshView();
            JOptionPane.showMessageDialog(view, "Patient mis à jour avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
            return updatedPatient;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la mise à jour: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    @Override
    public void handleUpdatePatient(Patient patient) {
        if (patient == null || patient.getIdEntite() == null) return;
        
        try {
            patientService.updatePatient(patient);
            refreshView();
            JOptionPane.showMessageDialog(view, "Patient mis à jour avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la mise à jour: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void deletePatient(Long patientId) {
        if (patientId == null) return;
        
        try {
            patientService.deletePatient(patientId);
            refreshView();
            JOptionPane.showMessageDialog(view, "Patient supprimé avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la suppression: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public String exportPatientsToCSV(List<Patient> patients) {
        // Implémenter l'export CSV
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Nom,Prénom,Date de naissance,Téléphone,Email,Adresse,Assurance\n");
        
        for (Patient patient : patients) {
            csv.append(patient.getIdEntite()).append(",");
            csv.append(patient.getNom()).append(",");
            csv.append(patient.getPrenom()).append(",");
            csv.append(patient.getDateDeNaissance()).append(",");
            csv.append(patient.getTelephone()).append(",");
            csv.append(patient.getEmail()).append(",");
            csv.append(patient.getAdresse()).append(",");
            csv.append(patient.getAssurance() != null ? patient.getAssurance().getLibelle() : "").append("\n");
        }
        
        return csv.toString();
    }
    
    @Override
    public List<Patient> importPatientsFromCSV(String csvContent) {
        // Implémenter l'import CSV
        List<Patient> patients = new ArrayList<>();
        String[] lines = csvContent.split("\n");
        
        // Skip header
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            if (fields.length >= 8) {
                Patient patient = new Patient();
                patient.setNom(fields[1].trim());
                patient.setPrenom(fields[2].trim());
                patient.setDateDeNaissance(LocalDate.parse(fields[3].trim()));
                patient.setTelephone(fields[4].trim());
                patient.setEmail(fields[5].trim());
                
                // Construire l'adresse à partir des champs CSV
                String adresseComplete = "";
                if (fields.length > 6 && fields[6] != null) adresseComplete += fields[6].trim() + " ";
                if (fields.length > 7 && fields[7] != null) adresseComplete += fields[7].trim() + ", ";
                if (fields.length > 8 && fields[8] != null) adresseComplete += fields[8].trim() + " ";
                if (fields.length > 9 && fields[9] != null) adresseComplete += fields[9].trim() + " ";
                if (fields.length > 10 && fields[10] != null) adresseComplete += fields[10].trim();
                patient.setAdresse(adresseComplete.trim());
                
                // Convertir l'assurance String en enum
                Assurance assuranceEnum = Assurance.NONE;
                if (fields.length > 7 && fields[7] != null) {
                    for (Assurance a : Assurance.values()) {
                        if (a.getLibelle().equalsIgnoreCase(fields[7].trim())) {
                            assuranceEnum = a;
                            break;
                        }
                    }
                }
                patient.setAssurance(assuranceEnum);
                
                patients.add(patient);
            }
        }
        
        return patients;
    }
}
