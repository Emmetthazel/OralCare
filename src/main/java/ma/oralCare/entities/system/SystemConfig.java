package ma.oralCare.entities.system;

import java.time.LocalDateTime;

public class SystemConfig {
    private String configKey;
    private String configValue;
    private String statut;
    private LocalDateTime derniereMaj;
    private String description;

    // Constructeurs
    public SystemConfig() {}

    // Getters et Setters
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDerniereMaj() { return derniereMaj; }
    public void setDerniereMaj(LocalDateTime derniereMaj) { this.derniereMaj = derniereMaj; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}