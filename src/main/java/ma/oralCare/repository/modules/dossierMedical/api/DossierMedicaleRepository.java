package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.consultation.Certificat;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité DossierMedicale.
 * Gère le CRUD et les recherches spécifiques des dossiers médicaux.
 */
public interface DossierMedicaleRepository extends CrudRepository<DossierMedicale, Long> {

    // --- 1. Méthodes de Recherche Spécifiques (UC: Chercher et consulter un Dossier) ---

    /**
     * Recherche le Dossier Médical associé à un Patient donné.
     * Un Patient est censé avoir un seul Dossier Médical (Relation 1:1 implicite via le Modèle).
     * @param patientId L'ID du Patient.
     * @return Le Dossier Médical, ou Optional.empty() si non trouvé.
     */
    Optional<DossierMedicale> findByPatientId(Long patientId);

    /**
     * Recherche les Dossiers Médicaux par l'ID du Médecin associé.
     * @param medecinId L'ID du Médecin.
     * @return Une liste des Dossiers Médicaux gérés par ce médecin.
     */
    List<DossierMedicale> findByMedecinId(Long medecinId);

    // --- 2. Fonctions d'Association (Gérer les Dossiers Médicaux) ---
    // Les listes (Consultation, Ordonnance, Certificat, RDV) sont gérées principalement par leurs propres Repositories
    // mais des méthodes d'accès directes sont utiles.

    /**
     * Récupère toutes les consultations d'un Dossier Médical (impliqué dans UC: consulter Cs).
     * @param dossierId L'ID du Dossier Médical.
     * @return La liste des consultations.
     */
    List<Consultation> findConsultationsByDossierId(Long dossierId);

    /**
     * Récupère toutes les ordonnances d'un Dossier Médical (impliqué dans UC: Gérer les Ordonnances).
     * @param dossierId L'ID du Dossier Médical.
     * @return La liste des ordonnances.
     */
    List<Ordonnance> findOrdonnancesByDossierId(Long dossierId);

    /**
     * Récupère tous les certificats d'un Dossier Médical (impliqué dans UC: Gérer les Certificats).
     * @param dossierId L'ID du Dossier Médical.
     * @return La liste des certificats.
     */
    List<Certificat> findCertificatsByDossierId(Long dossierId);

}