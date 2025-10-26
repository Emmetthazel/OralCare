package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Entité représentant une notification dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {
    
    
    private Long id;
    
    private NotificationTitre titre;
    
    private String message;
    
    private LocalDate date;
    
    private LocalTime time;
    
    private NotificationType type;
    
    private NotificationPriorite Priorité;
    

    /**
     * Liste des utilisateurs destinataires
     */
    private List<Utilisateur> utilisateurs;
    
}
