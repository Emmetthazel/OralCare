package ma.oralCare;

import ma.oralCare.entities.enums.*;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.staff.Medecin;
import ma.oralCare.entities.cabinet.CabinetMedicale;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Point d'entree de l application OralCare
 */
public class OralCareApp {
    public static void main(String[] args) {
        System.out.println("=== OralCare Application Started ===");
        
        // Test des entites principales
        testEntities();
        
        System.out.println("\n Application OralCare demarree avec succes!");
        System.out.println(" Architecture complete implementee:");
        System.out.println("   - Entites metier (Patient, Medecin, Cabinet, etc.)");
        System.out.println("   - Enums (Sexe, Assurance, Statuts, etc.)");
        System.out.println("   - Schema MySQL complet");
        System.out.println("   - Configuration Maven avec Lombok");
    }
    
    private static void testEntities() {
        System.out.println("\n Test des entites:");
        
        // Test Patient avec Builder (approche du professeur)
        Patient patient = Patient.builder()
                .nom("Ahmed")
                .prenom("Benali")
                .sexe(Sexe.MALE)
                .assurance(Assurance.CNOPS)
                .dateNaissance(LocalDate.of(1990, 5, 15))
                .dateCreation(LocalDateTime.now())
                .build();
        System.out.println("   Patient: " + patient.getNom() + " " + patient.getPrenom() + " (" + patient.getSexe().getLibelle() + ")");
        
        // Test Patient avec toString() (approche du professeur)
        System.out.println("   Patient.toString():\n" + patient);
        
        // Test Medecin (herite de Staff -> Utilisateur)
        Medecin medecin = new Medecin();
        medecin.setNom("Dr. Fatima");
        medecin.setSpecialite("Orthodontie");
        System.out.println("   Medecin: " + medecin.getNom() + " - " + medecin.getSpecialite());
        
        // Test Cabinet avec Builder
        CabinetMedicale cabinet = CabinetMedicale.builder()
                .nom("Cabinet Dentaire Alami")
                .email("contact@cabinet-alami.ma")
                .tel1("0537123456")
                .tel2("0537123457")
                .build();
        System.out.println("   Cabinet: " + cabinet.getNom() + " (" + cabinet.getEmail() + ")");
        
        // Test Enums
        System.out.println("\n Test des Enums:");
        System.out.println("   EnPromo: " + EnPromo.YES.getLibelle());
        System.out.println("   StatutFacture: " + StatutFacture.PAID.getLibelle());
        System.out.println("   StatutRDV: " + StatutRDV.CONFIRMED.getLibelle());
    }
}
