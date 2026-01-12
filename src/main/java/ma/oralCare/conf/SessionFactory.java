package ma.oralCare.conf;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class SessionFactory {
    private static volatile SessionFactory INSTANCE;
    private String url, user, password, driver;

    private SessionFactory() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/db.properties")) {
            Properties props = new Properties();
            props.load(is);
            this.url = props.getProperty("datasource.url");
            this.user = props.getProperty("datasource.user");
            this.password = props.getProperty("datasource.password");
            this.driver = props.getProperty("datasource.driver");
            Class.forName(driver);
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement SessionFactory : " + e.getMessage());
        }
    }

    public static SessionFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (SessionFactory.class) {
                if (INSTANCE == null) INSTANCE = new SessionFactory();
            }
        }
        return INSTANCE;
    }

    // Retourne une NOUVELLE connexion à chaque fois
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}