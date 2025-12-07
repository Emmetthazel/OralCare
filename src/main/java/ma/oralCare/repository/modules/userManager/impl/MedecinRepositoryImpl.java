package ma.oralCare.repository.modules.userManager.impl;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.entities.staff.Medecin;
import ma.oralCare.repository.modules.staff.api.MedecinRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedecinRepositoryImpl implements MedecinRepository {

    // --- 1. Opérations CRUD de base (Gestion de l'héritage) ---

    @Override
    public List<Medecin> findAll() {
        // Jointure pour récupérer tous les médecins
        String sql = "SELECT U.*, S.*, M.specialite FROM Utilisateur U " +
                "JOIN Staff S ON U.id = S.id " +
                "JOIN Medecin M ON S.id = M.id";
        List<Medecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapMedecin(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les médecins", e);
        }
        return out;
    }

    @Override
    public Medecin findById(Long id) {
        String sql = "SELECT U.*, S.*, M.specialite FROM Utilisateur U " +
                "JOIN Staff S ON U.id = S.id " +
                "JOIN Medecin M ON S.id = M.id " +
                "WHERE U.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapMedecin(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du médecin par ID", e);
        }
    }

    @Override
    public void create(Medecin newElement) {
        // Opération en cascade : Utilisateur -> Staff -> Medecin
        // Nécessite une gestion transactionnelle pour garantir l'intégrité.
        if (newElement == null) return;
        Connection c = null;
        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Début de transaction

            // 1. Insertion dans Utilisateur
            PreparedStatement psU = c.prepareStatement(
                    "INSERT INTO Utilisateur (nom, email, cin, tel, sexe, login, motDePass, dateNaissance, adresseId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            // ... (Set des paramètres Utilisateur)
            psU.executeUpdate();
            ResultSet rsU = psU.getGeneratedKeys();
            Long userId = rsU.next() ? rsU.getLong(1) : null;
            if (userId == null) throw new SQLException("Failed to get User ID.");
            newElement.setId(userId);

            // 2. Insertion dans Staff
            PreparedStatement psS = c.prepareStatement(
                    "INSERT INTO Staff (id, salaire, prime, dateRecrutement, soldeConge, cabinetMedicaleId) VALUES (?, ?, ?, ?, ?, ?)");
            // ... (Set des paramètres Staff)
            psS.setLong(1, userId);
            psS.executeUpdate();

            // 3. Insertion dans Medecin
            PreparedStatement psM = c.prepareStatement("INSERT INTO Medecin (id, specialite) VALUES (?, ?)");
            psM.setLong(1, userId);
            psM.setString(2, newElement.getSpecialite());
            psM.executeUpdate();

            c.commit(); // Validation de la transaction
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { /* Log */ }
            throw new RuntimeException("Erreur lors de la création du Médecin (transaction failed)", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ex) { /* Log */ }
        }
    }

    @Override
    public void update(Medecin newValuesElement) {
        // Opération en cascade : Utilisateur -> Staff -> Medecin
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        // Implémentation similaire à 'create' mais avec des UPDATEs sur les trois tables
        throw new UnsupportedOperationException("Méthode update complète non implémentée, nécessite gestion transactionnelle.");
    }

    @Override
    public void delete(Medecin element) {
        if (element != null && element.getId() != null) deleteById(element.getId());
    }

    @Override
    public void deleteById(Long id) {
        // Opération en cascade inversée : suppression de Medecin, Staff, puis Utilisateur
        // (ou suppression en cascade configurée dans la DB)
        // Implémentation complète non montrée ici.
        throw new UnsupportedOperationException("Méthode deleteById complète non implémentée, nécessite gestion transactionnelle.");
    }

    // --- 2. Méthodes de Recherche Spécifiques au Médecin ---

    @Override
    public List<Medecin> findBySpecialite(String specialite) {
        String sql = "SELECT U.*, S.*, M.specialite FROM Utilisateur U " +
                "JOIN Staff S ON U.id = S.id " +
                "JOIN Medecin M ON S.id = M.id " +
                "WHERE M.specialite = ?";
        List<Medecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, specialite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedecin(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par spécialité", e);
        }
        return out;
    }

    @Override
    public Optional<Medecin> findByLogin(String login) {
        String sql = "SELECT U.*, S.*, M.specialite FROM Utilisateur U " +
                "JOIN Staff S ON U.id = S.id " +
                "JOIN Medecin M ON S.id = M.id " +
                "WHERE U.login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapMedecin(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par login", e);
        }
    }

    // --- 3. Gestion des Dossiers Médicaux ---

    @Override
    public List<DossierMedicale> findDossiersMedicauxByMedecinId(Long medecinId) {
        // Supposons que DossierMedicale a une colonne medecinId
        String sql = "SELECT * FROM DossierMedicale WHERE medecinId = ? ORDER BY dateDeCreation DESC";
        List<DossierMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDossierMedicale(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des dossiers médicaux du médecin ID: " + medecinId, e);
        }
        return out;
    }

    // --- 4. Gestion de l'Agenda ---

    @Override
    public Optional<AgendaMensuel> findAgendaByMedecinIdAndMois(Long medecinId, Mois mois) {
        String sql = "SELECT * FROM AgendaMensuel WHERE medecinId = ? AND mois = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            ps.setString(2, mois.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapAgendaMensuel(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'agenda mensuel", e);
        }
    }

    @Override
    public void saveAgenda(AgendaMensuel agenda) {
        // Vérifie si l'ID existe pour déterminer si c'est une création ou une mise à jour.
        if (agenda.getId() == null) {
            // INSERT (Création)
            String sql = "INSERT INTO AgendaMensuel (mois, joursNonDisponible, medecinId) VALUES (?, ?, ?)";
            try (Connection c = SessionFactory.getInstance().getConnection();
                 PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, agenda.getMois() != null ? agenda.getMois().name() : null);
                // NOTE: L'implémentation suppose que List<Jour> est sérialisé en String (ex: JSON ou CSV)
                ps.setString(2, agenda.getJoursNonDisponible() != null ? agenda.getJoursNonDisponible().toString() : null);
                ps.setLong(3, agenda.getMedecin().getId());

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            agenda.setId(generatedKeys.getLong(1));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la création de l'agenda mensuel", e);
            }
        } else {
            // UPDATE (Modification)
            String sql = "UPDATE AgendaMensuel SET mois = ?, joursNonDisponible = ?, medecinId = ? WHERE id = ?";
            try (Connection c = SessionFactory.getInstance().getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, agenda.getMois() != null ? agenda.getMois().name() : null);
                ps.setString(2, agenda.getJoursNonDisponible() != null ? agenda.getJoursNonDisponible().toString() : null);
                ps.setLong(3, agenda.getMedecin().getId());
                ps.setLong(4, agenda.getId());

                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la mise à jour de l'agenda mensuel", e);
            }
        }
    }
}