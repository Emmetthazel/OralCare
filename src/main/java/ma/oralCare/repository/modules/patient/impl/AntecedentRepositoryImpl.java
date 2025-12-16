package ma.oralCare.repository.modules.patient.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.repository.modules.patient.api.AntecedentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    private static final Logger LOGGER = Logger.getLogger(AntecedentRepositoryImpl.class.getName());
    private static final String TABLE_NAME = "Antecedent";

    // --- Requêtes SQL BaseEntity ---
    private static final String INSERT_BASE_ENTITY_SQL =
            "INSERT INTO BaseEntity (date_creation, date_derniere_modification, cree_par, modifie_par) " +
                    "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL =
            "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String SELECT_BASE_ENTITY_ID = "SELECT id_entite FROM BaseEntity WHERE date_creation = ? AND cree_par = ? ORDER BY id_entite DESC LIMIT 1";

    // --- Requêtes SQL Antecedent ---
    private static final String INSERT_ANTECEDENT_SQL =
            "INSERT INTO Antecedent (id_entite, nom, categorie, niveau_de_risque) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_ANTECEDENT_SQL =
            "UPDATE Antecedent SET nom = ?, categorie = ?, niveau_de_risque = ? WHERE id_entite = ?";
    private static final String SELECT_BY_ID_SQL =
            "SELECT a.id_entite, a.nom, a.categorie, a.niveau_de_risque, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    "FROM Antecedent a JOIN BaseEntity b ON a.id_entite = b.id_entite WHERE a.id_entite = ?";
    private static final String DELETE_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // Requêtes de recherche spécifiques
    private static final String SELECT_BY_CATEGORIE_SQL = SELECT_BY_ID_SQL.replace(" WHERE a.id_entite = ?", " WHERE a.categorie = ?");
    private static final String SELECT_BY_RISQUE_SQL = SELECT_BY_ID_SQL.replace(" WHERE a.id_entite = ?", " WHERE a.niveau_de_risque = ?");
    private static final String SELECT_BY_NOM_CONTAINING_SQL = SELECT_BY_ID_SQL.replace(" WHERE a.id_entite = ?", " WHERE a.nom LIKE ?");

    // Requêtes Many-to-Many
    private static final String SELECT_BY_PATIENT_ID_SQL =
            "SELECT a.id_entite, a.nom, a.categorie, a.niveau_de_risque, " +
                    "b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    "FROM Antecedent a " +
                    "JOIN Patient_Antecedent pa ON a.id_entite = pa.antecedent_id " +
                    "JOIN BaseEntity b ON a.id_entite = b.id_entite " +
                    "WHERE pa.patient_id = ?";
    private static final String LINK_ANTECEDENT_PATIENT_SQL =
            "INSERT IGNORE INTO Patient_Antecedent (patient_id, antecedent_id) VALUES (?, ?)";
    private static final String UNLINK_ANTECEDENT_PATIENT_SQL =
            "DELETE FROM Patient_Antecedent WHERE patient_id = ? AND antecedent_id = ?";

    // Méthode utilitaire locale pour le rollback
    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Erreur lors du rollback.", ex);
            }
        }
    }

    // --- Implémentations des méthodes de persistance BaseEntity ---
    private void createBaseEntity(Connection conn, BaseEntity entity) throws SQLException {
        entity.onCreate();

        try (PreparedStatement stmt = conn.prepareStatement(INSERT_BASE_ENTITY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(entity.getDateCreation()));
            stmt.setTimestamp(2, (entity.getDateDerniereModification() != null) ? Timestamp.valueOf(entity.getDateDerniereModification()) : null);
            stmt.setLong(3, entity.getCreePar());

            if (entity.getModifiePar() != null) {
                stmt.setLong(4, entity.getModifiePar());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }

            stmt.executeUpdate();

            // Récupérer l'ID généré si l'AUTO_INCREMENT est utilisé par la DB
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setIdEntite(rs.getLong(1));
                } else {
                    // Si la DB ne supporte pas getGeneratedKeys, essayer une requête SELECT alternative (dépend de la DB)
                    try (PreparedStatement selectIdStmt = conn.prepareStatement(SELECT_BASE_ENTITY_ID)) {
                        selectIdStmt.setTimestamp(1, Timestamp.valueOf(entity.getDateCreation()));
                        selectIdStmt.setLong(2, entity.getCreePar());
                        try (ResultSet rsSelect = selectIdStmt.executeQuery()) {
                            if (rsSelect.next()) {
                                entity.setIdEntite(rsSelect.getLong(1));
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateBaseEntity(Connection conn, BaseEntity entity) throws SQLException {
        entity.onUpdate();

        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_BASE_ENTITY_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(entity.getDateDerniereModification()));

            if (entity.getModifiePar() != null) {
                stmt.setLong(2, entity.getModifiePar());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }

            stmt.setLong(3, entity.getIdEntite());
            stmt.executeUpdate();
        }
    }
    // --- Fin des méthodes BaseEntity ---


    // --- Mapper de résultat ---
    private Antecedent mapRow(ResultSet rs) throws SQLException {
        Antecedent antecedent = Antecedent.builder()
                .idEntite(rs.getLong("id_entite"))
                .dateCreation(rs.getTimestamp("date_creation").toLocalDateTime())
                .creePar(rs.getLong("cree_par"))
                // Mappage des champs d'Antecedent
                .nom(rs.getString("nom"))
                .categorie(CategorieAntecedent.valueOf(rs.getString("categorie")))
                .niveauDeRisque(NiveauDeRisque.valueOf(rs.getString("niveau_de_risque")))
                .build();

        // Gestion des champs potentiellement NULL
        Timestamp dateModif = rs.getTimestamp("date_derniere_modification");
        if (dateModif != null) {
            antecedent.setDateDerniereModification(dateModif.toLocalDateTime());
        } else {
            antecedent.setDateDerniereModification(null);
        }

        long modifPar = rs.getLong("modifie_par");
        if (rs.wasNull()) {
            antecedent.setModifiePar(null);
        } else {
            antecedent.setModifiePar(modifPar);
        }

        return antecedent;
    }

    // =========================================================================
    //                            Méthodes CRUD de CrudRepository
    // =========================================================================

    @Override
    public void create(Antecedent antecedent) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Créer BaseEntity (générer id_entite)
            createBaseEntity(conn, antecedent);

            // 2. Créer Antecedent
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_ANTECEDENT_SQL)) {
                stmt.setLong(1, antecedent.getIdEntite());
                stmt.setString(2, antecedent.getNom());
                stmt.setString(3, antecedent.getCategorie().name());
                stmt.setString(4, antecedent.getNiveauDeRisque().name());

                stmt.executeUpdate();
            }
            conn.commit();

        } catch (SQLException e) {
            rollback(conn);
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de l'antécédent: " + antecedent.getNom(), e);
            throw new RuntimeException("Erreur de base de données lors de la création de l'antécédent.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion.", e);
                }
            }
        }
    }

    @Override
    public Optional<Antecedent> findById(Long id) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de l'antécédent par ID: " + id, e);
            throw new RuntimeException("Erreur de base de données.", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Antecedent> findAll() {
        // Logique findAll non implémentée
        return Collections.emptyList();
    }

    @Override
    public void update(Antecedent antecedent) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Mettre à jour BaseEntity
            updateBaseEntity(conn, antecedent);

            // 2. Mettre à jour Antecedent
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_ANTECEDENT_SQL)) {
                stmt.setString(1, antecedent.getNom());
                stmt.setString(2, antecedent.getCategorie().name());
                stmt.setString(3, antecedent.getNiveauDeRisque().name());
                stmt.setLong(4, antecedent.getIdEntite());

                stmt.executeUpdate();
            }
            conn.commit();

        } catch (SQLException e) {
            rollback(conn);
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de l'antécédent: " + antecedent.getIdEntite(), e);
            throw new RuntimeException("Erreur de base de données lors de la mise à jour.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion.", e);
                }
            }
        }
    }

    @Override
    public void delete(Antecedent entity) {
        if (entity != null && entity.getIdEntite() != null) {
            deleteById(entity.getIdEntite());
        } else {
            LOGGER.log(Level.WARNING, "Tentative de suppression d'une entité Antécédent nulle ou sans ID.");
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                LOGGER.log(Level.WARNING, "Aucune BaseEntity trouvée avec l'ID: " + id + " à supprimer.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de l'antécédent: " + id, e);
            throw new RuntimeException("Erreur de base de données lors de la suppression.", e);
        }
    }

    // =========================================================================
    //                            Méthodes de recherche spécifiques
    // =========================================================================

    private List<Antecedent> findByField(String sql, String parameter) {
        List<Antecedent> results = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, parameter);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur de recherche Antecedent.", e);
            throw new RuntimeException("Erreur de base de données.", e);
        }
        return results;
    }

    @Override
    public List<Antecedent> findByCategorie(CategorieAntecedent categorie) {
        return findByField(SELECT_BY_CATEGORIE_SQL, categorie.name());
    }

    @Override
    public List<Antecedent> findByNiveauRisque(NiveauDeRisque niveauRisque) {
        return findByField(SELECT_BY_RISQUE_SQL, niveauRisque.name());
    }

    @Override
    public List<Antecedent> findByNomContaining(String nom) {
        return findByField(SELECT_BY_NOM_CONTAINING_SQL, "%" + nom + "%");
    }

    // La logique de findByPatientId est correcte
    @Override
    public List<Antecedent> findByPatientId(Long patientId) {
        List<Antecedent> results = new ArrayList<>();

        // Utilisation de la requête avec JOIN pour une meilleure performance
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_PATIENT_ID_SQL)) { // Utilisation du SELECT_BY_PATIENT_ID_SQL avec JOIN

            stmt.setLong(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des antécédents pour le patient: " + patientId, e);
            throw new RuntimeException("Erreur de base de données lors de la recherche par Patient ID.", e);
        }

        return results;
    }

    // =========================================================================
    //                            Méthodes Many-to-Many CORRIGÉES
    // =========================================================================

    @Override
    public void linkAntecedentToPatient(Long antecedentId, Long patientId) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false); // Démarrage de la transaction

            try (PreparedStatement pstmt = conn.prepareStatement(LINK_ANTECEDENT_PATIENT_SQL)) {
                pstmt.setLong(1, patientId);
                pstmt.setLong(2, antecedentId);

                int rowsAffected = pstmt.executeUpdate();

                // CORRECTION CRUCIALE : Exécuter le COMMIT pour rendre la ligne immédiatement visible
                conn.commit();

                if (rowsAffected == 0) {
                    System.out.println("INFO: Le lien Patient-Antécédent (" + patientId + ", " + antecedentId + ") existait déjà ou a été ignoré.");
                } else {
                    System.out.println("INFO: Lien Patient-Antécédent créé avec succès. (Commis)");
                }
            }

        } catch (SQLException e) {
            rollback(conn);
            System.err.println("SEVERE: Erreur lors de la liaison de l'antécédent " + antecedentId + " au patient " + patientId);
            LOGGER.log(Level.SEVERE, "Erreur lors de la liaison Many-to-Many", e);
            throw new RuntimeException("Erreur lors de la liaison Many-to-Many", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion.", e);
                }
            }
        }
    }

    @Override
    public void unlinkAntecedentFromPatient(Long antecedentId, Long patientId) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false); // Démarrage de la transaction

            try (PreparedStatement stmt = conn.prepareStatement(UNLINK_ANTECEDENT_PATIENT_SQL)) {

                stmt.setLong(1, patientId);
                stmt.setLong(2, antecedentId);

                int rowsAffected = stmt.executeUpdate();

                // COMMIT
                conn.commit();

                if (rowsAffected == 0) {
                    LOGGER.log(Level.WARNING, String.format("Liaison non trouvée entre l'antécédent %d et le patient %d à supprimer.", antecedentId, patientId));
                } else {
                    System.out.println("INFO: Lien Patient-Antécédent supprimé avec succès. (Commis)");
                }

            }

        } catch (SQLException e) {
            rollback(conn);
            LOGGER.log(Level.SEVERE,
                    String.format("Erreur lors de la suppression de la liaison entre l'antécédent %d et le patient %d", antecedentId, patientId), e);
            throw new RuntimeException("Erreur de base de données lors de la suppression de la liaison.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion.", e);
                }
            }
        }
    }
}