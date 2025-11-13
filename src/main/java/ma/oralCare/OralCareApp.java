package ma.oralCare;

import ma.oralCare.entities.*;

/**
 * Point d'entrée de l'application OralCare
 */
public class OralCareApp {
    public static void main(String[] args) {
        System.out.println("=== 🦷 OralCare Application Started ===");
        
        // Test des entités principales
        testEntities();
        
        System.out.println("\n Application OralCare démarrée avec succès!");
        System.out.println(" Architecture complète implémentée:");
        System.out.println("   - Entités métier (Patient, Médecin, Cabinet, etc.)");
        System.out.println("   - Enums (Sexe, Assurance, Statuts, etc.)");
        System.out.println("   - Schéma MySQL complet");
        System.out.println("   - Configuration Maven avec Lombok");
    }
    
    private static void testEntities() {
        System.out.println("\n Test des entités:");
        
        // Test Patient
        Patient patient = new Patient();
        patient.setNom("Ahmed Benali");
        patient.setSexe(Sexe.MALE);
        patient.setAssurance(Assurance.CNOPS);
        System.out.println("   ✓ Patient: " + patient.getNom() + " (" + patient.getSexe().getLibelle() + ")");
        
        // Test Médecin
        Médecin medecin = new Médecin();
        medecin.setNom("Dr. Fatima Alami");
        medecin.setSpécialité("Orthodontie");
        System.out.println("   ✓ Médecin: " + medecin.getNom() + " - " + medecin.getSpécialité());
        
        // Test Cabinet
        CabinetMédicale cabinet = new CabinetMédicale();
        cabinet.setNom("Cabinet Dentaire Alami");
        cabinet.setEmail("contact@cabinet-alami.ma");
        System.out.println("   ✓ Cabinet: " + cabinet.getNom());
        
        // Test Enums
        System.out.println("   ✓ EnPromo: " + EnPromo.YES.getLibelle());
        System.out.println("   ✓ StatutFacture: " + StatutFacture.PAID.getLibelle());
        System.out.println("   ✓ StatutRDV: " + StatutRDV.CONFIRMED.getLibelle());
    }
}
