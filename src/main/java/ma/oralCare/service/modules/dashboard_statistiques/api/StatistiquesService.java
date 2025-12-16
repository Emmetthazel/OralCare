package ma.oralCare.service.modules.dashboard_statistiques.api;

import ma.oralCare.entities.cabinet.Statistiques;
import java.util.List;
import java.util.Optional;


public interface StatistiquesService {
    // Créer une nouvelle statistique
    Statistiques createStatistique(Statistiques statistiques);

    // Mettre à jour une statistique existante
    Statistiques updateStatistique(Statistiques statistiques);

    // Récupérer une statistique par son ID
    Optional<Statistiques> getById(Long id);

    // Récupérer toutes les statistiques
    List<Statistiques> getAll();

    // Supprimer une statistique par son ID
    void deleteById(Long id);
}
