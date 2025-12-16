package ma.oralCare.service.modules.caisse.api;

import java.time.LocalDate;
import java.util.Map;

public interface RevenusService {

    /**
     * Calcule le total des revenus pour une période donnée.
     * @return Le montant total des revenus.
     */
    Double calculerRevenusTotaux(LocalDate start, LocalDate end);

    /**
     * Fournit une répartition des revenus par catégorie (e.g., par type de traitement).
     * @return Une Map avec la catégorie comme clé et le montant comme valeur.
     */
    Map<String, Double> getRevenusParCategorie(LocalDate start, LocalDate end);
}