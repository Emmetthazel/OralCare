package ma.oralCare.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.RoleLibelle;

import java.util.List;

/**
 * Entite representant un role dans le systeme OralCare
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Role extends BaseEntity {

    private RoleLibelle libelle;

    private List<String> privileges;

    private List<Utilisateur> utilisateurs;

    @Override
    public String toString() {
        return """
        Role {
          id = %d,
          libelle = %s,
          privilegesCount = %d,
          utilisateursCount = %d
        }
        """.formatted(
                getIdEntite(),
                libelle,
                privileges == null ? 0 : privileges.size(),
                utilisateurs == null ? 0 : utilisateurs.size()
        );
    }
}
