package ma.oralCare.entities.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

public abstract class BaseEntity {

    private Long idEntite; // ID unique pour toutes les entités
    private LocalDateTime dateCreation;
    private LocalDateTime dateDerniereModification;
    private Long modifiePar;
    private Long creePar;

    public void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateDerniereModification = LocalDateTime.now();
    }

    public void onUpdate() {
        this.dateDerniereModification = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof BaseEntity that) { // pattern variable Java 16+
            return idEntite != null && idEntite.equals(that.idEntite);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return idEntite != null ? idEntite.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "idEntite=" + idEntite +
                ", dateCreation=" + dateCreation +
                ", dateDerniereModification=" + dateDerniereModification +
                ", creePar='" + creePar + '\'' +
                ", modifiePar='" + modifiePar + '\'' +
                '}';
    }
}
