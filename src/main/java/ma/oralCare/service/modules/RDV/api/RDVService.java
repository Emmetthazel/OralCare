package ma.oralCare.service.modules.RDV.api;

import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;
import java.time.LocalDate;
import java.util.List;

public interface RDVService {
    List<RDVPanelDTO> chargerPlanning(LocalDate date, Long medecinId);

    void demarrerSeance(Long rdvId);

    void annulerRendezVous(Long rdvId);
}