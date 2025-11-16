package ma.oralCare.entities.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.enums.NotificationPriorite;
import ma.oralCare.entities.enums.NotificationTitre;
import ma.oralCare.entities.enums.NotificationType;
import ma.oralCare.entities.staff.Utilisateur;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Entite representant une notification dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    private Long id;

    private NotificationTitre titre;

    private String message;

    private LocalDate date;

    private LocalTime time;

    private NotificationType type;

    private NotificationPriorite priorite;

    /**
     * Liste des utilisateurs destinataires
     */
    private List<Utilisateur> utilisateurs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Notification {
          id = %d,
          titre = %s,
          message = '%s',
          date = %s,
          time = %s,
          type = %s,
          priorite = %s,
          utilisateursCount = %d
        }
        """.formatted(
                id,
                titre,
                message,
                date,
                time,
                type,
                priorite,
                utilisateurs == null ? 0 : utilisateurs.size()
        );
    }
}
