package ma.oralCare.entities.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.staff.Utilisateur;

import java.util.List;

/**
 * Entite representant un role dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    private Long id;

    private RoleLibelle libelle;

    private List<String> privileges;

    /**
     * Liste des utilisateurs ayant ce role
     */
    private List<Utilisateur> utilisateurs;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role that = (Role) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

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
                id,
                libelle,
                privileges == null ? 0 : privileges.size(),
                utilisateurs == null ? 0 : utilisateurs.size()
        );
    }
}
