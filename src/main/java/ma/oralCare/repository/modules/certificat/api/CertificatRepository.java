package ma.oralCare.repository.modules.certificat.api;

import ma.oralCare.entities.consultation.Certificat;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Certificat.
 * Gère le CRUD et les recherches spécifiques aux documents médicaux.
 */
public interface CertificatRepository extends CrudRepository<Certificat, Long> {

    // --- Méthodes de Recherche Spécifiques ---

    /**
     * Recherche un certificat par son ID (héritée de CrudRepository, mais rappelée pour clarté).
     * @param id L'ID du certificat.
     * @return Le Certificat correspondant, s'il existe.
     */
    Certificat findById(Long id);

    /**
     * Recherche tous les certificats associés à un Dossier Médical spécifique.
     * @param dossierMedicaleId L'ID du Dossier Médical.
     * @return Une liste de certificats pour ce dossier.
     */
    List<Certificat> findByDossierMedicaleId(Long dossierMedicaleId);

    /**
     * Recherche tous les certificats d'un patient par une date d'émission spécifique.
     * @param date La date de début du certificat.
     * @return Une liste des Certificats créés à cette date.
     */
    List<Certificat> findByDateDebut(LocalDate date);

    /**
     * Recherche les certificats qui sont encore valides (dont la date de fin n'est pas passée).
     * @param currentDate La date actuelle de référence.
     * @return La liste des certificats toujours valides.
     */
    List<Certificat> findValidCertificates(LocalDate currentDate);

    /**
     * Recherche les certificats par une partie de la note du médecin.
     * @param noteFragment Fragment de texte à chercher dans la note.
     * @return La liste des certificats contenant ce fragment.
     */
    List<Certificat> findByNoteMedecinContaining(String noteFragment);
}