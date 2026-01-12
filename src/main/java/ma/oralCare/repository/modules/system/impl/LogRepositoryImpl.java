package ma.oralCare.repository.modules.system.impl;

import ma.oralCare.conf.SessionFactory; // ✅ Import indispensable
import ma.oralCare.repository.modules.system.api.LogRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation autonome du LogRepository.
 * Elle récupère sa connexion directement depuis le SessionFactory.
 */
public class LogRepositoryImpl implements LogRepository {

    // ✅ ÉTAPE 1 : Suppression du champ 'private final Connection connection'

    public LogRepositoryImpl() {
        // ✅ ÉTAPE 2 : Constructeur vide pour correspondre à l'appel dans MainFrame
    }

    @Override
    public long countTodayLogs() {
        String sql = "SELECT COUNT(*) FROM logs_audit WHERE date_action = CURDATE()";

        try {
            // ✅ ÉTAPE 3 : Récupération de la connexion active
            Connection conn = SessionFactory.getInstance().getConnection();

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[LOG-REPO] Erreur countTodayLogs: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public List<String> getLatestLogs(int limit) {
        List<String> logs = new ArrayList<>();
        String sql = """
            SELECT heure_action, action_description, utilisateur_login 
            FROM logs_audit 
            WHERE date_action = CURDATE() 
            ORDER BY heure_action DESC, id_log DESC 
            LIMIT ?
        """;

        try {
            Connection conn = SessionFactory.getInstance().getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, limit);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Time heure = rs.getTime("heure_action");
                        String timeStr = (heure != null) ? heure.toString() : "--:--:--";

                        String logLine = String.format("[%s] %s : %s",
                                timeStr,
                                rs.getString("utilisateur_login"),
                                rs.getString("action_description"));
                        logs.add(logLine);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[LOG-REPO] Erreur getLatestLogs: " + e.getMessage());
        }
        return logs;
    }

    @Override
    public void saveLog(String adminLogin, Long cabinetId, String description) {
        String sql = """
            INSERT INTO logs_audit (utilisateur_login, id_cabinet_concerne, action_description, date_action, heure_action) 
            VALUES (?, ?, ?, CURDATE(), CURTIME())
        """;

        try {
            Connection conn = SessionFactory.getInstance().getConnection();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, adminLogin);

                if (cabinetId != null) {
                    ps.setLong(2, cabinetId);
                } else {
                    ps.setNull(2, Types.BIGINT);
                }

                ps.setString(3, description);
                ps.executeUpdate();

            }
        } catch (SQLException e) {
            System.err.println("[LOG-REPO] Erreur saveLog: " + e.getMessage());
        }
    }
}