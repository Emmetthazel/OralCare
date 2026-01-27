package ma.oralCare.mvc;

import ma.oralCare.conf.SessionFactory;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class StaffCreator {

    public static void main(String[] args) {
        System.out.println("🚀 Démarrage de l'insertion du personnel de test...");

        try (Connection cnx = SessionFactory.getInstance().getConnection()) {
            cnx.setAutoCommit(false); // Utilisation d'une transaction pour la sécurité

            try {
                // 1. Nettoyage des anciens comptes pour éviter les erreurs "Duplicate entry"
                // On supprime de BaseEntity, le CASCADE s'occupe du reste
                executeSimple(cnx, "DELETE FROM BaseEntity WHERE id_entite IN (1001, 1002)");
                System.out.println("🧹 Anciens comptes de test nettoyés.");

                // 2. Création du Médecin (ID: 1001, Role DOCTOR: 2, Cabinet: 10)
                createStaff(cnx, 1001, "House", "Gregory", "house", "doctor123", "Chirurgien Dentiste", 2, 10, true);

                // 3. Création de la Secrétaire (ID: 1002, Role SECRETARY: 3, Cabinet: 10)
                createStaff(cnx, 1002, "Smith", "Jane", "jane", "jane123", null, 3, 10, false);

                cnx.commit();
                System.out.println("\n✅ SUCCÈS : Le personnel a été créé et lié au cabinet 10 !");
                System.out.println("   - Médecin    : house / doctor123");
                System.out.println("   - Secrétaire : jane / jane123");

            } catch (Exception e) {
                cnx.rollback();
                System.err.println("❌ ERREUR lors de l'insertion : " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR de connexion via SessionFactory : " + e.getMessage());
        }
    }

    private static void createStaff(Connection cnx, long id, String nom, String prenom,
                                    String login, String passwordClair, String specialite,
                                    long roleId, long cabinetId, boolean isDoctor) throws Exception {

        // Hachage du mot de passe avec BCrypt
        String passwordHashe = BCrypt.hashpw(passwordClair, BCrypt.gensalt());

        // A. BaseEntity
        String sqlBase = "INSERT INTO BaseEntity (id_entite, date_creation) VALUES (?, NOW())";
        try (PreparedStatement ps = cnx.prepareStatement(sqlBase)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }

        // B. Utilisateur
        String sqlUser = "INSERT INTO utilisateur (id_entite, nom, prenom, login, mot_de_pass) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sqlUser)) {
            ps.setLong(1, id);
            ps.setString(2, nom);
            ps.setString(3, prenom);
            ps.setString(4, login);
            ps.setString(5, passwordHashe);
            ps.executeUpdate();
        }

        // C. Staff (Lien avec le cabinet médical ID 10)
        String sqlStaff = "INSERT INTO Staff (id_entite, salaire, cabinet_id, date_recrutement) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sqlStaff)) {
            ps.setLong(1, id);
            ps.setDouble(2, isDoctor ? 15000.0 : 5000.0);
            ps.setLong(3, cabinetId);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
        }

        // D. Rôle et Spécification
        if (isDoctor) {
            String sqlMed = "INSERT INTO Medecin (id_entite, specialite) VALUES (?, ?)";
            try (PreparedStatement ps = cnx.prepareStatement(sqlMed)) {
                ps.setLong(1, id);
                ps.setString(2, specialite);
                ps.executeUpdate();
            }
        } else {
            String sqlSec = "INSERT INTO Secretaire (id_entite, num_cnss) VALUES (?, ?)";
            try (PreparedStatement ps = cnx.prepareStatement(sqlSec)) {
                ps.setLong(1, id);
                ps.setString(2, "CNSS-" + id);
                ps.executeUpdate();
            }
        }

        // E. Lien Utilisateur_Role
        String sqlRole = "INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES (?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sqlRole)) {
            ps.setLong(1, id);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        }
    }

    private static void executeSimple(Connection cnx, String sql) throws Exception {
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}