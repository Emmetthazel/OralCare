package ma.oralCare.repository;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.acte.Acte;
import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.consultation.InterventionMedecin;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.enums.Assurance;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.entities.medicament.Medicament;
import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.patient.Antecedent;

import java.sql.*;
import java.util.List;

public final class DbTestUtils {

    private DbTestUtils() {}

    // ==========================
    // 1) NETTOYAGE COMPLET
    // ==========================
    public static void cleanAll() {
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement()) {

            st.execute("SET FOREIGN_KEY_CHECKS = 0");

            st.executeUpdate("DELETE FROM prescriptions");
            st.executeUpdate("DELETE FROM medicament");
            st.executeUpdate("DELETE FROM certificats");
            st.executeUpdate("DELETE FROM ordonnances");
            st.executeUpdate("DELETE FROM rdv");
            st.executeUpdate("DELETE FROM actes");
            st.executeUpdate("DELETE FROM interventions_medecin");
            st.executeUpdate("DELETE FROM consultations");
            st.executeUpdate("DELETE FROM situations_financieres");
            st.executeUpdate("DELETE FROM factures");
            st.executeUpdate("DELETE FROM antecedents");
            st.executeUpdate("DELETE FROM dossiers_medicaux");
            st.executeUpdate("DELETE FROM patients");
            st.executeUpdate("DELETE FROM staff");
            st.executeUpdate("DELETE FROM medecins");
            st.executeUpdate("DELETE FROM secretaires");
            st.executeUpdate("DELETE FROM admins");
            st.executeUpdate("DELETE FROM utilisateur_notifications");
            st.executeUpdate("DELETE FROM utilisateur_roles");
            st.executeUpdate("DELETE FROM notifications");
            st.executeUpdate("DELETE FROM roles");
            st.executeUpdate("DELETE FROM utilisateurs");
            st.executeUpdate("DELETE FROM agendas_mensuels");
            st.executeUpdate("DELETE FROM cabinets_medicaux");

            // Reset AUTO_INCREMENT
            st.executeUpdate("ALTER TABLE actes AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE interventions_medecin AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE consultations AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE factures AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE dossiers_medicaux AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE patients AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE cabinets_medicaux AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE medicament AUTO_INCREMENT = 1");

            st.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (SQLException e) {
            throw new RuntimeException("Nettoyage BD échoué", e);
        }
    }

    // ==========================
    // 2) SEED GLOBAL
    // ==========================
    public static void seedFullDataset() {
        seedCabinet();
        seedPatients();
        seedDossiersMedicaux();
        seedConsultations();
        seedInterventions();
        seedActes();
        seedMedicaments();
    }

    // --------------------------
    // Seeds minimal pour tests
    // --------------------------

