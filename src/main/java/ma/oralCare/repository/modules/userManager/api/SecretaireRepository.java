package ma.oralCare.repository.modules.userManager.api;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.entities.staff.Secretaire;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SecretaireRepository extends CrudRepository<Secretaire, Long> {

    // Méthodes spécifiques à la recherche de la secrétaire
    Optional<Secretaire> findByLogin(String login);

    // --- Gestion des Patients (via CrudRepository) ---
    // Les méthodes CRUD de Patient devraient être dans PatientRepository,
    // mais si le Use Case implique la secrétaire, on peut ajouter ici :
    // findPatientsByCabinetId(Long cabinetId); // si la secrétaire est liée à un cabinet

    // --- Gestion des RDV ---
    List<RDV> findRDVByDate(LocalDate date);
    List<RDV> findRDVByPatientId(Long patientId);
    void updateRDVStatus(Long rdvId, String newStatut); // Confirmer/Annuler

    // --- Gestion de la Caisse/Factures ---
    List<Facture> findAllFactures();
    List<Facture> findFacturesByPatientId(Long patientId);
    void enregistrerPaiementFacture(Long factureId, double montantPaye);

    // --- Autres (Basé sur Use Case : Gérer l'Agenda) ---
    // Note: L'agenda est surtout lié au Médecin, la secrétaire y accède pour la planification.
    // L'opération concrète serait sur AgendaMensuelRepository ou MedecinRepository,
    // mais on peut simuler l'accès pour la planification.
    Optional<AgendaMensuel> findMedecinAgenda(Long medecinId, String mois);
}