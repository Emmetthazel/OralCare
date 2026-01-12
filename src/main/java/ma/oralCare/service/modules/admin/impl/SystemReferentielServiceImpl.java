package ma.oralCare.service.modules.admin.impl;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.repository.modules.dossierMedical.api.ActeRepository;
import ma.oralCare.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.oralCare.repository.modules.patient.api.AntecedentRepository;
import ma.oralCare.service.modules.admin.api.SystemReferentielService;

import java.time.LocalDateTime;
import java.util.List;

public class SystemReferentielServiceImpl implements SystemReferentielService {

    // Supposons que ces repositories sont injectés via constructeur ou framework
    private final MedicamentRepository medicamentRepository;
    private final AntecedentRepository antecedentRepository;
    private final ActeRepository acteRepository;

    public SystemReferentielServiceImpl(MedicamentRepository medicamentRepository,
                                        AntecedentRepository antecedentRepository,
                                        ActeRepository acteRepository) {
        this.medicamentRepository = medicamentRepository;
        this.antecedentRepository = antecedentRepository;
        this.acteRepository = acteRepository;
    }

    // --- GESTION MÉDICAMENTS ---
    @Override
    public List<Medicament> getAllMedicaments() {
        return medicamentRepository.findAll();
    }

    @Override
    public void saveMedicament(Medicament medicament) {
        // 1. Gestion des métadonnées de base
        if (medicament.getIdEntite() == null) {
            medicament.setDateCreation(LocalDateTime.now());
            // TODO: Récupérer l'ID de l'admin connecté via une session ou un contexte
            medicament.setCreePar(1L);
        } else {
            medicament.setDateDerniereModification(LocalDateTime.now());
            medicament.setModifiePar(1L);
        }

        // 2. Appel au repository (l'erreur "save" disparaîtra après l'étape 1 et 2)
        medicamentRepository.save(medicament);
    }

    @Override
    public void deleteMedicament(Long id) {
        medicamentRepository.deleteById(id);
    }

    // --- GESTION ANTÉCÉDENTS ---
    @Override
    public List<Antecedent> getAllAntecedents() {
        return antecedentRepository.findAll();
    }

    @Override
    public void saveAntecedent(Antecedent antecedent) {
        if (antecedent.getIdEntite() == null) {
            antecedent.setDateCreation(LocalDateTime.now());
        }
        antecedent.setDateDerniereModification(LocalDateTime.now());
        antecedentRepository.save(antecedent);
    }

    @Override
    public void deleteAntecedent(Long id) {
        antecedentRepository.deleteById(id);
    }

    // --- GESTION ACTES ---
    @Override
    public List<Acte> getAllActes() {
        return acteRepository.findAll();
    }

    @Override
    public void saveActe(Acte acte) {
        if (acte.getIdEntite() == null) {
            acte.setDateCreation(LocalDateTime.now());
        }
        acte.setDateDerniereModification(LocalDateTime.now());
        acteRepository.save(acte);
    }

    @Override
    public void deleteActe(Long id) {
        acteRepository.deleteById(id);
    }
}