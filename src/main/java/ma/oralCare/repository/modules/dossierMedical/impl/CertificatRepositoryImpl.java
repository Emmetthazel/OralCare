package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.repository.modules.dossierMedical.api.CertificatRepository;
import ma.oralCare.conf.SessionFactory;
import ma.oralCare.repository.modules.dossierMedical.api.ConsultationRepository; // Supposons que vous ayez ce repository
import ma.oralCare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl; // Implémentation ou une Factory

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CertificatRepositoryImpl implements CertificatRepository {

    private final ConsultationRepository consultationRepository = new ConsultationRepositoryImpl(); // Dépendance

    // Requêtes SQL
// Ajoutez la table BaseEntity (be) via JOIN
    private static final String SELECT_ALL_SQL =
            "SELECT c.*, co.dossier_medicale_id, " +
                    "be.date_creation, be.cree_par, be.date_derniere_modification, be.modifie_par " + // <-- AJOUT CRITIQUE
                    "FROM Certificat c " +
                    "JOIN BaseEntity be ON c.id_entite = be.id_entite " + // <-- AJOUT CRITIQUE
                    "JOIN Consultation co ON c.consultation_id = co.id_entite " +
                    "WHERE 1=1";
    private static final String SELECT_BY_ID_SQL = SELECT_ALL_SQL + " AND c.id_entite = ?";
    private static final String INSERT_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String INSERT_CERTIFICAT_SQL = "INSERT INTO Certificat (id_entite, date_debut, date_fin, duree, note_medecin, consultation_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_BASE_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String UPDATE_CERTIFICAT_SQL = "UPDATE Certificat SET date_debut = ?, date_fin = ?, duree = ?, note_medecin = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";
    private static final String FIND_BY_DOSSIER_ID_SQL = SELECT_ALL_SQL + " AND co.dossier_medicale_id = ?";
    private static final String FIND_BY_DATE_DEBUT_SQL = SELECT_ALL_SQL + " AND c.date_debut = ?";
    private static final String FIND_VALID_CERTIFICATES_SQL = SELECT_ALL_SQL + " AND c.date_fin >= ?";
    private static final String FIND_BY_NOTE_CONTAINING_SQL = SELECT_ALL_SQL + " AND c.note_medecin LIKE ?";

    // --- Mapper ---
    private Certificat mapResultSetToCertificat(ResultSet rs) throws SQLException {
        Certificat certificat = Certificat.builder()
                .idEntite(rs.getLong("id_entite"))
                .dateDebut(rs.getDate("date_debut") != null ? rs.getDate("date_debut").toLocalDate() : null)
                .dateFin(rs.getDate("date_fin") != null ? rs.getDate("date_fin").toLocalDate() : null)
                .duree(rs.getInt("duree"))
                .noteMedecin(rs.getString("note_medecin"))
                .build();

        // Récupération des champs de BaseEntity
        if (rs.getTimestamp("date_creation") != null) {
            certificat.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        }
        if (rs.getTimestamp("date_derniere_modification") != null) {
            certificat.setDateDerniereModification(rs.getTimestamp("date_derniere_modification").toLocalDateTime());
        }
        certificat.setCreePar(rs.getLong("cree_par"));
        certificat.setModifiePar(rs.getLong("modifie_par"));

        // La consultation doit être chargée séparément (Lazy Loading ou juste l'ID)
        // Pour l'instant, on laisse la consultation à null ou on initialise un stub

        // On pourrait ajouter un champ 'consultation_id' si on le récupère du SELECT
        // long consultationId = rs.getLong("consultation_id");

        return certificat;
    }

    // --- CRUD Standard ---

    @Override
    public List<Certificat> findAll() {
        List<Certificat> certificats = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                certificats.add(mapResultSetToCertificat(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les certificats: " + e.getMessage());
        }
        return certificats;
    }

    @Override
    public Optional<Certificat> findById(Long id) {
        Certificat certificat = null;
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    certificat = mapResultSetToCertificat(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du certificat ID " + id + ": " + e.getMessage());
        }
        return Optional.ofNullable(certificat);
    }

    @Override
    public void create(Certificat newElement) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Insertion dans BaseEntity
            try (PreparedStatement psBase = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(newElement.getDateCreation()));
                psBase.setObject(2, newElement.getCreePar());
                psBase.executeUpdate();

                try (ResultSet generatedKeys = psBase.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long newId = generatedKeys.getLong(1);
                        newElement.setIdEntite(newId);

                        // 2. Insertion dans Certificat
                        try (PreparedStatement psCertificat = conn.prepareStatement(INSERT_CERTIFICAT_SQL)) {
                            psCertificat.setLong(1, newId);
                            psCertificat.setDate(2, Date.valueOf(newElement.getDateDebut()));
                            psCertificat.setDate(3, Date.valueOf(newElement.getDateFin()));
                            psCertificat.setInt(4, newElement.getDuree());
                            psCertificat.setString(5, newElement.getNoteMedecin());
                            psCertificat.setLong(6, newElement.getConsultation().getIdEntite());
                            psCertificat.executeUpdate();
                        }
                    } else {
                        throw new SQLException("Échec de la création, aucun ID généré.");
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du certificat: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void update(Certificat newValuesElement) {
        Connection conn = null;
        try {
            conn = SessionFactory.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1. Mise à jour de BaseEntity
            try (PreparedStatement psBase = conn.prepareStatement(UPDATE_BASE_SQL)) {
                psBase.setTimestamp(1, Timestamp.valueOf(newValuesElement.getDateDerniereModification()));
                psBase.setObject(2, newValuesElement.getModifiePar());
                psBase.setLong(3, newValuesElement.getIdEntite());
                psBase.executeUpdate();
            }

            // 2. Mise à jour de Certificat
            try (PreparedStatement psCertificat = conn.prepareStatement(UPDATE_CERTIFICAT_SQL)) {
                psCertificat.setDate(1, Date.valueOf(newValuesElement.getDateDebut()));
                psCertificat.setDate(2, Date.valueOf(newValuesElement.getDateFin()));
                psCertificat.setInt(3, newValuesElement.getDuree());
                psCertificat.setString(4, newValuesElement.getNoteMedecin());
                psCertificat.setLong(5, newValuesElement.getIdEntite());
                psCertificat.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du certificat ID " + newValuesElement.getIdEntite() + ": " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erreur lors du rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void delete(Certificat certificat) {
        deleteById(certificat.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BASE_SQL)) {

            // Grâce à ON DELETE CASCADE sur BaseEntity, une seule suppression suffit.
            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du certificat ID " + id + ": " + e.getMessage());
        }
    }

    // --- Méthodes Spécifiques à Certificat ---

    @Override
    public List<Certificat> findByDossierMedicaleId(Long dossierMedicaleId) {
        List<Certificat> certificats = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_DOSSIER_ID_SQL)) {

            ps.setLong(1, dossierMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(mapResultSetToCertificat(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des certificats pour le dossier ID " + dossierMedicaleId + ": " + e.getMessage());
        }
        return certificats;
    }

    @Override
    public List<Certificat> findByDateDebut(LocalDate date) {
        List<Certificat> certificats = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_DATE_DEBUT_SQL)) {

            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(mapResultSetToCertificat(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des certificats par date de début: " + e.getMessage());
        }
        return certificats;
    }

    @Override
    public List<Certificat> findValidCertificates(LocalDate currentDate) {
        List<Certificat> certificats = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_VALID_CERTIFICATES_SQL)) {

            // WHERE c.date_fin >= ?
            ps.setDate(1, Date.valueOf(currentDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(mapResultSetToCertificat(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des certificats valides: " + e.getMessage());
        }
        return certificats;
    }

    @Override
    public List<Certificat> findByNoteMedecinContaining(String noteFragment) {
        List<Certificat> certificats = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_NOTE_CONTAINING_SQL)) {

            ps.setString(1, "%" + noteFragment + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(mapResultSetToCertificat(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des certificats par note: " + e.getMessage());
        }
        return certificats;
    }
}