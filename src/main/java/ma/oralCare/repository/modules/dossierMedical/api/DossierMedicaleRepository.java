package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossier.DossierMedicale;
import java.util.List;

public interface DossierMedicaleRepository {

    DossierMedicale save(DossierMedicale dossierMedicale);

    DossierMedicale update(DossierMedicale dossierMedicale);

    boolean deleteById(Long id);

    DossierMedicale findById(Long id);

    List<DossierMedicale> findAll();

    List<DossierMedicale> findByPatientId(Long patientId);
}