    public static Patient getFirstPatient() {
        String sql = "SELECT * FROM patients ORDER BY id_patient ASC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Patient p = new Patient();
                p.setId(rs.getLong("id_patient"));
                p.setNom(rs.getString("nom"));
                p.setPrenom(rs.getString("prenom")); // si colonne prenom existe
                p.setDateNaissance(rs.getDate("date_de_naissance") != null
                        ? rs.getDate("date_de_naissance").toLocalDate()
                        : null);
                p.setSexe(rs.getString("sexe") != null ? Sexe.valueOf(rs.getString("sexe")) : null);
                p.setAssurance(rs.getString("assurance") != null ? Assurance.valueOf(rs.getString("assurance")) : null);
                // ajouter autres champs si nécessaire
                return p;
            } else {
                throw new RuntimeException("Aucun patient trouvé en base de test");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération premier patient", e);
        }
    }



    /** Retourne le premier antécédent existant */
    public static Antecedent getFirstAntecedent() {
        // Implémenter selon ta table Antecedent et ton RowMapper
        // Ici on suppose que tu as un AntecedentRepository similaire
        // Ou pour test simple, tu peux créer un Antecedent "dummy" et l'ajouter à la base
        Antecedent a = new Antecedent();
        a.setId(1L);
        a.setNom("Diabète");
        return a;
    }

    private static void seedCabinet() {
        execute("""
            INSERT INTO cabinets_medicaux (id_cabinet, nom, email, ville, pays)
            VALUES (1, 'OralCare Center', 'contact@oralcare.ma', 'Rabat', 'Maroc');
        """);
    }

    public static void seedPatients() {
        execute("""
            INSERT INTO patients (id_patient, nom, date_de_naissance, sexe, assurance)
            VALUES
            (1,'Amal','1999-05-10','FEMALE','CNSS'),
            (2,'Omar','1995-02-20','MALE','CNOPS');
        """);
    }

    public static void seedDossiersMedicaux() {
        execute("""
            INSERT INTO dossiers_medicaux (id_dm, date_de_creation, id_patient)
            VALUES (1,CURDATE(),1),(2,CURDATE(),2);
        """);
    }

    private static void seedConsultations() {
        execute("""
            INSERT INTO consultations (id_consultation, date, statut, id_dm, id_medecin)
            VALUES
            (1,CURDATE(),'COMPLETED',1,2),
            (2,CURDATE(),'SCHEDULED',2,2);
        """);
    }

    private static void seedInterventions() {
        execute("""
            INSERT INTO interventions_medecin (id_im, prix_de_patient, id_consultation)
            VALUES (1,300,1),(2,500,2);
        """);
    }

    private static void seedActes() {
        execute("""
            INSERT INTO actes (id_acte, libelle, categorie, prix_de_base, id_im)
            VALUES
            (1,'Détartrage','PREVENTIF',300,1),
            (2,'Extraction','CHIRURGIE',500,2);
        """);
    }

    // ==========================
    // ✅ SEED MEDICAMENT INTÉGRÉ
    // ==========================
    private static void seedMedicaments() {
        execute("""
            INSERT INTO medicament (id, nom, laboratoire, type, forme, remboursable, prixUnitaire, description)
            VALUES
            (1,'Paracétamol','Sanofi','Antalgique','TABLET',true,10.5,'Contre la douleur'),
            (2,'Amoxicilline','Pfizer','Antibiotique','CAPSULE',true,25.0,'Antibiotique large spectre'),
            (3,'Sirop Toux','Bayer','Antitussif','SYRUP',false,18.0,'Pour la toux');
        """);
    }

    // ==========================
    // 3) Méthodes utilitaires
    // ==========================

    public static Medicament getFirstMedicament() {
        String sql = "SELECT * FROM medicament ORDER BY id ASC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Medicament.builder()
                        .id(rs.getLong("id"))
                        .nom(rs.getString("nom"))
                        .laboratoire(rs.getString("laboratoire"))
                        .type(rs.getString("type"))
                        .forme(FormeMedicament.valueOf(rs.getString("forme")))
                        .remboursable(rs.getBoolean("remboursable"))
                        .prixUnitaire(rs.getDouble("prixUnitaire"))
                        .description(rs.getString("description"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération premier médicament", e);
        }
        return null;
    }

    public static Acte getFirstActe() {
        String sql = "SELECT * FROM actes ORDER BY id_acte ASC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Acte.builder()
                        .id(rs.getLong("id_acte"))
                        .libelle(rs.getString("libelle"))
                        .categorie(rs.getString("categorie"))
                        .prixDeBase(rs.getDouble("prix_de_base"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération du premier acte", e);
        }
        return null;
    }

    public static Consultation getFirstConsultation() {
        String sql = "SELECT * FROM consultations ORDER BY id_consultation ASC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Consultation.builder()
                        .id(rs.getLong("id_consultation"))
                        .date(rs.getDate("date").toLocalDate())
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération première consultation", e);
        }
        return null;
    }

    public static InterventionMedecin getFirstIntervention() {
        String sql = "SELECT * FROM interventions_medecin ORDER BY id_im ASC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Long id = rs.getLong("id_im");
                Double prix = rs.getDouble("prix_de_patient");
                Long consultationId = rs.getLong("id_consultation");
                Consultation consultation = getConsultationById(consultationId);
                return InterventionMedecin.builder()
                        .id(id)
                        .prixDePatient(prix)
                        .consultation(consultation)
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération première intervention", e);
        }
        return null;
    }

    private static Consultation getConsultationById(Long id) {
        String sql = "SELECT * FROM consultations WHERE id_consultation = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Consultation.builder()
                            .id(rs.getLong("id_consultation"))
                            .date(rs.getDate("date").toLocalDate())
                            .build();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération consultation par ID", e);
        }
        return null;
    }

    // ==========================
    // 4) Exécution SQL utilitaire
    // ==========================
    private static void execute(String sql) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur seed SQL", e);
        }
    }
    // ==========================
// ✅ RECUPERATION DOSSIER MEDICAL
// ==========================
    public static DossierMedicale getFirstDossierMedicale() {
        String sql = "SELECT * FROM dossiers_medicaux ORDER BY id_dm ASC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return DossierMedicale.builder()
                        .id(rs.getLong("id_dm"))
                        .dateDeCreation(rs.getDate("date_de_creation").toLocalDate())
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération premier dossier médical", e);
        }
        return null;
    }
    public static RDV getFirstRDV() {
        String sql = "SELECT * FROM rdv ORDER BY id ASC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Long dossierId = rs.getLong("dossier_id");
                DossierMedicale dossier = getDossierById(dossierId); // utiliser la méthode existante
                return RDV.builder()
                        .id(rs.getLong("id"))
                        .date(rs.getDate("date").toLocalDate())
                        .heure(rs.getTime("heure").toLocalTime())
                        .motif(rs.getString("motif"))
                        .statut(StatutRDV.valueOf(rs.getString("statut")))
                        .dossierMedicale(dossier)
                        .noteMedecin(rs.getString("noteMedecin"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération premier RDV", e);
        }
        return null;
    }

    private static DossierMedicale getDossierById(Long id) {
        String sql = "SELECT * FROM dossiers_medicaux WHERE id_dm = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return DossierMedicale.builder()
                            .id(rs.getLong("id_dm"))
                            .dateDeCreation(rs.getDate("date_de_creation").toLocalDate())
                            .build();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération dossier médical par ID", e);
        }
        return null;
    }
    public static Ordonnance getFirstOrdonnance() {
        String sql = "SELECT * FROM ordonnances ORDER BY id ASC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Long dossierId = rs.getLong("id_dm");
                DossierMedicale dossier = getDossierById(dossierId);
                return Ordonnance.builder()
                        .id(rs.getLong("id"))
                        .date(rs.getDate("date").toLocalDate())
                        .dossierMedicale(dossier)
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération première ordonnance", e);
        }
        return null;
    }
}
