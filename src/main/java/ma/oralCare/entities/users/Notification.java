package ma.oralCare.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.NotificationPriorite;
import ma.oralCare.entities.enums.NotificationTitre;
import ma.oralCare.entities.enums.NotificationType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Notification extends BaseEntity {

    private NotificationTitre titre;

    private String message;

    private LocalDate date;

    private LocalTime time;

    private NotificationType type;

    private NotificationPriorite priorite;

    private List<Utilisateur> utilisateurs;


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
                getIdEntite(),
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
