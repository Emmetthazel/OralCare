package ma.oralCare.repository.modules.cabinet.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.entities.enums.StatistiqueCategorie;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.cabinet.api.StatistiquesRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE : Toutes les méthodes gèrent les SQLException en interne et relancent des RuntimeException.

public class StatistiquesRepositoryImpl implements StatistiquesRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // --- Requêtes BaseEntity (pour gestion des transactions) ---
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // --- Requêtes Statistiques ---
    private static final String SELECT_BASE_FIELDS =
            " s.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Statistiques s JOIN BaseEntity b ON s.id_entite = b.id_entite ";

    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_BASE_FIELDS;
    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE s.id_entite = ?";

    private static final String CREATE_SQL =
            "INSERT INTO Statistiques (id_entite, nom, categorie, chiffre, date_calcul, cabinet_medicale_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Statistiques SET nom = ?, categorie = ?, chiffre = ?, date_calcul = ?, cabinet_medicale_id = ? WHERE id_entite = ?";
    private static final String DELETE_SQL =
            "DELETE FROM Statistiques WHERE id_entite = ?";

    // Requêtes spécifiques
    private static final String FIND_BY_CABINET_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE s.cabinet_medicale_id = ?";

    private static final String FIND_BY_CATEGORIE_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE s.categorie = ?";

    private static final String FIND_BY_DATE_CALCUL_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE s.date_calcul = ?";

    private static final String FIND_BY_DATE_BETWEEN_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE s.date_calcul BETWEEN ? AND ?";

    // Trouver la dernière statistique par catégorie et cabinet
    private static final String FIND_LATEST_BY_CATEGORIE_AND_CABINET_SQL =
            "SELECT " + SELECT_BASE_FIELDS +
                    " WHERE s.categorie = ? AND s.cabinet_medicale_id = ? " +
                    " ORDER BY s.date_calcul DESC, s.id_entite DESC LIMIT 1";


    // --- Logique BaseEntity (gestion des transactions) ---

    private Long createBaseEntity(Connection conn, BaseEntity entity) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(CREATE_BASE_ENTITY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(entity.getDateCreation()));
            stmt.setLong(2, entity.getCreePar());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            throw new SQLException("Échec de la création de BaseEntity, aucun ID généré.");
        }
    }

    private void updateBaseEntity(Connection conn, BaseEntity entity) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_BASE_ENTITY_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(entity.getDateDerniereModification()));
            stmt.setLong(2, entity.getModifiePar());
            stmt.setLong(3, entity.getIdEntite());

            stmt.executeUpdate();
        }
    }

    private void deleteBaseEntity(Connection conn, Long id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_BASE_ENTITY_SQL)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    // --- Méthode privée d'aide pour le Mappage et l'Exécution ---

    private List<Statistiques> executeFindQuery(String sql, Object... params) {
        List<Statistiques> statsList = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                // Gestion spécifique des types LocalDate et Enum
                if (params[i] instanceof LocalDate) {
                    stmt.setDate(i + 1, Date.valueOf((LocalDate) params[i]));
                } else if (params[i] instanceof StatistiqueCategorie) {
                    stmt.setString(i + 1, ((StatistiqueCategorie) params[i]).name());
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    statsList.add(RowMappers.mapStatistiques(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de l'exécution de la requête FIND Statistiques : " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ Statistiques) : " + e.getMessage(), e);
        }
        return statsList;
    }

    // --- Méthodes de l'interface CrudRepository ---

    @Override
    public List<Statistiques> findAll() {
        return executeFindQuery(FIND_ALL_SQL);
    }

    @Override
    public Optional<Statistiques> findById(Long id) {
        List<Statistiques> results = executeFindQuery(FIND_BY_ID_SQL, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void create(Statistiques entity) {
        if (entity.getCreePar() == null) {
            throw new IllegalArgumentException("Le champ creePar doit contenir l'ID de l'utilisateur créateur.");
        }
        if (entity.getCabinetMedicale() == null || entity.getCabinetMedicale().getIdEntite() == null) {
            throw new IllegalArgumentException("Le CabinetMedicale est obligatoire pour une Statistique.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. CREATE BaseEntity
            entity.setDateCreation(LocalDateTime.now());
            Long newId = createBaseEntity(conn, entity);
            entity.setIdEntite(newId);

            // 2. CREATE Statistiques
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setString(2, entity.getNom());
                stmt.setString(3, entity.getCategorie().name()); // Categorie NOT NULL
                stmt.setBigDecimal(4, entity.getChiffre());
                stmt.setDate(5, entity.getDateCalcul() != null ? Date.valueOf(entity.getDateCalcul()) : null); // Date NOT NULL dans la DB
                stmt.setLong(6, entity.getCabinetMedicale().getIdEntite()); // NOT NULL

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La création de la Statistique a échoué, aucune ligne affectée.");
                }
            }
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur JDBC lors de la création de la Statistique: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CREATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Statistiques entity) {
        if (entity.getIdEntite() == null || entity.getModifiePar() == null) {
            throw new IllegalArgumentException("L'ID et le ModifiePar de la Statistique ne peuvent pas être nuls pour la mise à jour.");
        }
        if (entity.getCabinetMedicale() == null || entity.getCabinetMedicale().getIdEntite() == null) {
            throw new IllegalArgumentException("Le CabinetMedicale est obligatoire pour la mise à jour.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE Statistiques
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setString(1, entity.getNom());
                stmt.setString(2, entity.getCategorie().name());
                stmt.setBigDecimal(3, entity.getChiffre());
                stmt.setDate(4, entity.getDateCalcul() != null ? Date.valueOf(entity.getDateCalcul()) : null);
                stmt.setLong(5, entity.getCabinetMedicale().getIdEntite());
                stmt.setLong(6, entity.getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Mise à jour de la Statistique échouée. ID: " + entity.getIdEntite());
                }
            }

            // 2. UPDATE BaseEntity
            entity.setDateDerniereModification(LocalDateTime.now());
            updateBaseEntity(conn, entity);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur JDBC lors de la mise à jour de la Statistique: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Statistiques entity) {
        if (entity.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID de la Statistique ne peut pas être null pour la suppression.");
        }
        deleteById(entity.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID pour la suppression ne peut pas être null.");
        }
        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. DELETE Statistiques (optionnel)
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            // 2. DELETE BaseEntity
            deleteBaseEntity(conn, id);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur lors de la suppression de la Statistique par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (DELETE) : " + e.getMessage(), e);
        }
    }

    // --- Méthodes de l'interface StatistiquesRepository ---

    @Override
    public List<Statistiques> findByCabinetMedicaleId(Long cabinetMedicaleId) {
        if (cabinetMedicaleId == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_CABINET_ID_SQL, cabinetMedicaleId);
    }

    @Override
    public List<Statistiques> findByCategorie(StatistiqueCategorie categorie) {
        if (categorie == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_CATEGORIE_SQL, categorie);
    }

    @Override
    public List<Statistiques> findByDateCalcul(LocalDate dateCalcul) {
        if (dateCalcul == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_DATE_CALCUL_SQL, dateCalcul);
    }

    @Override
    public List<Statistiques> findByDateCalculBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_DATE_BETWEEN_SQL, startDate, endDate);
    }

    @Override
    public Optional<Statistiques> findLatestByCategorieAndCabinet(StatistiqueCategorie categorie, Long cabinetMedicaleId) {
        if (categorie == null || cabinetMedicaleId == null) return Optional.empty();

        List<Statistiques> results = executeFindQuery(FIND_LATEST_BY_CATEGORIE_AND_CABINET_SQL, categorie, cabinetMedicaleId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}