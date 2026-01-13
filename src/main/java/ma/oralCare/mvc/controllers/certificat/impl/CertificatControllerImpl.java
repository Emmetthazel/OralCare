package ma.oralCare.mvc.controllers.certificat.impl;

import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.mvc.controllers.certificat.api.CertificatController;
import ma.oralCare.mvc.ui1.medecin.CertificateView;
import ma.oralCare.service.modules.certificat.api.CertificatService;
import ma.oralCare.service.modules.certificat.impl.CertificatServiceImpl;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CertificatControllerImpl implements CertificatController {
    
    private final CertificateView view;
    private final CertificatService certificatService;
    
    public CertificatControllerImpl(CertificateView view) {
        this.view = view;
        this.certificatService = new CertificatServiceImpl();
    }
    
    @Override
    public void refreshView() {
        // Rafraîchit la vue avec les certificats
        // À implémenter selon CertificateView
    }
    
    @Override
    public void handleCreateCertificat(Long consultationId, LocalDate dateDebut, int duree, String noteMedecin) {
        if (consultationId == null) {
            JOptionPane.showMessageDialog(view, "Consultation requise", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Certificat certificat = Certificat.builder()
                    .dateDebut(dateDebut != null ? dateDebut : LocalDate.now())
                    .dateFin(dateDebut != null ? dateDebut.plusDays(duree) : LocalDate.now().plusDays(duree))
                    .duree(duree)
                    .noteMedecin(noteMedecin)
                    .build();
            
            // TODO: Assigner la consultation
            
            certificatService.createCertificat(certificat);
            refreshView();
            JOptionPane.showMessageDialog(view, "Certificat créé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la création: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleSelectCertificat(Long certificatId) {
        if (certificatId == null) return;
        
        try {
            Optional<Certificat> certificatOpt = certificatService.getCertificatById(certificatId);
            if (certificatOpt.isPresent()) {
                // Mettre à jour la vue avec le certificat sélectionné
                // À implémenter selon CertificateView
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors du chargement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleDeleteCertificat(Long certificatId) {
        if (certificatId == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous vraiment supprimer ce certificat ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                certificatService.deleteCertificat(certificatId);
                refreshView();
                JOptionPane.showMessageDialog(view, "Certificat supprimé avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    public List<Certificat> getCertificatsByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return certificatService.getCertificatsByDossierId(dossierId);
    }
    
    @Override
    public List<Certificat> getCertificatsByConsultationId(Long consultationId) {
        if (consultationId == null) return List.of();
        // À implémenter dans le service si nécessaire
        return List.of();
    }
    
    @Override
    public Optional<Certificat> getCertificatById(Long id) {
        if (id == null) return Optional.empty();
        return certificatService.getCertificatById(id);
    }
    
    @Override
    public void handleSaveCertificat(Certificat certificat) {
        if (certificat == null) return;
        
        try {
            if (certificat.getIdEntite() == null) {
                certificatService.createCertificat(certificat);
            } else {
                certificatService.updateCertificat(certificat);
            }
            refreshView();
            JOptionPane.showMessageDialog(view, "Certificat enregistré avec succès",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de l'enregistrement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
