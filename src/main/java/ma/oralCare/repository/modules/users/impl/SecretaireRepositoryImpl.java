package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.users.api.SecretaireRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    // --- Requêtes SQL ---
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_INSERT_UTILISATEUR = "INSERT INTO utilisateur(id_entite, nom, prenom, email, cin, tel, sexe, login, mot_de_pass, date_naissance, numero, rue, code_postal, ville, pays, complement) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_STAFF = "INSERT INTO Staff(id_entite, salaire, prime, date_recrutement, solde_conge, cabinet_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_SECRETAIRE = "INSERT INTO Secretaire(id_entite, num_cnss, commission) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_UTILISATEUR = "UPDATE utilisateur SET nom=?, prenom=?, email=?, cin=?, tel=?, sexe=?, login=?, mot_de_pass=?, date_naissance=?, numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=? WHERE id_entite=?";
    private static final String SQL_UPDATE_STAFF = "UPDATE Staff SET salaire=?, prime=?, date_recrutement=?, solde_conge=?, cabinet_id=? WHERE id_entite=?";
    private static final String SQL_UPDATE_SECRETAIRE = "UPDATE Secretaire SET num_cnss=?, commission=? WHERE id_entite=?";

    private static final String BASE_SELECT_SECRETAIRE_SQL = """
        SELECT sec.num_cnss, sec.commission,
               s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_id,
               u.*,
               b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM Secretaire sec
        JOIN Staff s ON sec.id_entite = s.id_entite
        JOIN utilisateur u ON s.id_entite = u.id_entite
        JOIN BaseEntity b ON u.id_entite = b.id_entite
        """;

    // ✅ Constructeur vide pour autonomie
    public SecretaireRepositoryImpl() {
    }

    @Override
    public void create(Secretaire secretaire) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setObject(2, secretaire.getCreePar() != null ? secretaire.getCreePar() : 1L);
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) secretaire.setIdEntite(keys.getLong(1));
                }
                secretaire.setDateCreation(now);
            }

            // 2. Utilisateur
            try (PreparedStatement psUser = c.prepareStatement(SQL_INSERT_UTILISATEUR)) {
                mapUtilisateurParams(psUser, secretaire);
                psUser.executeUpdate();
            }

            // 3. Staff
            try (PreparedStatement psStaff = c.prepareStatement(SQL_INSERT_STAFF)) {
                int i = 1;
                psStaff.setLong(i++, secretaire.getIdEntite());
                psStaff.setBigDecimal(i++, secretaire.getSalaire() != null ? secretaire.getSalaire() : BigDecimal.ZERO);
                psStaff.setBigDecimal(i++, secretaire.getPrime() != null ? secretaire.getPrime() : BigDecimal.ZERO);
                psStaff.setDate(i++, secretaire.getDateRecrutement() != null ? Date.valueOf(secretaire.getDateRecrutement()) : null);
                psStaff.setInt(i++, secretaire.getSoldeConge() != null ? secretaire.getSoldeConge() : 0);
                psStaff.setObject(i++, secretaire.getCabinetMedicale() != null ? secretaire.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
                psStaff.executeUpdate();
            }

            // 4. Secretaire
            try (PreparedStatement psSec = c.prepareStatement(SQL_INSERT_SECRETAIRE)) {
                psSec.setLong(1, secretaire.getIdEntite());
                psSec.setString(2, secretaire.getNumCNSS());
                psSec.setBigDecimal(3, secretaire.getCommission());
                psSec.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Erreur création Secretaire", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @Override
    public void update(Secretaire secretaire) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // BaseEntity
            try (PreparedStatement ps = c.prepareStatement(SQL_UPDATE_BASE)) {
                ps.setTimestamp(1, Timestamp.valueOf(now));
                ps.setObject(2, secretaire.getModifiePar() != null ? secretaire.getModifiePar() : 1L);
                ps.setLong(3, secretaire.getIdEntite());
                ps.executeUpdate();
            }

            // Utilisateur
            try (PreparedStatement psUser = c.prepareStatement(SQL_UPDATE_UTILISATEUR)) {
                int i = 1;
                psUser.setString(i++, secretaire.getNom());
                psUser.setString(i++, secretaire.getPrenom());
                psUser.setString(i++, secretaire.getEmail());
                psUser.setString(i++, secretaire.getCin());
                psUser.setString(i++, secretaire.getTel());
                psUser.setString(i++, secretaire.getSexe() != null ? secretaire.getSexe().name() : null);
                psUser.setString(i++, secretaire.getLogin());
                psUser.setString(i++, secretaire.getMotDePass());
                psUser.setDate(i++, secretaire.getDateNaissance() != null ? Date.valueOf(secretaire.getDateNaissance()) : null);

                Adresse adr = secretaire.getAdresse();
                psUser.setString(i++, adr != null ? adr.getNumero() : null);
                psUser.setString(i++, adr != null ? adr.getRue() : null);
                psUser.setString(i++, adr != null ? adr.getCodePostal() : null);
                psUser.setString(i++, adr != null ? adr.getVille() : null);
                psUser.setString(i++, adr != null ? adr.getPays() : null);
                psUser.setString(i++, adr != null ? adr.getComplement() : null);
                psUser.setLong(i++, secretaire.getIdEntite());
                psUser.executeUpdate();
            }

            // Staff & Secretaire spécifiques
            try (PreparedStatement psStaff = c.prepareStatement(SQL_UPDATE_STAFF)) {
                psStaff.setBigDecimal(1, secretaire.getSalaire());
                psStaff.setBigDecimal(2, secretaire.getPrime());
                psStaff.setDate(3, secretaire.getDateRecrutement() != null ? Date.valueOf(secretaire.getDateRecrutement()) : null);
                psStaff.setInt(4, secretaire.getSoldeConge());
                psStaff.setObject(5, secretaire.getCabinetMedicale() != null ? secretaire.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
                psStaff.setLong(6, secretaire.getIdEntite());
                psStaff.executeUpdate();
            }

            try (PreparedStatement psSec = c.prepareStatement(SQL_UPDATE_SECRETAIRE)) {
                psSec.setString(1, secretaire.getNumCNSS());
                psSec.setBigDecimal(2, secretaire.getCommission());
                psSec.setLong(3, secretaire.getIdEntite());
                psSec.executeUpdate();
            }

            c.commit();
            secretaire.setDateDerniereModification(now);
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Erreur mise à jour Secretaire", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void mapUtilisateurParams(PreparedStatement ps, Secretaire s) throws SQLException {
        int i = 1;
        ps.setLong(i++, s.getIdEntite());
        ps.setString(i++, s.getNom());
        ps.setString(i++, s.getPrenom());
        ps.setString(i++, s.getEmail());
        ps.setString(i++, s.getCin());
        ps.setString(i++, s.getTel());
        ps.setString(i++, s.getSexe() != null ? s.getSexe().name() : null);
        ps.setString(i++, s.getLogin());
        ps.setString(i++, s.getMotDePass());
        ps.setDate(i++, s.getDateNaissance() != null ? Date.valueOf(s.getDateNaissance()) : null);
        Adresse adr = s.getAdresse();
        ps.setString(i++, adr != null ? adr.getNumero() : null);
        ps.setString(i++, adr != null ? adr.getRue() : null);
        ps.setString(i++, adr != null ? adr.getCodePostal() : null);
        ps.setString(i++, adr != null ? adr.getVille() : null);
        ps.setString(i++, adr != null ? adr.getPays() : null);
        ps.setString(i++, adr != null ? adr.getComplement() : null);
    }

    @Override
    public Optional<Secretaire> findById(Long id) {
        return findOneBy("sec.id_entite", id);
    }

    @Override public List<Secretaire> findAll() {
        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(BASE_SELECT_SECRETAIRE_SQL + " ORDER BY u.nom")) {
            while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM BaseEntity WHERE id_entite = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Optional<Secretaire> findOneBy(String field, Object val) {
        String sql = BASE_SELECT_SECRETAIRE_SQL + " WHERE " + field + " = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, val);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapSecretaire(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void delete(Secretaire s) { if(s != null) deleteById(s.getIdEntite()); }
    @Override public Optional<Secretaire> findByLogin(String login) { return findOneBy("u.login", login); }
    @Override public Optional<Secretaire> findByCin(String cin) { return findOneBy("u.cin", cin); }
    @Override public List<Secretaire> findAllByNomContaining(String nom) { return new ArrayList<>(); }
    @Override public List<Secretaire> findAllByCabinetId(Long id) { return new ArrayList<>(); }
}