package ma.oralCare.repository.modules.userManager.api;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.entities.staff.Staff;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité CabinetMedicale.
 * Gère les informations de base de l'entité racine (le cabinet) et fournit des accès rapides aux collections liées.
 */
public interface CabinetMedicaleRepository extends CrudRepository<CabinetMedicale, Long> {

    // --- 1. Méthodes de Recherche Spécifiques ---

    /**
     * Recherche un cabinet par son identifiant unique (CIN).
     */
    Optional<CabinetMedicale> findByCin(String cin);

    /**
     * Recherche un cabinet par son adresse email.
     */
    Optional<CabinetMedicale> findByEmail(String email);

    // --- 2. Méthodes d'Accès aux Relations (Collections - Lazy Loading) ---

    /**
     * Récupère la liste de toutes les charges associées à un cabinet.
     * Nécessaire pour les rapports financiers (CU "Gérer la caisse", "Consulter statistiques de caisse").
     */
    List<Charges> findAllCharges(Long cabinetId);

    /**
     * Récupère la liste de tous les revenus associés à un cabinet.
     * Nécessaire pour les rapports financiers (CU "Gérer la caisse", "Consulter statistiques de caisse").
     */
    List<Revenues> findAllRevenues(Long cabinetId);

    /**
     * Récupère la liste de tous les membres du personnel (Staff) associés à un cabinet.
     */
    List<Staff> findAllStaff(Long cabinetId);
}