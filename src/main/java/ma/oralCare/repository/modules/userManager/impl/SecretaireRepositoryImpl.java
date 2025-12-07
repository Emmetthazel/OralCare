package ma.oralCare.repository.modules.userManager.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.entities.staff.Secretaire;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.userManager.api.SecretaireRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    // --- 1. Opérations CRUD de base (Héritage Utilisateur -> Staff -> Secretaire) ---

    @Override
    public List<Secretaire> findAll() {
        String sql = "SELECT U.*, S.*, Sec.* FROM Utilisateur U " +
                "JOIN Staff S ON U.id = S.id " +
                "JOIN Secretaire Sec ON S.id = Sec.id";
        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les secrétaires", e);
        }
        return out;
    }

    @Override
    public Secretaire findById(Long id) {
        String sql = "SELECT U.*, S.*, Sec.* FROM Utilisateur U " +
                "JOIN Staff S ON U.id = S.id " +
                "JOIN Secretaire Sec ON S.id = Sec.id " +
                "WHERE U.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapSecretaire(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la secrétaire par ID", e);
        }
    }

    @Override
    public void create(Secretaire newElement) {
        if (newElement == null) return;
        Connection c = null;
        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Insertion dans Utilisateur (Utilisateur.create n'est pas utilisé directement ici, nous faisons la cascade manuellement)
            // L'implémentation complète nécessiterait les SQL INSERTs pour Utilisateur et Staff

            // 2. Insertion dans Secretaire (avec le même ID généré/récupéré pour Utilisateur/Staff)
            PreparedStatement psS = c.prepareStatement(
                    "INSERT INTO Secretaire (id, numCNSS, commission) VALUES (?, ?, ?)");
            psS.setLong(1, newElement.getId()); // Supposons que l'ID est déjà généré ou récupéré
            psS.setString(2, newElement.getNumCNSS());
            psS.setDouble(3, newElement.getCommission());
            psS.executeUpdate();

            c.commit();
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { /* Log */ }
            throw new RuntimeException("Erreur lors de la création de la Secrétaire (transaction failed)", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ex) { /* Log */ }
        }
    }

    @Override
    public void update(Secretaire newValuesElement) {
        // Implémentation des UPDATEs en cascade sur Utilisateur, Staff et Secretaire
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        // UPDATE sur Secretaire
        String sql = "UPDATE Secretaire SET numCNSS = ?, commission = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newValuesElement.getNumCNSS());
            ps.setDouble(2, newValuesElement.getCommission());
            ps.setLong(3, newValuesElement.getId());
            ps.executeUpdate();
            // Des UPDATEs pour Staff et Utilisateur devraient suivre ici...
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la Secrétaire", e);
        }
    }

    // Les méthodes delete/deleteById nécessitent aussi la gestion de la cascade et des contraintes d'intégrité (non implémentées ici pour des raisons de concision).
    @Override
    public void delete(Secretaire element) {
        if (element != null && element.getId() != null) deleteById(element.getId());
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Suppression complète non implémentée, nécessite gestion transactionnelle.");
    }

    // --- 2. Méthodes Spécifiques ---

    @Override
    public Optional<Secretaire> findByLogin(String login) {
        String sql = "SELECT U.*, S.*, Sec.* FROM Utilisateur U " +
                "JOIN Staff S ON U.id = S.id " +
                "JOIN Secretaire Sec ON S.id = Sec.id " +
                "WHERE U.login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapSecretaire(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par login", e);
        }
    }

    // --- Gestion des RDV ---

    @Override
    public List<RDV> findRDVByDate(LocalDate date) {
        // La secrétaire consulte l'agenda/RDV
        String sql = "SELECT * FROM RDV WHERE date = ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de RDV par date", e);
        }
        return out;
    }

    @Override
    public List<RDV> findRDVByPatientId(Long patientId) {
        // La secrétaire consulte l'historique RDV du patient
        String sql = "SELECT R.* FROM RDV R JOIN DossierMedicale DM ON R.dossierMedicaleId = DM.id WHERE DM.patientId = ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de RDV par patient", e);
        }
        return out;
    }

    @Override
    public void updateRDVStatus(Long rdvId, String newStatut) {
        // Confirmer/Annuler RDV
        String sql = "UPDATE RDV SET statut = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatut); // Ex: StatutRDV.CONFIRME.name()
            ps.setLong(2, rdvId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut du RDV", e);
        }
    }

    // --- Gestion de la Caisse/Factures ---

    @Override
    public List<Facture> findAllFactures() {
        String sql = "SELECT * FROM Facture";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapFacture(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les factures", e);
        }
        return out;
    }

    @Override
    public List<Facture> findFacturesByPatientId(Long patientId) {
        // Jointure Facture -> SituationFinanciere -> DossierMedicale -> Patient
        String sql = "SELECT F.* FROM Facture F " +
                "JOIN SituationFinanciere SF ON F.situationFinanciereId = SF.id " +
                "JOIN DossierMedicale DM ON SF.dossierMedicaleId = DM.id " +
                "WHERE DM.patientId = ?";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapFacture(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des factures par patient", e);
        }
        return out;
    }

    @Override
    public void enregistrerPaiementFacture(Long factureId, double montantPaye) {
        // Cette opération est complexe et devrait mettre à jour la facture, puis la SituationFinanciere.
        // Ici, nous ne mettons à jour que le montant payé et le reste de la facture.
        String sql = "UPDATE Facture SET totalePaye = totalePaye + ?, reste = totaleFacture - (totalePaye + ?) WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, montantPaye);
            ps.setDouble(2, montantPaye);
            ps.setLong(3, factureId);
            ps.executeUpdate();

            // NOTE: Une logique de mise à jour de StatutFacture et SituationFinanciere devrait être ajoutée ici.
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du paiement de la facture", e);
        }
    }

    @Override
    public Optional<AgendaMensuel> findMedecinAgenda(Long medecinId, String mois) {
        // La secrétaire consulte l'agenda pour planifier un RDV
        String sql = "SELECT * FROM AgendaMensuel WHERE medecinId = ? AND mois = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            ps.setString(2, mois);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapAgendaMensuel(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'agenda mensuel du médecin", e);
        }
    }
}