package ma.oralCare.mvc.controllers.admin.dto;

import java.util.List;

public class AdminDashboardDTO {
    private String adminName;
    private String adminGender;
    private long todayLogsCount;
    private double databaseSize;
    private long totalCabinets;
    private long activeCabinets;
    private List<String> latestActions;
    private String systemStatus;

    public AdminDashboardDTO() {}

    // Getters et Setters
    public long getTodayLogsCount() { return todayLogsCount; }
    public void setTodayLogsCount(long todayLogsCount) { this.todayLogsCount = todayLogsCount; }
    public double getDatabaseSize() { return databaseSize; }
    public void setDatabaseSize(double databaseSize) { this.databaseSize = databaseSize; }
    public long getTotalCabinets() { return totalCabinets; }
    public void setTotalCabinets(long totalCabinets) { this.totalCabinets = totalCabinets; }
    public long getActiveCabinets() { return activeCabinets; }
    public void setActiveCabinets(long activeCabinets) { this.activeCabinets = activeCabinets; }
    public List<String> getLatestActions() { return latestActions; }
    public void setLatestActions(List<String> latestActions) { this.latestActions = latestActions; }
    public String getSystemStatus() { return systemStatus; }
    public void setSystemStatus(String systemStatus) { this.systemStatus = systemStatus; }
    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
    public String getAdminGender() { return adminGender; }
    public void setAdminGender(String adminGender) { this.adminGender = adminGender; }

    public String getFormattedActiveCabinets() { return String.format("%02d", activeCabinets); }
    public String getFormattedDatabaseSize() { return String.format("%.2f MB", databaseSize); }
}