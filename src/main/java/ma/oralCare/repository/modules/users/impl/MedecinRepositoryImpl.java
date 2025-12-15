package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.users.Medecin;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.users.api.MedecinRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedecinRepositoryImpl implements MedecinRepository {

    // Requêtes d'insertion (BaseEntity doit accepter un ID fixe pour le test)
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_INSERT_UTILISATEUR = "INSERT INTO utilisateur(id_entite, nom, prenom, email, cin, tel, sexe, login, mot_de_pass, date_naissance, numero, rue, code_postal, ville, pays, complement) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_STAFF = "INSERT INTO Staff(id_entite, salaire, prime, date_recrutement, solde_conge, cabinet_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_MEDECIN = "INSERT INTO Medecin(id_entite, specialite) VALUES (?, ?)";

    // 1. Mise à jour des champs spécifiques à l'utilisateur (nom, tel, email, etc.)
    private static final String SQL_UPDATE_UTILISATEUR = "UPDATE utilisateur SET nom=?, prenom=?, email=?, cin=?, tel=?, sexe=?, login=?, mot_de_pass=?, date_naissance=?, numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=? WHERE id_entite=?";

    // 2. Mise à jour de la BaseEntity (date_derniere_modification, modifie_par)
    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";

    // 3. Mise à jour des champs spécifiques au Staff (salaire, prime, solde_conge)
    private static final String SQL_UPDATE_STAFF = "UPDATE Staff SET salaire=?, prime=?, date_recrutement=?, solde_conge=?, cabinet_id=? WHERE id_entite=?";

    // 4. Mise à jour des champs spécifiques au Medecin
    private static final String SQL_UPDATE_MEDECIN = "UPDATE Medecin SET specialite=? WHERE id_entite=?";
    // Requête de sélection de base (utilisée pour toutes les méthodes READ)
    private static final String BASE_SELECT_MEDECIN_SQL = """
        SELECT m.specialite, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_id,
               u.nom, u.prenom, u.email, u.cin, u.tel, u.sexe, u.login, u.mot_de_pass, u.date_naissance, u.last_login_date, u.numero, u.rue, u.code_postal, u.ville, u.pays, u.complement,
               b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM Medecin m
        JOIN Staff s ON m.id_entite = s.id_entite
        JOIN utilisateur u ON s.id_entite = u.id_entite
        JOIN BaseEntity b ON u.id_entite = b.id_entite
        """;

    // =========================================================================
    //                            1. CREATE (Insertion Atomique)
    // =========================================================================

    @Override
    public void create(Medecin medecin) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();
        Long generatedId = null;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity - Insertion et récupération de l'ID généré
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, medecin.getCreePar() != null ? medecin.getCreePar() : 1L);
                psBase.executeUpdate();

                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) generatedId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity pour Medecin.");
                }
                medecin.setIdEntite(generatedId); // Affecte l'ID généré à l'objet
                medecin.setDateCreation(now);
            }

            // 2. Utilisateur (Hérite de BaseEntity)
            try (PreparedStatement psUser = c.prepareStatement(SQL_INSERT_UTILISATEUR)) {
                int i = 1;
                psUser.setLong(i++, generatedId); // Utilise l'ID généré
                psUser.setString(i++, medecin.getNom());
                psUser.setString(i++, medecin.getPrenom());
                psUser.setString(i++, medecin.getEmail());
                psUser.setString(i++, medecin.getCin());
                psUser.setString(i++, medecin.getTel());
                psUser.setString(i++, medecin.getSexe().name());
                psUser.setString(i++, medecin.getLogin());
                psUser.setString(i++, medecin.getMotDePass());
                psUser.setDate(i++, medecin.getDateNaissance() != null ? Date.valueOf(medecin.getDateNaissance()) : null);

                // Adresse
                Adresse adresse = medecin.getAdresse();
                psUser.setString(i++, adresse != null ? adresse.getNumero() : null);
                psUser.setString(i++, adresse != null ? adresse.getRue() : null);
                psUser.setString(i++, adresse != null ? adresse.getCodePostal() : null);
                psUser.setString(i++, adresse != null ? adresse.getVille() : null);
                psUser.setString(i++, adresse != null ? adresse.getPays() : null);
                psUser.setString(i++, adresse != null ? adresse.getComplement() : null);

                psUser.executeUpdate();
            }

            // 3. Staff (Hérite d'Utilisateur)
            try (PreparedStatement psStaff = c.prepareStatement(SQL_INSERT_STAFF)) {
                int i = 1;
                psStaff.setLong(i++, generatedId); // Utilise l'ID généré
                psStaff.setBigDecimal(i++, medecin.getSalaire());
                psStaff.setBigDecimal(i++, medecin.getPrime());
                psStaff.setDate(i++, Date.valueOf(medecin.getDateRecrutement()));
                psStaff.setInt(i++, medecin.getSoldeConge());

                // Clé étrangère vers CabinetMedicale (doit exister)
                if (medecin.getCabinetMedicale() != null) {
                    psStaff.setLong(i++, medecin.getCabinetMedicale().getIdEntite());
                } else {
                    psStaff.setNull(i++, Types.BIGINT);
                }

                psStaff.executeUpdate();
            }

            // 4. Medecin (Spécifique)
            try (PreparedStatement psMedecin = c.prepareStatement(SQL_INSERT_MEDECIN)) {
                psMedecin.setLong(1, generatedId); // Utilise l'ID généré
                psMedecin.setString(2, medecin.getSpecialite());
                psMedecin.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            // Log détaillé de l'erreur SQL pour le débogage
            System.err.println("SQL ERROR DETAILS lors de l'insertion Medecin: " + e.getMessage() + " | State: " + e.getSQLState());
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback lors de la création Medecin: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Erreur lors de la création du Medecin.", e);
        } finally {
            if (c != null) {
                try { c.setAutoCommit(true); c.close(); }
                catch (SQLException closeEx) {
                    throw new RuntimeException("Erreur de fermeture connexion.", closeEx);
                }
            }
        }
    }

    // =========================================================================
    //                            2. READ (Méthodes CRUD et API)
    // =========================================================================

    @Override
    public Optional<Medecin> findById(Long id) {
        String sql = BASE_SELECT_MEDECIN_SQL + " WHERE m.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapMedecin(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findById Medecin.", e);
        }
    }

    @Override
    public Optional<Medecin> findByLogin(String login) {
        String sql = BASE_SELECT_MEDECIN_SQL + " WHERE u.login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapMedecin(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByLogin Medecin.", e);
        }
    }

    @Override
    public Optional<Medecin> findByCin(String cin) {
        String sql = BASE_SELECT_MEDECIN_SQL + " WHERE u.cin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapMedecin(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByCin Medecin.", e);
        }
    }

    private List<Medecin> executeFindList(String sql) {
        List<Medecin> medecins = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                medecins.add(RowMappers.mapMedecin(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de executeFindList Medecin.", e);
        }
        return medecins;
    }

    @Override
    public List<Medecin> findAllBySpecialite(String specialite) {
        String sql = BASE_SELECT_MEDECIN_SQL + " WHERE m.specialite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, specialite);
            List<Medecin> medecins = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medecins.add(RowMappers.mapMedecin(rs));
                }
                return medecins;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllBySpecialite Medecin.", e);
        }
    }

    @Override
    public List<Medecin> findAllByNomContaining(String nom) {
        String sql = BASE_SELECT_MEDECIN_SQL + " WHERE u.nom LIKE ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            List<Medecin> medecins = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    medecins.add(RowMappers.mapMedecin(rs));
                }
                return medecins;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllByNomContaining Medecin.", e);
        }
    }

    @Override
    public List<Medecin> findAll() {
        return executeFindList(BASE_SELECT_MEDECIN_SQL);
    }

    // =========================================================================
    //                            3. UPDATE
    // =========================================================================

    @Override
    public void update(Medecin medecin) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        if (medecin.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID du Medecin ne peut pas être null pour la mise à jour.");
        }

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Démarrage de la transaction

            // --- A. UPDATE UTILISATEUR ---
            try (PreparedStatement psUser = c.prepareStatement(SQL_UPDATE_UTILISATEUR)) {
                int i = 1;
                psUser.setString(i++, medecin.getNom());
                psUser.setString(i++, medecin.getPrenom());
                psUser.setString(i++, medecin.getEmail());
                psUser.setString(i++, medecin.getCin());
                psUser.setString(i++, medecin.getTel());
                psUser.setString(i++, medecin.getSexe().name());
                psUser.setString(i++, medecin.getLogin());
                psUser.setString(i++, medecin.getMotDePass()); // Bien que rarement mis à jour ici, on le garde.
                psUser.setDate(i++, medecin.getDateNaissance() != null ? Date.valueOf(medecin.getDateNaissance()) : null);

                // Adresse
                Adresse adresse = medecin.getAdresse();
                psUser.setString(i++, adresse != null ? adresse.getNumero() : null);
                psUser.setString(i++, adresse != null ? adresse.getRue() : null);
                psUser.setString(i++, adresse != null ? adresse.getCodePostal() : null);
                psUser.setString(i++, adresse != null ? adresse.getVille() : null);
                psUser.setString(i++, adresse != null ? adresse.getPays() : null);
                psUser.setString(i++, adresse != null ? adresse.getComplement() : null);

                // WHERE clause
                psUser.setLong(i++, medecin.getIdEntite());

                psUser.executeUpdate();
            }

            // --- B. UPDATE STAFF ---
            try (PreparedStatement psStaff = c.prepareStatement(SQL_UPDATE_STAFF)) {
                int i = 1;
                psStaff.setBigDecimal(i++, medecin.getSalaire());
                psStaff.setBigDecimal(i++, medecin.getPrime());
                psStaff.setDate(i++, Date.valueOf(medecin.getDateRecrutement()));
                psStaff.setInt(i++, medecin.getSoldeConge());

                // Clé étrangère vers CabinetMedicale (doit exister)
                if (medecin.getCabinetMedicale() != null) {
                    psStaff.setLong(i++, medecin.getCabinetMedicale().getIdEntite());
                } else {
                    psStaff.setNull(i++, Types.BIGINT);
                }

                // WHERE clause
                psStaff.setLong(i++, medecin.getIdEntite());

                psStaff.executeUpdate();
            }

            // --- C. UPDATE MEDECIN ---
            try (PreparedStatement psMedecin = c.prepareStatement(SQL_UPDATE_MEDECIN)) {
                psMedecin.setString(1, medecin.getSpecialite());
                // WHERE clause
                psMedecin.setLong(2, medecin.getIdEntite());
                psMedecin.executeUpdate();
            }

            // --- D. UPDATE BASEENTITY (Mise à jour de la date de modification) ---
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, medecin.getModifiePar() != null ? medecin.getModifiePar() : 1L);
                // WHERE clause
                psBase.setLong(3, medecin.getIdEntite());
                psBase.executeUpdate();

                // Mettre à jour l'objet pour la vérification du test
                medecin.setDateDerniereModification(now);
            }

            c.commit(); // Validation de la transaction

        } catch (SQLException e) {
            System.err.println("SQL ERROR DETAILS lors de l'UPDATE Medecin: " + e.getMessage() + " | State: " + e.getSQLState());
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback lors de l'UPDATE Medecin: " + rollbackEx.getMessage());
                }
            }
            // Retirer le message d'erreur statique du test et laisser remonter la cause réelle
            throw new RuntimeException("Erreur lors de la mise à jour du Medecin.", e);
        } finally {
            if (c != null) {
                try { c.setAutoCommit(true); c.close(); }
                catch (SQLException closeEx) {
                    throw new RuntimeException("Erreur de fermeture connexion.", closeEx);
                }
            }
        }
    }

    // =========================================================================
    //                            4. DELETE
    // =========================================================================

    @Override
    public void delete(Medecin medecin) {
        deleteById(medecin.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        // La suppression de BaseEntity supprime en cascade toutes les entités liées (Utilisateur, Staff, Medecin)
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du Medecin.", e);
        }
    }
}