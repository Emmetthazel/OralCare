package ma.oralCare.repository.modules.system.impl;

import ma.oralCare.conf.SessionFactory; // ✅ Import indispensable
import ma.oralCare.repository.modules.system.api.SystemRepository;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation autonome du SystemRepository.
 * Gère les statistiques de la base de données et la configuration globale.
 */
public class SystemRepositoryImpl implements SystemRepository {

    // ✅ Suppression du champ 'private final Connection connection'

    public SystemRepositoryImpl() {
        // ✅ Constructeur vide pour permettre l'instanciation autonome
    }

    @Override
    public double getDatabaseSizeInMB() {
        // SQL générique pour MySQL/MariaDB
        String sql = "SELECT SUM(data_length + index_length) / 1024 / 1024 " +
                "FROM information_schema.TABLES " +
                "WHERE table_schema = (SELECT DATABASE())";

        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[SYSTEM-REPO] Erreur calcul taille DB: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public String getConfigStatus(String key) {
        String sql = "SELECT statut FROM system_config WHERE config_key = ?";
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("statut");
                }
            }
        } catch (SQLException e) {
            System.err.println("[SYSTEM-REPO] Erreur getConfigStatus: " + e.getMessage());
        }
        return "UNKNOWN";
    }

    @Override
    public String getConfigValue(String key) {
        String sql = "SELECT config_value FROM system_config WHERE config_key = ?";
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("config_value");
                }
            }
        } catch (SQLException e) {
            System.err.println("[SYSTEM-REPO] Erreur getConfigValue: " + e.getMessage());
        }
        return "";
    }

    @Override
    public void updateConfig(String key, String value, String status, String description) {
        String sql = "INSERT INTO system_config (config_key, config_value, statut, description, derniere_maj) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON DUPLICATE KEY UPDATE " +
                "config_value = VALUES(config_value), " +
                "statut = VALUES(statut), " +
                "description = VALUES(description), " +
                "derniere_maj = CURRENT_TIMESTAMP";

        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, key);
                ps.setString(2, value);
                ps.setString(3, status);
                ps.setString(4, description);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour config système", e);
        }
    }

    @Override
    public Map<String, String> getAllConfigs() {
        Map<String, String> configs = new HashMap<>();
        String sql = "SELECT config_key, config_value FROM system_config";

        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    configs.put(rs.getString("config_key"), rs.getString("config_value"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[SYSTEM-REPO] Erreur getAllConfigs: " + e.getMessage());
        }
        return configs;
    }
}