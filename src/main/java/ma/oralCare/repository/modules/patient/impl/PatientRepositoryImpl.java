package ma.oralCare.repository.modules.patient.impl; // Notez le changement de package

import ma.oralCare.entities.patient.*; // Notez le changement de package
import ma.oralCare.conf.SessionFactory; // Assurez-vous que ce package est correct
import ma.oralCare.repository.common.RowMappers; // Notez le changement de package
import ma.oralCare.repository.modules.patient.api.*; // Assurez-vous que ce package est correct

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepositoryImpl implements PatientRepository {

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT\n" +
                "            p.nom, p.prenom, p.date_de_naissance, p.email, p.sexe, p.adresse, p.telephone, p.assurance,\n" +
                "            b.id_entite, \n" +
                "            b.date_creation,\n" +
                "            b.date_derniere_modification,\n" +
                "            b.cree_par,\n" +
                "            b.modifie_par\n" +
                "        FROM Patient p JOIN BaseEntity b ON p.id_entite = b.id_entite\n" +
                "        ORDER BY p.id_entite";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de findAll patients.", e); }
        return out;
    }

    @Override
    public Optional<Patient> findById(Long id) {
        String sql = "SELECT\n" +
                "                p.nom, p.prenom, p.date_de_naissance, p.email, p.sexe, p.adresse, p.telephone, p.assurance,\n" +
                "                b.id_entite, /* <--- L'ID DOIT VENIR DE BASEENTITY */\n" +
                "                b.date_creation,\n" +
                "                b.date_derniere_modification,\n" +
                "                b.cree_par,\n" +
                "                b.modifie_par\n" +
                "            FROM Patient p JOIN BaseEntity b ON p.id_entite = b.id_entite\n" +
                "            WHERE p.id_entite = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapPatient(rs));
                }
                return Optional.empty();

            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findById patient.", e);
        }
    }

    @Override
    public void create(Patient p) {
        Long baseId = null;
        Connection c = null;

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now); // Convertir pour l'insertion SQL
        // cree_par est BIGINT
        String sqlBase = "INSERT INTO BaseEntity(date_creation, date_derniere_modification, cree_par) VALUES(?, ?, ?)";

        String sqlPatient = """
            INSERT INTO Patient(id_entite, nom, prenom, date_de_naissance, email, sexe, adresse, telephone, assurance)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Début de la transaction

            // 1. Insertion dans BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, nowTimestamp);
                psBase.setNull(2, Types.TIMESTAMP);

                // UTILISATION DE BIGINT (p.getCreePar() doit retourner Long)
                if (p.getCreePar() != null) psBase.setLong(3, p.getCreePar());
                else psBase.setNull(3, Types.BIGINT);

                psBase.executeUpdate();

                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                p.setIdEntite(baseId);
                p.setDateCreation(now);
                p.setDateDerniereModification(null);
                p.setModifiePar(null);
            }

            // 2. Insertion dans Patient
            try (PreparedStatement psPatient = c.prepareStatement(sqlPatient)) {
                psPatient.setLong(1, p.getIdEntite()); // Utilise l'ID généré par BaseEntity
                psPatient.setString(2, p.getNom());
                psPatient.setString(3, p.getPrenom());
                if (p.getDateDeNaissance() != null) psPatient.setDate(4, Date.valueOf(p.getDateDeNaissance()));
                else psPatient.setNull(4, Types.DATE);
                // Le champ email n'est pas mappé dans mapPatient, mais il doit être là
                psPatient.setString(5, p.getEmail());
                psPatient.setString(6, p.getSexe().name());
                psPatient.setString(7, p.getAdresse());
                psPatient.setString(8, p.getTelephone());
                psPatient.setString(9, p.getAssurance().name());
                psPatient.executeUpdate();
            }

            c.commit(); // Validation

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la création du patient.", e);

        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); }
            }
        }
    }

    /**
     * Met à jour le patient dans BaseEntity et Patient.
     */
    @Override
    public void update(Patient p) {
        Connection c = null;

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
        String sqlPatient = """
            UPDATE Patient SET nom=?, prenom=?, date_de_naissance=?, email=?, 
                   sexe=?, adresse=?, telephone=?, assurance=? 
            WHERE id_entite=?
            """;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Début de la transaction

            // 1. Mise à jour de BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase)) {
                psBase.setTimestamp(1, nowTimestamp);
                // UTILISATION DE BIGINT (p.getModifiePar() doit retourner Long)
                if (p.getModifiePar() != null) psBase.setLong(2, p.getModifiePar());
                else psBase.setNull(2, Types.BIGINT);

                psBase.setLong(3, p.getIdEntite()); // Utilise getIdEntite()
                psBase.executeUpdate();
                p.setDateDerniereModification(now);
            }

            // 2. Mise à jour de Patient
            try (PreparedStatement psPatient = c.prepareStatement(sqlPatient)) {
                psPatient.setString(1, p.getNom());
                psPatient.setString(2, p.getPrenom());
                if (p.getDateDeNaissance() != null) psPatient.setDate(3, Date.valueOf(p.getDateDeNaissance()));
                else psPatient.setNull(3, Types.DATE);
                psPatient.setString(4, p.getEmail());
                psPatient.setString(5, p.getSexe().name());
                psPatient.setString(6, p.getAdresse());
                psPatient.setString(7, p.getTelephone());
                psPatient.setString(8, p.getAssurance().name());
                psPatient.setLong(9, p.getIdEntite());
                psPatient.executeUpdate();
            }

            c.commit(); // Validation

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on update.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la mise à jour du patient.", e);

        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); }
            }
        }
    }


    @Override
    public void delete(Patient p) { if (p != null) deleteById(p.getIdEntite()); } // Utilise getIdEntite()

    /**
     * Supprime le patient en supprimant la ligne dans BaseEntity.
     * ON DELETE CASCADE supprime les lignes dans Patient et Patient_Antecedent.
     */
    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression du patient par ID.", e); }
    }

    // -------- Extras (Recherche et Pagination) --------

    @Override
    public Optional<Patient> findByEmail(String email) {
        String sql = "SELECT \n" +
                "            p.nom, p.prenom, p.date_de_naissance, p.email, p.sexe, p.adresse, p.telephone, p.assurance,\n" +
                "            b.id_entite, /* <--- L'ID DOIT VENIR DE BASEENTITY */\n" +
                "            b.date_creation, \n" +
                "            b.date_derniere_modification, \n" +
                "            b.cree_par, \n" +
                "            b.modifie_par \n" +
                "        FROM Patient p \n" +
                "        JOIN BaseEntity b ON p.id_entite = b.id_entite \n" +
                "        WHERE p.email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapPatient(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de findByEmail.", e); }
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        String sql = "SELECT \n" +
                "            p.nom, p.prenom, p.date_de_naissance, p.email, p.sexe, p.adresse, p.telephone, p.assurance,\n" +
                "            b.id_entite, \n" +
                "            b.date_creation, \n" +
                "            b.date_derniere_modification, \n" +
                "            b.cree_par, \n" +
                "            b.modifie_par \n" +
                "        FROM Patient p \n" +
                "        JOIN BaseEntity b ON p.id_entite = b.id_entite \n" +
                "        WHERE p.telephone = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, telephone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapPatient(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de findByTelephone.", e); }
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        String sql = "SELECT * FROM Patient WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de searchByNomPrenom.", e); }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM Patient WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de existsById.", e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Patient";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de count().", e); }
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Patient ORDER BY id_entite LIMIT ? OFFSET ?";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de findPage.", e); }
        return out;
    }

    // -------- Liaison Many-to-Many (Patient_Antecedent) --------

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        String sql = "INSERT INTO Patient_Antecedent(patient_id, antecedent_id) VALUES (?,?) ON DUPLICATE KEY UPDATE patient_id=patient_id";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de addAntecedentToPatient.", e); }
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        String sql = "DELETE FROM Patient_Antecedent WHERE patient_id=? AND antecedent_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de removeAntecedentFromPatient.", e); }
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        String sql = "DELETE FROM Patient_Antecedent WHERE patient_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de removeAllAntecedentsFromPatient.", e); }
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        String sql = """
            SELECT a.* FROM Antecedent a 
            JOIN Patient_Antecedent pa ON pa.antecedent_id = a.id_entite
            WHERE pa.patient_id = ?
            ORDER BY a.categorie, a.niveau_de_risque, a.nom
            """;
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de getAntecedentsOfPatient.", e); }
        return out;
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        String sql = """
            SELECT p.* FROM Patient p JOIN Patient_Antecedent pa ON pa.patient_id = p.id_entite
            WHERE pa.antecedent_id = ?
            ORDER BY p.nom, p.prenom
            """;
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, antecedentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de getPatientsByAntecedent.", e); }
        return out;
    }
}