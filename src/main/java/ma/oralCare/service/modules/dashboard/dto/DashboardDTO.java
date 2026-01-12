package ma.oralCare.service.modules.dashboard.dto;

import ma.oralCare.entities.agenda.RDV;
import java.util.List;
import java.util.ArrayList;

public class DashboardDTO {

    // --- Compteurs (Les petites cartes en haut) ---
    private long totalPatients;
    private int rdvToday;
    private double revenuesToday;
    private String adminName;
    private String medecinNom;

    private List<RDV> prochainsRDV;

    // --- Constructeur ---
    public DashboardDTO() {
        this.prochainsRDV = new ArrayList<>();
    }

    public String getMedecinNom() { return medecinNom; }
    public void setMedecinNom(String medecinNom) { this.medecinNom = medecinNom; }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    // --- Getters et Setters ---

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public int getRdvToday() {
        return rdvToday;
    }

    public void setRdvToday(int rdvToday) {
        this.rdvToday = rdvToday;
    }

    public double getRevenuesToday() {
        return revenuesToday;
    }

    public void setRevenuesToday(double revenuesToday) {
        this.revenuesToday = revenuesToday;
    }

    public List<RDV> getProchainsRDV() {
        return prochainsRDV;
    }

    public void setProchainsRDV(List<RDV> prochainsRDV) {
        this.prochainsRDV = prochainsRDV;
    }

    @Override
    public String toString() {
        return "DashboardDTO{" +
                "totalPatients=" + totalPatients +
                ", rdvToday=" + rdvToday +
                ", revenuesToday=" + revenuesToday +
                ", rdvListSize=" + (prochainsRDV != null ? prochainsRDV.size() : 0) +
                '}';
    }

    public String getDisplayName() {
        if (adminName != null && !adminName.isEmpty()) return adminName;
        if (medecinNom != null && !medecinNom.isEmpty()) return medecinNom;
        return "Utilisateur";
    }
}