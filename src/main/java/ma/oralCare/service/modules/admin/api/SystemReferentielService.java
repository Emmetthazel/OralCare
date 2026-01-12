package ma.oralCare.service.modules.admin.api;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.patient.Antecedent;

import java.util.List;

public interface SystemReferentielService {
    // CRUD Médicaments
    List<Medicament> getAllMedicaments();
    void saveMedicament(Medicament medicament);
    void deleteMedicament(Long id);

    // CRUD Antécédents
    List<Antecedent> getAllAntecedents();
    void saveAntecedent(Antecedent antecedent);
    void deleteAntecedent(Long id);

    // CRUD Actes
    List<Acte> getAllActes();
    void saveActe(Acte acte);
    void deleteActe(Long id);
}