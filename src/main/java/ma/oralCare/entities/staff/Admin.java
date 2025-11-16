package ma.oralCare.entities.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entite representant un administrateur dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends Utilisateur {

    private List<String> permissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin)) return false;
        Admin that = (Admin) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Admin {
          id = %d,
          nom = '%s',
          email = '%s',
          permissionsCount = %d
        }
        """.formatted(
                getId(),
                getNom(),
                getEmail(),
                permissions == null ? 0 : permissions.size()
        );
    }
}
