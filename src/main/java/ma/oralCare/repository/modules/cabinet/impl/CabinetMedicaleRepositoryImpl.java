package ma.oralCare.repository.modules.cabinet.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.cabinet.*;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.cabinet.api.CabinetMedicaleRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CabinetMedicaleRepositoryImpl implements CabinetMedicaleRepository {

    // --- Requêtes SQL ---
    // Dans CabinetMedicaleRepositoryImpl.java
    private static final String SQL_INSERT_CABINET = "INSERT INTO cabinet_medicale (id_entite, nom, email, logo, numero, rue, code_postal, ville, pays, complement, cin, tel1, tel2, siteWeb, instagram, facebook, description) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_CABINET = "UPDATE cabinet_medicale SET " +
            "nom=?, email=?, logo=?, " +
            "numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=?, " +
            "cin=?, tel1=?, tel2=?, siteWeb=?, instagram=?, facebook=?, description=? " + // Ajout des champs manquants
            "WHERE id_entite=?";
    private static final String BASE_SELECT_CABINET_SQL = """
    SELECT c.nom, c.email, c.logo, c.numero, c.rue, c.code_postal, c.ville, c.pays, c.complement, c.cin, c.tel1, c.tel2, c.siteWeb, c.instagram, c.facebook, c.description,
           b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
    FROM cabinet_medicale c
    JOIN BaseEntity b ON c.id_entite = b.id_entite
    """;

    private static final String SQL_DELETE_CABINET = "DELETE FROM BaseEntity WHERE id_entite = ?";


    // =========================================================================
    //                            CRUD (Création, Mise à jour)
    // =========================================================================

    @Override
    public void create(CabinetMedicale cabinet) {
        Long baseId = null;
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, cabinet.getCreePar());
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                cabinet.setIdEntite(baseId);
                cabinet.setDateCreation(now);
            }

            // 2. CabinetMedicale
            try (PreparedStatement psCabinet = c.prepareStatement(SQL_INSERT_CABINET)) {
                int i = 1;
                psCabinet.setLong(i++, cabinet.getIdEntite());
                psCabinet.setString(i++, cabinet.getNom());
                psCabinet.setString(i++, cabinet.getEmail());
                psCabinet.setString(i++, cabinet.getLogo());

                // Champs Adresse (Correction de l'accès)
                psCabinet.setString(i++, cabinet.getAdresse().getNumero());
                psCabinet.setString(i++, cabinet.getAdresse().getRue());
                psCabinet.setString(i++, cabinet.getAdresse().getCodePostal());
                psCabinet.setString(i++, cabinet.getAdresse().getVille());
                psCabinet.setString(i++, cabinet.getAdresse().getPays());
                psCabinet.setString(i++, cabinet.getAdresse().getComplement());

                // Champs restants (Inclus si le SQL_INSERT_CABINET est complet)
                psCabinet.setString(i++, cabinet.getCin());
                psCabinet.setString(i++, cabinet.getTel1());
                psCabinet.setString(i++, cabinet.getTel2());
                psCabinet.setString(i++, cabinet.getSiteWeb());
                psCabinet.setString(i++, cabinet.getInstagram());
                psCabinet.setString(i++, cabinet.getFacebook());
                psCabinet.setString(i++, cabinet.getDescription());

                psCabinet.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la création Cabinet.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la création du Cabinet.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void update(CabinetMedicale cabinet) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity (Mise à jour) : OK, ne nécessite pas de changement
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, cabinet.getModifiePar());
                psBase.setLong(3, cabinet.getIdEntite());
                psBase.executeUpdate();
                cabinet.setDateDerniereModification(now);
            }

            // 2. CabinetMedicale (Mise à jour) : ATTENTION, MISE À JOUR REQUISE ICI
            try (PreparedStatement psCabinet = c.prepareStatement(SQL_UPDATE_CABINET)) {
                int i = 1;
                // 1-3. Champs directs
                psCabinet.setString(i++, cabinet.getNom());
                psCabinet.setString(i++, cabinet.getEmail());
                psCabinet.setString(i++, cabinet.getLogo());

                // 4-9. Champs Adresse
                psCabinet.setString(i++, cabinet.getAdresse().getNumero());
                psCabinet.setString(i++, cabinet.getAdresse().getRue());
                psCabinet.setString(i++, cabinet.getAdresse().getCodePostal());
                psCabinet.setString(i++, cabinet.getAdresse().getVille());
                psCabinet.setString(i++, cabinet.getAdresse().getPays());
                psCabinet.setString(i++, cabinet.getAdresse().getComplement());

                // 10-16. Champs restants
                psCabinet.setString(i++, cabinet.getCin());
                psCabinet.setString(i++, cabinet.getTel1());
                psCabinet.setString(i++, cabinet.getTel2());
                psCabinet.setString(i++, cabinet.getSiteWeb());
                psCabinet.setString(i++, cabinet.getInstagram());
                psCabinet.setString(i++, cabinet.getFacebook());
                psCabinet.setString(i++, cabinet.getDescription());

                // 17. WHERE clause
                psCabinet.setLong(i++, cabinet.getIdEntite());
                psCabinet.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la mise à jour Cabinet.", rollbackEx); } }
            // Capturez l'erreur SQL d'origine pour le débogage.
            System.err.println("SQL ERROR DETAILS: " + e.getMessage() + " (SQLState: " + e.getSQLState() + ")");
            throw new RuntimeException("Erreur lors de la mise à jour du Cabinet.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void delete(CabinetMedicale cabinet) {
        if (cabinet != null) deleteById(cabinet.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        // Supprime l'entrée dans BaseEntity, qui doit cascader la suppression vers CabinetMedicale.
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_DELETE_CABINET)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du Cabinet.", e);
        }
    }


    // =========================================================================
    //                            READ & Méthodes de Recherche
    // =========================================================================

    private Optional<CabinetMedicale> executeFindQuery(String sql, Object... params) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                // RowMappers.mapCabinetMedicale doit exister dans votre package common
                return rs.next() ? Optional.of(RowMappers.mapCabinetMedicale(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'exécution de la requête de recherche.", e);
        }
    }

    private List<CabinetMedicale> executeFindAllQuery(String sql, Object... params) {
        List<CabinetMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapCabinetMedicale(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'exécution de la requête findAll.", e);
        }
        return out;
    }

    @Override
    public Optional<CabinetMedicale> findById(Long id) {
        String sql = BASE_SELECT_CABINET_SQL + " WHERE c.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapCabinetMedicale(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findById CabinetMedicale.", e);
        }
    }

    @Override
    public List<CabinetMedicale> findAll() {
        String sql = BASE_SELECT_CABINET_SQL + " ORDER BY c.nom";
        return executeFindAllQuery(sql);
    }

    @Override
    public Optional<CabinetMedicale> findByNom(String nom) {
        String sql = BASE_SELECT_CABINET_SQL + " WHERE c.nom = ?";
        return executeFindQuery(sql, nom);
    }

    @Override
    public List<CabinetMedicale> findAllByNomContaining(String nom) {
        String sql = BASE_SELECT_CABINET_SQL + " WHERE c.nom LIKE ?";
        return executeFindAllQuery(sql, "%" + nom + "%");
    }

    @Override
    public List<CabinetMedicale> findAllByVille(String ville) {
        String sql = BASE_SELECT_CABINET_SQL + " WHERE c.ville = ?";
        return executeFindAllQuery(sql, ville);
    }
}