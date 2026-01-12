package ma.oralCare.repository.modules.cabinet.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.cabinet.api.RevenuesRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RevenuesRepositoryImpl implements RevenuesRepository {

    // ✅ ÉTAPE 1 : Constructeur vide pour l'autonomie du Repository
    public RevenuesRepositoryImpl() {}

    // --- SQL Queries ---
    private static final String CREATE_BASE_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";
    private static final String SELECT_JOIN_BASE =
            "SELECT r.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    "FROM Revenues r JOIN BaseEntity b ON r.id_entite = b.id_entite ";
    private static final String CREATE_REV_SQL = "INSERT INTO Revenues (id_entite, titre, description, montant, date, cabinet_medicale_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_REV_SQL = "UPDATE Revenues SET titre=?, description=?, montant=?, date=?, cabinet_medicale_id=? WHERE id_entite=?";

    // =========================================================================
    // ✅ MÉTHODES SPÉCIFIQUES DASHBOARD
    // =========================================================================

    @Override
    public Double calculateDailyRevenues(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return calculateTotalRevenuesBetween(start, end);
    }

    @Override
    public Double calculateTotalRevenuesBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT SUM(montant) FROM Revenues WHERE date BETWEEN ? AND ?";
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double total = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : total;
                }
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur calcul revenus période", e);
        }
    }

    // =========================================================================
    // ✅ PERSISTENCE (CREATE / UPDATE / DELETE)
    // =========================================================================

    @Override
    public void create(Revenues entity) {
        if (entity.getCabinetMedicale() == null) throw new IllegalArgumentException("Cabinet requis");

        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                // 1. Insertion BaseEntity
                try (PreparedStatement stmt = connection.prepareStatement(CREATE_BASE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setObject(2, entity.getCreePar() != null ? entity.getCreePar() : 1L, Types.BIGINT);
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) entity.setIdEntite(rs.getLong(1));
                    }
                }

                // 2. Insertion Revenues
                try (PreparedStatement stmt = connection.prepareStatement(CREATE_REV_SQL)) {
                    stmt.setLong(1, entity.getIdEntite());
                    stmt.setString(2, entity.getTitre());
                    stmt.setString(3, entity.getDescription());
                    stmt.setBigDecimal(4, entity.getMontant());
                    stmt.setTimestamp(5, entity.getDate() != null ? Timestamp.valueOf(entity.getDate()) : Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setLong(6, entity.getCabinetMedicale().getIdEntite());
                    stmt.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création revenu", e);
        }
    }

    @Override
    public void update(Revenues entity) {
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                // 1. Update Revenues
                try (PreparedStatement stmt = connection.prepareStatement(UPDATE_REV_SQL)) {
                    stmt.setString(1, entity.getTitre());
                    stmt.setString(2, entity.getDescription());
                    stmt.setBigDecimal(3, entity.getMontant());
                    stmt.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
                    stmt.setLong(5, entity.getCabinetMedicale().getIdEntite());
                    stmt.setLong(6, entity.getIdEntite());
                    stmt.executeUpdate();
                }

                // 2. Update BaseEntity
                try (PreparedStatement stmt = connection.prepareStatement(UPDATE_BASE_SQL)) {
                    stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setObject(2, entity.getModifiePar() != null ? entity.getModifiePar() : 1L, Types.BIGINT);
                    stmt.setLong(3, entity.getIdEntite());
                    stmt.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour revenu", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_BASE_SQL)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression revenu", e);
        }
    }

    // =========================================================================
    // ✅ RECHERCHES (READ)
    // =========================================================================

    @Override
    public List<Revenues> findAll() {
        return executeFindList(SELECT_JOIN_BASE + " ORDER BY r.date DESC");
    }

    @Override
    public Optional<Revenues> findById(Long id) {
        List<Revenues> list = executeFindList(SELECT_JOIN_BASE + " WHERE r.id_entite = ?", id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Revenues> findByCabinetMedicaleId(Long cabinetId) {
        return executeFindList(SELECT_JOIN_BASE + " WHERE r.cabinet_medicale_id = ?", cabinetId);
    }

    @Override
    public List<Revenues> findByTitreContaining(String t) {
        return executeFindList(SELECT_JOIN_BASE + " WHERE r.titre LIKE ?", "%" + t + "%");
    }

    // =========================================================================
    // ✅ MÉTHODES TECHNIQUES
    // =========================================================================

    private List<Revenues> executeFindList(String sql, Object... params) {
        List<Revenues> list = new ArrayList<>();
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapRevenues(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lecture revenus", e);
        }
        return list;
    }

    @Override public void delete(Revenues entity) { deleteById(entity.getIdEntite()); }
    @Override public List<Revenues> findPage(int limit, int offset) {
        return executeFindList(SELECT_JOIN_BASE + " ORDER BY r.date DESC LIMIT ? OFFSET ?", limit, offset);
    }
}