package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory; // ✅ Import indispensable
import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.dossierMedical.api.ActeRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation autonome d'ActeRepository.
 * Elle ne dépend plus d'une connexion injectée mais utilise le SessionFactory.
 */
public class ActeRepositoryImpl implements ActeRepository {

    // ✅ ÉTAPE 1 : On supprime le champ 'private final Connection connection'

    private static final String BASE_SELECT_SQL = """
        SELECT a.id_entite, a.libelle, a.categorie, a.prix_de_base,
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM acte a JOIN BaseEntity b ON a.id_entite = b.id_entite
        """;

    // ✅ ÉTAPE 2 : Constructeur vide pour la MainFrame
    public ActeRepositoryImpl() {
    }

    private List<Acte> executeSelectQuery(String sql, Object... params) {
        List<Acte> out = new ArrayList<>();
        try {
            // ✅ ÉTAPE 3 : Récupération de la connexion active
            Connection conn = SessionFactory.getInstance().getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    if (params[i] instanceof BigDecimal) {
                        ps.setBigDecimal(i + 1, (BigDecimal) params[i]);
                    } else {
                        ps.setObject(i + 1, params[i]);
                    }
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        out.add(RowMappers.mapActe(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB-ERROR] ActeRepository: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la lecture des Actes.", e);
        }
        return out;
    }

    @Override
    public void create(Acte acte) {
        String sqlBase = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
        String sqlActe = "INSERT INTO acte(id_entite, libelle, categorie, prix_de_base) VALUES(?, ?, ?, ?)";

        try {
            Connection conn = SessionFactory.getInstance().getConnection();

            // 1. Insertion dans BaseEntity
            try (PreparedStatement psBase = conn.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                psBase.setObject(2, acte.getCreePar());
                psBase.executeUpdate();

                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) acte.setIdEntite(keys.getLong(1));
                }
            }

            // 2. Insertion dans Acte
            try (PreparedStatement psActe = conn.prepareStatement(sqlActe)) {
                psActe.setLong(1, acte.getIdEntite());
                psActe.setString(2, acte.getLibelle());
                psActe.setString(3, acte.getCategorie());
                psActe.setBigDecimal(4, acte.getPrixDeBase());
                psActe.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'Acte.", e);
        }
    }

    @Override
    public void update(Acte acte) {
        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
        String sqlActe = "UPDATE acte SET libelle = ?, categorie = ?, prix_de_base = ? WHERE id_entite = ?";

        try {
            Connection conn = SessionFactory.getInstance().getConnection();

            // 1. Update Acte
            try (PreparedStatement psActe = conn.prepareStatement(sqlActe)) {
                psActe.setString(1, acte.getLibelle());
                psActe.setString(2, acte.getCategorie());
                psActe.setBigDecimal(3, acte.getPrixDeBase());
                psActe.setLong(4, acte.getIdEntite());
                psActe.executeUpdate();
            }
            // 2. Update BaseEntity
            try (PreparedStatement psBase = conn.prepareStatement(sqlBase)) {
                psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                psBase.setObject(2, acte.getModifiePar());
                psBase.setLong(3, acte.getIdEntite());
                psBase.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'Acte.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression.", e);
        }
    }

    @Override public List<Acte> findAll() { return executeSelectQuery(BASE_SELECT_SQL + " ORDER BY a.libelle"); }
    @Override public Optional<Acte> findById(Long id) { return executeSelectQuery(BASE_SELECT_SQL + " WHERE a.id_entite = ?", id).stream().findFirst(); }
    @Override public void save(Acte acte) { if (acte.getIdEntite() == null) create(acte); else update(acte); }
    @Override public void delete(Acte acte) { if (acte != null) deleteById(acte.getIdEntite()); }
    @Override public Optional<Acte> findByLibelle(String libelle) { return executeSelectQuery(BASE_SELECT_SQL + " WHERE a.libelle = ?", libelle).stream().findFirst(); }
    @Override public List<Acte> findByCategorie(String cat) { return executeSelectQuery(BASE_SELECT_SQL + " WHERE a.categorie = ?", cat); }

    @Override
    public long count() {
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM acte")) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public boolean existsById(Long id) { return findById(id).isPresent(); }
    @Override public List<InterventionMedecin> findInterventionsByActeId(Long id) { return new ArrayList<>(); }
    @Override public Optional<Acte> findByInterventionMedecinId(Long id) { return Optional.empty(); }
    @Override public List<Acte> findPage(int lim, int off) { return new ArrayList<>(); }
}