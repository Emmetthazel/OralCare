package ma.oralCare.service.modules.cabinet.api;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.entities.cabinet.Statistiques;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CabinetMedicalService {

    // -------------------------------------------------------------------------
    // Gestion de l'entité Cabinet (CRUD de base)
    // -------------------------------------------------------------------------

    Optional<CabinetMedicale> getCabinetById(Long id);
    CabinetMedicale saveCabinet(CabinetMedicale cabinet);

    // -------------------------------------------------------------------------
    // Gestion des Charges (Calculs et Requêtes)
    // -------------------------------------------------------------------------

    Charges createCharge(Charges charges);
    List<Charges> getChargesByCabinet(Long cabinetId);
    List<Charges> getChargesBetween(LocalDateTime start, LocalDateTime end);
    Double getTotalChargesBetween(LocalDateTime start, LocalDateTime end);

    // -------------------------------------------------------------------------
    // Accès aux Revenus et Statistiques
    // -------------------------------------------------------------------------

    Revenues createRevenu(Revenues revenu);
    List<Revenues> getAllRevenus();
    List<Statistiques> getAllStatistiques();
}