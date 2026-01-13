package ma.oralCare.service.modules.realtime.dto;

import java.util.List;

public class SubscriptionRequest {
    private String subscriptionId;
    private Long secretaireId;
    private Long cabinetId;
    private List<String> eventTypes;
    private boolean subscribeToAll;
    private String clientInfo;

    public SubscriptionRequest() {
        this.subscribeToAll = false;
    }

    public SubscriptionRequest(Long secretaireId, Long cabinetId, List<String> eventTypes) {
        this();
        this.secretaireId = secretaireId;
        this.cabinetId = cabinetId;
        this.eventTypes = eventTypes;
        this.subscriptionId = generateSubscriptionId();
    }

    public static SubscriptionRequest forSecretaire(Long secretaireId, List<String> eventTypes) {
        return new SubscriptionRequest(secretaireId, null, eventTypes);
    }

    public static SubscriptionRequest forCabinet(Long cabinetId, List<String> eventTypes) {
        return new SubscriptionRequest(null, cabinetId, eventTypes);
    }

    public static SubscriptionRequest forAllEvents(Long secretaireId) {
        SubscriptionRequest request = new SubscriptionRequest();
        request.setSecretaireId(secretaireId);
        request.setSubscribeToAll(true);
        request.setSubscriptionId(request.generateSubscriptionId());
        return request;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getSecretaireId() {
        return secretaireId;
    }

    public void setSecretaireId(Long secretaireId) {
        this.secretaireId = secretaireId;
    }

    public Long getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(Long cabinetId) {
        this.cabinetId = cabinetId;
    }

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(List<String> eventTypes) {
        this.eventTypes = eventTypes;
    }

    public boolean isSubscribeToAll() {
        return subscribeToAll;
    }

    public void setSubscribeToAll(boolean subscribeToAll) {
        this.subscribeToAll = subscribeToAll;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * Vérifie si cet abonnement est intéressé par un type d'événement
     */
    public boolean isInterestedIn(String eventType) {
        return subscribeToAll || (eventTypes != null && eventTypes.contains(eventType));
    }

    /**
     * Vérifie si cet abonnement est pour un secrétaire spécifique
     */
    public boolean isForSecretaire(Long secretaireId) {
        return this.secretaireId != null && this.secretaireId.equals(secretaireId);
    }

    /**
     * Vérifie si cet abonnement est pour un cabinet spécifique
     */
    public boolean isForCabinet(Long cabinetId) {
        return this.cabinetId != null && this.cabinetId.equals(cabinetId);
    }

    /**
     * Génère un ID d'abonnement unique
     */
    private String generateSubscriptionId() {
        return "SUB_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    @Override
    public String toString() {
        return "SubscriptionRequest{" +
                "subscriptionId='" + subscriptionId + '\'' +
                ", secretaireId=" + secretaireId +
                ", cabinetId=" + cabinetId +
                ", eventTypes=" + eventTypes +
                ", subscribeToAll=" + subscribeToAll +
                ", clientInfo='" + clientInfo + '\'' +
                '}';
    }
}
