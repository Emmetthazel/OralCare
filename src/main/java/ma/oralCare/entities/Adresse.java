package ma.oralCare.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe représentant une adresse physique
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Adresse {
    
    
    private String numero;
    
    private String rue;
    
    private String codePostal;
    
    private String ville;
    
    private String pays;
    
    private String complement; /* appartement, étage, etc. */
    
    
    public Adresse(String numero, String rue, String codePostal, String ville, String pays) {
        this.numero = numero;
        this.rue = rue;
        this.codePostal = codePostal;
        this.ville = ville;
        this.pays = pays;
    }
    
    /**
     * Retourne l'adresse complète formatée
     */
    public String getAdresseComplete() {
        StringBuilder sb = new StringBuilder();
        if (numero != null && !numero.isEmpty()) {
            sb.append(numero).append(" ");
        }
        if (rue != null && !rue.isEmpty()) {
            sb.append(rue).append(", ");
        }
        if (complement != null && !complement.isEmpty()) {
            sb.append(complement).append(", ");
        }
        if (codePostal != null && !codePostal.isEmpty()) {
            sb.append(codePostal).append(" ");
        }
        if (ville != null && !ville.isEmpty()) {
            sb.append(ville);
        }
        if (pays != null && !pays.isEmpty()) {
            sb.append(", ").append(pays);
        }
        return sb.toString();
    }
    
}
