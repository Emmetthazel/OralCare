package ma.oralCare.mvc.utils;

/**
 * Utilitaire pour traduire les statuts en français dans les interfaces secrétaire
 */
public class StatutTranslator {
    
    /**
     * Traduit un statut anglais vers français
     * @param statutAnglais Le statut en anglais
     * @return Le statut en français
     */
    public static String traduireStatut(String statutAnglais) {
        if (statutAnglais == null) {
            return "Inconnu";
        }
        
        switch (statutAnglais.toUpperCase()) {
            case "PENDING":
            case "EN ATTENTE":
                return "En attente";
                
            case "CONFIRMED":
            case "CONFIRMÉ":
                return "Confirmé";
                
            case "COMPLETED":
            case "TERMINÉ":
                return "Terminé";
                
            case "CANCELLED":
            case "ANNULÉ":
                return "Annulé";
                
            case "IN_PROGRESS":
            case "EN COURS":
                return "En cours";
                
            case "SCHEDULED":
            case "PLANIFIÉ":
                return "Planifié";
                
            case "WAITING":
            case "ATTENTE":
                return "En attente de traitement";
                
            case "PAID":
            case "PAYÉ":
                return "Payé";
                
            case "UNPAID":
            case "IMPAYÉ":
                return "Impayé";
                
            case "PARTIALLY_PAID":
            case "PARTIELLEMENT_PAYÉ":
                return "Partiellement payé";
                
            case "ACTIVE":
            case "ACTIF":
                return "Actif";
                
            case "INACTIVE":
            case "INACTIF":
                return "Inactif";
                
            case "NEW":
            case "NOUVEAU":
                return "Nouveau";
                
            case "CLOSED":
            case "FERMÉ":
                return "Fermé";
                
            case "OPEN":
            case "OUVERT":
                return "Ouvert";
                
            default:
                // Si le statut est déjà en français ou inconnu, le retourner tel quel
                return statutAnglais;
        }
    }
    
    /**
     * Traduit un statut de rendez-vous
     * @param statut Le statut du rendez-vous
     * @return Le statut traduit en français
     */
    public static String traduireStatutRDV(String statut) {
        return traduireStatut(statut);
    }
    
    /**
     * Traduit un statut de paiement
     * @param statut Le statut de paiement
     * @return Le statut traduit en français
     */
    public static String traduireStatutPaiement(String statut) {
        return traduireStatut(statut);
    }
    
    /**
     * Traduit un statut de dossier
     * @param statut Le statut du dossier
     * @return Le statut traduit en français
     */
    public static String traduireStatutDossier(String statut) {
        return traduireStatut(statut);
    }
}
