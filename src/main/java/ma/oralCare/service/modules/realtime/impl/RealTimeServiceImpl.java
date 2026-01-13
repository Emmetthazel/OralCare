package ma.oralCare.service.modules.realtime.impl;

import ma.oralCare.service.modules.realtime.api.RealTimeService;
import ma.oralCare.service.modules.realtime.dto.RealTimeEvent;
import ma.oralCare.service.modules.realtime.dto.SubscriptionRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class RealTimeServiceImpl implements RealTimeService {

    private final Map<String, SubscriptionRequest> activeSubscriptions;
    private final Map<String, List<RealTimeEvent>> eventHistory;
    private final Map<String, List<Consumer<RealTimeEvent>>> eventListeners;
    private final Map<Long, List<String>> secretaireSubscriptions;
    private final Map<Long, List<String>> cabinetSubscriptions;
    
    private boolean serviceRunning = false;
    private final int MAX_HISTORY_SIZE = 100;

    public RealTimeServiceImpl() {
        this.activeSubscriptions = new ConcurrentHashMap<>();
        this.eventHistory = new ConcurrentHashMap<>();
        this.eventListeners = new ConcurrentHashMap<>();
        this.secretaireSubscriptions = new ConcurrentHashMap<>();
        this.cabinetSubscriptions = new ConcurrentHashMap<>();
    }

    @Override
    public String subscribeToEvents(SubscriptionRequest request) {
        if (request == null || request.getSubscriptionId() == null) {
            throw new IllegalArgumentException("Requête d'abonnement invalide");
        }

        try {
            activeSubscriptions.put(request.getSubscriptionId(), request);
            
            // Enregistrer l'abonnement par secrétaire
            if (request.getSecretaireId() != null) {
                secretaireSubscriptions.computeIfAbsent(request.getSecretaireId(), k -> new CopyOnWriteArrayList<>())
                    .add(request.getSubscriptionId());
            }
            
            // Enregistrer l'abonnement par cabinet
            if (request.getCabinetId() != null) {
                cabinetSubscriptions.computeIfAbsent(request.getCabinetId(), k -> new CopyOnWriteArrayList<>())
                    .add(request.getSubscriptionId());
            }
            
            // Initialiser l'historique pour cet abonnement
            eventHistory.putIfAbsent(request.getSubscriptionId(), new CopyOnWriteArrayList<>());
            
            logInfo("Nouvel abonnement: " + request.getSubscriptionId() + 
                   " pour secrétaire: " + request.getSecretaireId());
            
            return request.getSubscriptionId();
            
        } catch (Exception e) {
            logError("Erreur lors de l'abonnement: " + e.getMessage());
            throw new RuntimeException("Erreur d'abonnement", e);
        }
    }

    @Override
    public void unsubscribeFromEvents(String subscriptionId) {
        if (subscriptionId == null) return;

        try {
            SubscriptionRequest request = activeSubscriptions.remove(subscriptionId);
            if (request != null) {
                // Retirer des abonnements par secrétaire
                if (request.getSecretaireId() != null) {
                    List<String> subs = secretaireSubscriptions.get(request.getSecretaireId());
                    if (subs != null) {
                        subs.remove(subscriptionId);
                        if (subs.isEmpty()) {
                            secretaireSubscriptions.remove(request.getSecretaireId());
                        }
                    }
                }
                
                // Retirer des abonnements par cabinet
                if (request.getCabinetId() != null) {
                    List<String> subs = cabinetSubscriptions.get(request.getCabinetId());
                    if (subs != null) {
                        subs.remove(subscriptionId);
                        if (subs.isEmpty()) {
                            cabinetSubscriptions.remove(request.getCabinetId());
                        }
                    }
                }
                
                // Nettoyer l'historique
                eventHistory.remove(subscriptionId);
                
                logInfo("Abonnement résilié: " + subscriptionId);
            }
        } catch (Exception e) {
            logError("Erreur lors de la résiliation d'abonnement: " + e.getMessage());
        }
    }

    @Override
    public void publishEvent(RealTimeEvent event) {
        if (event == null || serviceRunning) return;

        try {
            // Ajouter l'événement à l'historique
            addToHistory(event);
            
            // Notifier les abonnés concernés
            notifySubscribers(event);
            
            // Notifier les écouteurs d'événements
            notifyEventListeners(event);
            
            logDebug("Événement publié: " + event.getEventType() + " - " + event.getMessage());
            
        } catch (Exception e) {
            logError("Erreur lors de la publication d'événement: " + e.getMessage());
        }
    }

    @Override
    public void publishEventToSecretaire(Long secretaireId, RealTimeEvent event) {
        if (secretaireId == null || event == null) return;

        event.setTargetUserId(secretaireId);
        publishEvent(event);
    }

    @Override
    public void publishEventToCabinet(Long cabinetId, RealTimeEvent event) {
        if (cabinetId == null || event == null) return;

        event.setTargetCabinetId(cabinetId);
        publishEvent(event);
    }

    @Override
    public List<RealTimeEvent> getRecentEvents(Long secretaireId, int limit) {
        if (secretaireId == null) return Collections.emptyList();

        List<RealTimeEvent> allEvents = new ArrayList<>();
        
        // Récupérer les événements de tous les abonnements du secrétaire
        List<String> subs = secretaireSubscriptions.get(secretaireId);
        if (subs != null) {
            for (String subId : subs) {
                List<RealTimeEvent> events = eventHistory.get(subId);
                if (events != null) {
                    allEvents.addAll(events);
                }
            }
        }
        
        // Trier par date et limiter
        allEvents.sort((e1, e2) -> e2.getTimestamp().compareTo(e1.getTimestamp()));
        return allEvents.stream().limit(Math.min(limit, MAX_HISTORY_SIZE)).toList();
    }

    @Override
    public boolean isSubscriptionActive(String subscriptionId) {
        return activeSubscriptions.containsKey(subscriptionId);
    }

    @Override
    public void cleanupExpiredSubscriptions() {
        // Implémentation simple - dans un vrai système, on vérifierait les timestamps
        logDebug("Nettoyage des abonnements expirés - " + activeSubscriptions.size() + " actifs");
    }

    @Override
    public void startRealTimeService() {
        if (!serviceRunning) {
            serviceRunning = true;
            logInfo("Service temps réel démarré");
            
            // Démarrer le thread de nettoyage
            startCleanupThread();
        }
    }

    @Override
    public void stopRealTimeService() {
        if (serviceRunning) {
            serviceRunning = false;
            logInfo("Service temps réel arrêté");
        }
    }

    @Override
    public void registerEventListener(String eventType, Consumer<RealTimeEvent> listener) {
        if (eventType != null && listener != null) {
            eventListeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
            logDebug("Écouteur enregistré pour: " + eventType);
        }
    }

    @Override
    public void unregisterEventListener(String eventType, Consumer<RealTimeEvent> listener) {
        if (eventType != null && listener != null) {
            List<Consumer<RealTimeEvent>> listeners = eventListeners.get(eventType);
            if (listeners != null) {
                listeners.remove(listener);
                if (listeners.isEmpty()) {
                    eventListeners.remove(eventType);
                }
                logDebug("Écouteur supprimé pour: " + eventType);
            }
        }
    }

    /**
     * Ajoute un événement à l'historique
     */
    private void addToHistory(RealTimeEvent event) {
        for (Map.Entry<String, SubscriptionRequest> entry : activeSubscriptions.entrySet()) {
            String subId = entry.getKey();
            SubscriptionRequest request = entry.getValue();
            
            if (shouldReceiveEvent(request, event)) {
                List<RealTimeEvent> events = eventHistory.computeIfAbsent(subId, k -> new CopyOnWriteArrayList<>());
                events.add(event);
                
                // Limiter la taille de l'historique
                if (events.size() > MAX_HISTORY_SIZE) {
                    events.remove(0);
                }
            }
        }
    }

    /**
     * Notifie les abonnés concernés par un événement
     */
    private void notifySubscribers(RealTimeEvent event) {
        for (Map.Entry<String, SubscriptionRequest> entry : activeSubscriptions.entrySet()) {
            String subId = entry.getKey();
            SubscriptionRequest request = entry.getValue();
            
            if (shouldReceiveEvent(request, event)) {
                // Dans une vraie implémentation, on enverrait l'événement via WebSocket ou autre
                logDebug("Notification envoyée à l'abonnement: " + subId);
            }
        }
    }

    /**
     * Notifie les écouteurs d'événements
     */
    private void notifyEventListeners(RealTimeEvent event) {
        List<Consumer<RealTimeEvent>> listeners = eventListeners.get(event.getEventType());
        if (listeners != null) {
            for (Consumer<RealTimeEvent> listener : listeners) {
                try {
                    listener.accept(event);
                } catch (Exception e) {
                    logError("Erreur dans l'écouteur d'événements: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Vérifie si un abonnement doit recevoir un événement
     */
    private boolean shouldReceiveEvent(SubscriptionRequest request, RealTimeEvent event) {
        if (request.isSubscribeToAll()) {
            return true;
        }
        
        // Vérifier le type d'événement
        if (!request.isInterestedIn(event.getEventType())) {
            return false;
        }
        
        // Vérifier la cible (secrétaire ou cabinet)
        if (request.getSecretaireId() != null && !request.getSecretaireId().equals(event.getTargetUserId())) {
            return false;
        }
        
        if (request.getCabinetId() != null && !request.getCabinetId().equals(event.getTargetCabinetId())) {
            return false;
        }
        
        return true;
    }

    /**
     * Démarre le thread de nettoyage
     */
    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (serviceRunning) {
                try {
                    Thread.sleep(300000); // 5 minutes
                    cleanupExpiredSubscriptions();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    /**
     * Méthodes de logging
     */
    private void logInfo(String message) {
        System.out.println("[REALTIME-SERVICE] INFO: " + message);
    }

    private void logDebug(String message) {
        System.out.println("[REALTIME-SERVICE] DEBUG: " + message);
    }

    private void logError(String message) {
        System.err.println("[REALTIME-SERVICE] ERROR: " + message);
    }
}
