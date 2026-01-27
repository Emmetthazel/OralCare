package ma.oralCare.mvc.ui.admin.security;

import ma.oralCare.mvc.controllers.admin.api.AdminDashboardController;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Vue combinée pour la Sécurité et l'Audit
 * Interface unique regroupant la sauvegarde et les logs
 */
public class SecurityAuditView extends JPanel {
    
    private final AdminDashboardController dashboardCtrl;
    private BackupManagerView backupView;
    private AuditLogView auditView;
    
    // Palette de couleurs moderne
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_LIGHT = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    
    public SecurityAuditView(AdminDashboardController dashboardCtrl) {
        this.dashboardCtrl = dashboardCtrl;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_LIGHT);
        
        // Panneau principal avec JSplitPane horizontal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.5);
        
        // Panneau gauche : Sécurité & Sauvegarde
        JPanel securityPanel = createSecurityPanel();
        
        // Panneau droit : Logs & Audit
        JPanel auditPanel = createAuditPanel();
        
        splitPane.setLeftComponent(securityPanel);
        splitPane.setRightComponent(auditPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    /**
     * Crée le panneau pour la Sécurité & Sauvegarde
     */
    private JPanel createSecurityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            "🛡️ Sécurité & Sauvegarde"
        ));
        
        backupView = new BackupManagerView();
        panel.add(backupView, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée le panneau pour les Logs & Audit
     */
    private JPanel createAuditPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            "📑 Logs & Audit"
        ));
        
        auditView = new AuditLogView(dashboardCtrl);
        panel.add(auditView, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Rafraîchit les logs d'audit
     */
    public void refreshLogs() {
        if (auditView != null) {
            auditView.refreshLogs();
        }
    }
    
    /**
     * Obtient la vue de sauvegarde
     */
    public BackupManagerView getBackupView() {
        return backupView;
    }
    
    /**
     * Obtient la vue d'audit
     */
    public AuditLogView getAuditView() {
        return auditView;
    }
}
