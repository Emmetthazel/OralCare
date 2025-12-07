package ma.oralCare.repository.modules.consultation.api;

import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.medicament.Prescription;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface de dépôt (Repository) pour l'entité Ordonnance.
 * Gère le CRUD (créer/modifier/supprimer/consulter ordonnance) et les recherches spécifiques.
 */
public interface OrdonnanceRepository extends CrudRepository<Ordonnance, Long> {

    // --- 1. Méthodes de Recherche Spécifiques (UC: consulter ordonnance) ---

    /**
     * Recherche toutes les ordonnances associées à un Dossier Médical.
     * @param dossierId L'ID du Dossier Médical parent.
     * @return Une liste des ordonnances pour ce dossier.
     */
    List<Ordonnance> findByDossierMedicaleId(Long dossierId);

    /**
     * Recherche toutes les ordonnances créées lors d'une Consultation spécifique.
     * @param consultationId L'ID de la Consultation.
     * @return Une liste des ordonnances issues de cette consultation.
     */
    List<Ordonnance> findByConsultationId(Long consultationId);

    /**
     * Recherche toutes les ordonnances émises à une date donnée.
     * @param date La date d'émission.
     * @return Une liste des ordonnances de cette date.
     */
    List<Ordonnance> findByDate(LocalDate date);

    // --- 2. Fonctions d'Association (Gérer les Prescriptions) ---

    /**
     * Récupère la liste des prescriptions incluses dans une ordonnance.
     * Bien que cela puisse être fait par PrescriptionRepository, l'accès direct est utile ici.
     * @param ordonnanceId L'ID de l'Ordonnance.
     * @return La liste des prescriptions associées.
     */
    List<Prescription> findPrescriptionsByOrdonnanceId(Long ordonnanceId);
}