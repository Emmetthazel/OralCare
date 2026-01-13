package ma.oralCare.service.modules.realtime.api;

import ma.oralCare.service.modules.realtime.dto.RealTimeEvent;
import ma.oralCare.service.modules.realtime.dto.SubscriptionRequest;

import java.util.List;
import java.util.function.Consumer;

public interface RealTimeService {

    /**
     * Abonne un client aux événements en temps réel
     */
    String subscribeToEvents(SubscriptionRequest request);

    /**
     * Désabonne un client des événements
     */
    void unsubscribeFromEvents(String subscriptionId);

    /**
     * Publie un événement à tous les abonnés concernés
     */
    void publishEvent(RealTimeEvent event);

    /**
     * Publie un événement à un secrétaire spécifique
     */
    void publishEventToSecretaire(Long secretaireId, RealTimeEvent event);

    /**
     * Publie un événement à tous les secrétaires d'un cabinet
     */
    void publishEventToCabinet(Long cabinetId, RealTimeEvent event);

    /**
     * Récupère les événements récents pour un secrétaire
     */
    List<RealTimeEvent> getRecentEvents(Long secretaireId, int limit);

    /**
     * Vérifie si un abonnement est actif
     */
    boolean isSubscriptionActive(String subscriptionId);

    /**
     * Nettoie les abonnements expirés
     */
    void cleanupExpiredSubscriptions();

    /**
     * Démarre le service de temps réel
     */
    void startRealTimeService();

    /**
     * Arrête le service de temps réel
     */
    void stopRealTimeService();

    /**
     * Enregistre un écouteur d'événements personnalisé
     */
    void registerEventListener(String eventType, Consumer<RealTimeEvent> listener);

    /**
     * Supprime un écouteur d'événements
     */
    void unregisterEventListener(String eventType, Consumer<RealTimeEvent> listener);
}
