package ma.oralCare.mvc.controllers.admin.impl;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.mvc.controllers.admin.api.SystemReferentielController;
import ma.oralCare.service.modules.admin.api.SystemReferentielService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrôleur gérant le référentiel système (Médicaments, Actes, Antécédents).
 * Assure la transition entre les données textuelles de l'IHM et les objets métiers.
 */
public class SystemReferentielControllerImpl implements SystemReferentielController {

    private final SystemReferentielService service;

    public SystemReferentielControllerImpl(SystemReferentielService service) {
        this.service = service;
    }

    // =========================================================================
    // ✅ GESTION DES MÉDICAMENTS
    // =========================================================================

    @Override
    public void addMedicament(String nom, String labo, String type, String forme, boolean remboursable, String prix) {
        Medicament m = new Medicament();
        m.setNom(nom);
        m.setLaboratoire(labo);
        m.setType(type);
        m.setRemboursable(remboursable);

        // Conversion sécurisée de l'Enum
        try {
            m.setForme(FormeMedicament.valueOf(forme));
        } catch (Exception e) {
            m.setForme(FormeMedicament.TABLET); // Fallback
        }

        // Conversion sécurisée du prix (BigDecimal)
        m.setPrixUnitaire(safeParseBigDecimal(prix));

        service.saveMedicament(m);
    }

    @Override
    public List<Medicament> loadMedicaments() {
        return service.getAllMedicaments();
    }

    @Override
    public void updateMedicament(Medicament m) {
        if (m != null) service.saveMedicament(m);
    }

    // =========================================================================
    // ✅ GESTION DES ANTÉCÉDENTS
    // =========================================================================

    @Override
    public void addAntecedent(String nom, String categorie, String risque) {
        Antecedent a = new Antecedent();
        a.setNom(nom);

        try {
            a.setCategorie(CategorieAntecedent.valueOf(categorie));
            a.setNiveauDeRisque(NiveauDeRisque.valueOf(risque));
        } catch (Exception e) {
            // En cas d'erreur de format, on peut logguer ou mettre des valeurs par défaut
        }

        service.saveAntecedent(a);
    }

    @Override
    public List<Antecedent> loadAntecedents() {
        return service.getAllAntecedents();
    }

    @Override
    public void updateAntecedent(Antecedent ant) {
        if (ant != null) service.saveAntecedent(ant);
    }

    // =========================================================================
    // ✅ GESTION DES ACTES
    // =========================================================================

    @Override
    public void addActe(String libelle, String categorie, String prix) {
        Acte acte = new Acte();
        acte.setLibelle(libelle);
        acte.setCategorie(categorie);
        acte.setPrixDeBase(safeParseBigDecimal(prix));

        service.saveActe(acte);
    }

    @Override
    public List<Acte> loadActes() {
        return service.getAllActes();
    }

    @Override
    public void updateActe(Acte a) {
        if (a != null) service.saveActe(a);
    }

    // =========================================================================
    // ✅ OPÉRATIONS COMMUNES
    // =========================================================================

    @Override
    public void deleteEntity(String type, Long id) {
        if (id == null) return;

        switch (type) {
            case "MEDIC":
                service.deleteMedicament(id);
                break;
            case "ANTECEDENT":
                service.deleteAntecedent(id);
                break;
            case "ACTE":
                service.deleteActe(id);
                break;
        }
    }

    // =========================================================================
    // 🛠 HELPERS PRIVÉS
    // =========================================================================

    /**
     * Convertit une chaîne de caractères en BigDecimal de manière sécurisée.
     * Gère les champs vides, les espaces et les virgules.
     */
    private BigDecimal safeParseBigDecimal(String input) {
        if (input == null || input.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // Remplace la virgule par un point pour le format standard BigDecimal
            String cleanInput = input.trim().replace(",", ".");
            return new BigDecimal(cleanInput);
        } catch (NumberFormatException e) {
            System.err.println("❌ Erreur de format BigDecimal pour : " + input);
            return BigDecimal.ZERO;
        }
    }
}