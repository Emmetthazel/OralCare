package ma.oralCare.repository.modules.cabinet.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.cabinet.api.CabinetMedicaleRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CabinetMedicaleRepositoryImpl implements CabinetMedicaleRepository {

    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_INSERT_CABINET = "INSERT INTO cabinet_medicale (id_entite, nom, email, logo, numero, rue, code_postal, ville, pays, complement, cin, tel1, tel2, siteWeb, instagram, facebook, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_CABINET = "UPDATE cabinet_medicale SET nom=?, email=?, logo=?, numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=?, cin=?, tel1=?, tel2=?, siteWeb=?, instagram=?, facebook=?, description=? WHERE id_entite=?";

    private static final String SQL_SELECT_ALL = "SELECT c.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par FROM cabinet_medicale c JOIN BaseEntity b ON c.id_entite = b.id_entite";
    private static final String SQL_DELETE_BASE = "DELETE FROM BaseEntity WHERE id_entite = ?";

    public CabinetMedicaleRepositoryImpl() {}

    @Override
    public void create(CabinetMedicale cabinet) {
        LocalDateTime now = LocalDateTime.now();
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Insertion dans BaseEntity
                Long baseId = null;
                try (PreparedStatement psBase = conn.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(now));
                    psBase.setObject(2, cabinet.getCreePar());
                    psBase.executeUpdate();
                    try (ResultSet keys = psBase.getGeneratedKeys()) {
                        if (keys.next()) baseId = keys.getLong(1);
                    }
                }

                if (baseId == null) throw new SQLException("ID BaseEntity non généré.");
                cabinet.setIdEntite(baseId);

                // 2. Insertion dans cabinet_medicale
                try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_CABINET)) {
                    mapParams(ps, cabinet, true);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du cabinet", e);
        }
    }

    @Override
    public List<CabinetMedicale> findAll() {
        List<CabinetMedicale> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SQL_SELECT_ALL)) {
            while (rs.next()) {
                list.add(RowMappers.mapCabinetMedicale(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des cabinets", e);
        }
        return list;
    }

    @Override
    public Optional<CabinetMedicale> findById(Long id) {
        String sql = SQL_SELECT_ALL + " WHERE c.id_entite = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedicale(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void update(CabinetMedicale cabinet) {
        LocalDateTime now = LocalDateTime.now();
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update Base
                try (PreparedStatement psBase = conn.prepareStatement(SQL_UPDATE_BASE)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(now));
                    psBase.setObject(2, cabinet.getModifiePar());
                    psBase.setLong(3, cabinet.getIdEntite());
                    psBase.executeUpdate();
                }
                // Update Cabinet
                try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_CABINET)) {
                    mapParams(ps, cabinet, false);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update cabinet", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        // Grâce au ON DELETE CASCADE dans le SQL, supprimer la base suffit
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BASE)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression cabinet", e);
        }
    }

    /**
     * Mappe les paramètres de l'objet CabinetMedicale vers le PreparedStatement.
     * @param isInsert Définit si l'ID doit être placé au début (INSERT) ou à la fin (UPDATE ... WHERE ID=?)
     */
    private void mapParams(PreparedStatement ps, CabinetMedicale c, boolean isInsert) throws SQLException {
        int idx = 1;
        if (isInsert) ps.setLong(idx++, c.getIdEntite());

        ps.setString(idx++, c.getNom());
        ps.setString(idx++, c.getEmail());
        ps.setString(idx++, c.getLogo());

        // Adresse (Embedded)
        ps.setString(idx++, c.getAdresse() != null ? c.getAdresse().getNumero() : null);
        ps.setString(idx++, c.getAdresse() != null ? c.getAdresse().getRue() : null);
        ps.setString(idx++, c.getAdresse() != null ? c.getAdresse().getCodePostal() : null);
        ps.setString(idx++, c.getAdresse() != null ? c.getAdresse().getVille() : null);
        ps.setString(idx++, c.getAdresse() != null ? c.getAdresse().getPays() : null);
        ps.setString(idx++, c.getAdresse() != null ? c.getAdresse().getComplement() : null);

        ps.setString(idx++, c.getCin());
        ps.setString(idx++, c.getTel1());
        ps.setString(idx++, c.getTel2());
        ps.setString(idx++, c.getSiteWeb());
        ps.setString(idx++, c.getInstagram());
        ps.setString(idx++, c.getFacebook());
        ps.setString(idx++, c.getDescription());

        if (!isInsert) ps.setLong(idx, c.getIdEntite());
    }

    // --- Méthodes utilitaires ---
    @Override public long countAll() {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM cabinet_medicale")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) { return 0; }
    }

    @Override public void delete(CabinetMedicale c) { if (c != null) deleteById(c.getIdEntite()); }
    @Override public Optional<CabinetMedicale> findByNom(String nom) { return Optional.empty(); }
    @Override public List<CabinetMedicale> findAllByNomContaining(String nom) { return new ArrayList<>(); }
    @Override public List<CabinetMedicale> findAllByVille(String ville) { return new ArrayList<>(); }
    @Override public long countActiveRecently() { return 0; }
}