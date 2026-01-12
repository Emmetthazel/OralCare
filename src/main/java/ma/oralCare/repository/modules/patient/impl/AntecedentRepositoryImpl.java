package ma.oralCare.repository.modules.patient.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.repository.modules.patient.api.AntecedentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    // ✅ Suppression de 'private final Connection connection'

    public AntecedentRepositoryImpl() {
        // Constructeur vide pour permettre l'autonomie du repository
    }

    // --- Requêtes SQL ---
    private static final String INSERT_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String INSERT_ANTECEDENT_SQL = "INSERT INTO Antecedent (id_entite, nom, categorie, niveau_de_risque) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_ANTECEDENT_SQL = "UPDATE Antecedent SET nom = ?, categorie = ?, niveau_de_risque = ? WHERE id_entite = ?";
    private static final String DELETE_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    private static final String SELECT_BASE =
            "SELECT a.id_entite, a.nom, a.categorie, a.niveau_de_risque, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    "FROM Antecedent a JOIN BaseEntity b ON a.id_entite = b.id_entite";

    @Override
    public void create(Antecedent antecedent) {
        try {
            // ✅ Récupération de la connexion via SessionFactory
            Connection conn = SessionFactory.getInstance().getConnection();

            // 1. Création BaseEntity
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_BASE_ENTITY_SQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
                stmt.setLong(2, antecedent.getCreePar() != null ? antecedent.getCreePar() : 1L);
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) antecedent.setIdEntite(rs.getLong(1));
                }
            }

            // 2. Création Antecedent
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_ANTECEDENT_SQL)) {
                stmt.setLong(1, antecedent.getIdEntite());
                stmt.setString(2, antecedent.getNom());
                stmt.setString(3, antecedent.getCategorie().name());
                stmt.setString(4, antecedent.getNiveauDeRisque().name());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'antécédent.", e);
        }
    }

    @Override
    public void update(Antecedent antecedent) {
        try {
            Connection conn = SessionFactory.getInstance().getConnection();

            // 1. Update Antecedent
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_ANTECEDENT_SQL)) {
                stmt.setString(1, antecedent.getNom());
                stmt.setString(2, antecedent.getCategorie().name());
                stmt.setString(3, antecedent.getNiveauDeRisque().name());
                stmt.setLong(4, antecedent.getIdEntite());
                stmt.executeUpdate();
            }
            // 2. Update BaseEntity
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_BASE_ENTITY_SQL)) {
                stmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
                stmt.setLong(2, antecedent.getModifiePar() != null ? antecedent.getModifiePar() : 1L);
                stmt.setLong(3, antecedent.getIdEntite());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update antécédent", e);
        }
    }

    @Override
    public List<Antecedent> findAll() {
        List<Antecedent> results = new ArrayList<>();
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(SELECT_BASE)) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll antécédents", e);
        }
        return results;
    }

    @Override
    public Optional<Antecedent> findById(Long id) {
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_BASE + " WHERE a.id_entite = ?")) {
                stmt.setLong(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById antécédent", e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Antecedent mapRow(ResultSet rs) throws SQLException {
        return Antecedent.builder()
                .idEntite(rs.getLong("id_entite"))
                .dateCreation(rs.getTimestamp("date_creation").toLocalDateTime())
                .creePar(rs.getLong("cree_par"))
                .nom(rs.getString("nom"))
                .categorie(CategorieAntecedent.valueOf(rs.getString("categorie")))
                .niveauDeRisque(NiveauDeRisque.valueOf(rs.getString("niveau_de_risque")))
                .build();
    }

    @Override public List<Antecedent> findByCategorie(CategorieAntecedent c) { return findByField("categorie", c.name()); }
    @Override public List<Antecedent> findByNiveauRisque(NiveauDeRisque n) { return findByField("niveau_de_risque", n.name()); }
    @Override public void save(Antecedent a) { if (a.getIdEntite() == null) create(a); else update(a); }
    @Override public void delete(Antecedent a) { if (a != null) deleteById(a.getIdEntite()); }

    private List<Antecedent> findByField(String fieldName, String value) {
        List<Antecedent> results = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE a." + fieldName + " = ?";
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, value);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return results;
    }

    @Override public List<Antecedent> findByNomContaining(String n) {
        // Note: Correction ici pour passer par findByField avec l'opérateur LIKE
        List<Antecedent> results = new ArrayList<>();
        String sql = SELECT_BASE + " WHERE a.nom LIKE ?";
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + n + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return results;
    }

    @Override public List<Antecedent> findByPatientId(Long id) { return new ArrayList<>(); }
    @Override public void linkAntecedentToPatient(Long aId, Long pId) {}
    @Override public void unlinkAntecedentFromPatient(Long aId, Long pId) {}
}