package ma.oralCare.entities.system;

import java.time.LocalDate;
import java.time.LocalTime;

public class LogAudit {
    private Long idLog;
    private LocalDate dateAction;
    private LocalTime heureAction;
    private String utilisateurLogin;
    private Long idCabinetConcerne;
    private String actionDescription;

    // Constructeurs
    public LogAudit() {}

    // Getters et Setters
    public Long getIdLog() { return idLog; }
    public void setIdLog(Long idLog) { this.idLog = idLog; }

    public LocalDate getDateAction() { return dateAction; }
    public void setDateAction(LocalDate dateAction) { this.dateAction = dateAction; }

    public LocalTime getHeureAction() { return heureAction; }
    public void setHeureAction(LocalTime heureAction) { this.heureAction = heureAction; }

    public String getUtilisateurLogin() { return utilisateurLogin; }
    public void setUtilisateurLogin(String utilisateurLogin) { this.utilisateurLogin = utilisateurLogin; }

    public Long getIdCabinetConcerne() { return idCabinetConcerne; }
    public void setIdCabinetConcerne(Long idCabinetConcerne) { this.idCabinetConcerne = idCabinetConcerne; }

    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }
}