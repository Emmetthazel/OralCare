package ma.oralCare.mvc.controllers.admin.api;

import java.util.List;
import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.patient.Antecedent;

public interface SystemReferentielController {
    // Actions pour les Médicaments
    void addMedicament(String nom, String labo, String type, String forme, boolean remboursable, String prix);
    List<Medicament> loadMedicaments();

    // Actions pour les Antécédents
    void addAntecedent(String nom, String categorie, String risque);
    List<Antecedent> loadAntecedents();

    // Actions pour les Actes
    void addActe(String libelle, String categorie, String prix);
    List<Acte> loadActes();

    // Action de suppression générique
    void deleteEntity(String type, Long id);
    void updateMedicament(Medicament m);
    void updateActe(Acte a);
    void updateAntecedent(Antecedent ant);

}