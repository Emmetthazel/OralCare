package ma.oralCare.service.modules.realtime.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class RealTimeEvent {
    private String eventId;
    private String eventType;
    private String source;
    private Long targetUserId;
    private Long targetCabinetId;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
    private String message;
    private int priority; // 1=low, 2=medium, 3=high

    public RealTimeEvent() {
        this.timestamp = LocalDateTime.now();
        this.priority = 2; // Medium par défaut
    }

    public RealTimeEvent(String eventType, String source, Map<String, Object> data) {
        this();
        this.eventType = eventType;
        this.source = source;
        this.data = data;
        this.eventId = generateEventId();
    }

    // Types d'événements prédéfinis
    public static final String TYPE_NEW_RDV = "NEW_RDV";
    public static final String TYPE_RDV_CANCELLED = "RDV_CANCELLED";
    public static final String TYPE_RDV_MODIFIED = "RDV_MODIFIED";
    public static final String TYPE_NEW_PATIENT = "NEW_PATIENT";
    public static final String TYPE_PATIENT_UPDATED = "PATIENT_UPDATED";
    public static final String TYPE_NEW_CONSULTATION = "NEW_CONSULTATION";
    public static final String TYPE_CONSULTATION_UPDATED = "CONSULTATION_UPDATED";
    public static final String TYPE_NEW_FACTURE = "NEW_FACTURE";
    public static final String TYPE_FACTURE_PAID = "FACTURE_PAID";
    public static final String TYPE_NEW_CERTIFICAT = "NEW_CERTIFICAT";
    public static final String TYPE_AGENDA_UPDATED = "AGENDA_UPDATED";
    public static final String TYPE_SYSTEM_NOTIFICATION = "SYSTEM_NOTIFICATION";

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Long getTargetCabinetId() {
        return targetCabinetId;
    }

    public void setTargetCabinetId(Long targetCabinetId) {
        this.targetCabinetId = targetCabinetId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Génère un ID d'événement unique
     */
    private String generateEventId() {
        return "EVT_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    /**
     * Crée un événement pour un nouveau rendez-vous
     */
    public static RealTimeEvent newRdvEvent(Long secretaireId, Long cabinetId, String patientNom, String rdvDate) {
        RealTimeEvent event = new RealTimeEvent(TYPE_NEW_RDV, "RDV_SERVICE", Map.of(
            "patientNom", patientNom,
            "rdvDate", rdvDate,
            "action", "Nouveau rendez-vous"
        ));
        event.setTargetUserId(secretaireId);
        event.setTargetCabinetId(cabinetId);
        event.setMessage("Nouveau rendez-vous pour " + patientNom + " le " + rdvDate);
        event.setPriority(2);
        return event;
    }

    /**
     * Crée un événement pour un nouveau patient
     */
    public static RealTimeEvent newPatientEvent(Long secretaireId, Long cabinetId, String patientNom) {
        RealTimeEvent event = new RealTimeEvent(TYPE_NEW_PATIENT, "PATIENT_SERVICE", Map.of(
            "patientNom", patientNom,
            "action", "Nouveau patient"
        ));
        event.setTargetUserId(secretaireId);
        event.setTargetCabinetId(cabinetId);
        event.setMessage("Nouveau patient enregistré: " + patientNom);
        event.setPriority(2);
        return event;
    }

    /**
     * Crée un événement pour une nouvelle facture
     */
    public static RealTimeEvent newFactureEvent(Long secretaireId, Long cabinetId, String patientNom, double montant) {
        RealTimeEvent event = new RealTimeEvent(TYPE_NEW_FACTURE, "FACTURE_SERVICE", Map.of(
            "patientNom", patientNom,
            "montant", montant,
            "action", "Nouvelle facture"
        ));
        event.setTargetUserId(secretaireId);
        event.setTargetCabinetId(cabinetId);
        event.setMessage("Nouvelle facture générée pour " + patientNom + " - Montant: " + montant + " DH");
        event.setPriority(3);
        return event;
    }

    /**
     * Crée un événement de notification système
     */
    public static RealTimeEvent systemNotificationEvent(Long secretaireId, String message) {
        RealTimeEvent event = new RealTimeEvent(TYPE_SYSTEM_NOTIFICATION, "SYSTEM", Map.of(
            "action", "Notification système"
        ));
        event.setTargetUserId(secretaireId);
        event.setMessage(message);
        event.setPriority(1);
        return event;
    }

    @Override
    public String toString() {
        return "RealTimeEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", source='" + source + '\'' +
                ", targetUserId=" + targetUserId +
                ", targetCabinetId=" + targetCabinetId +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", priority=" + priority +
                '}';
    }
}
