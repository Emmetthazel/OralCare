package ma.oralCare.service.modules.cabinet.impl;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.oralCare.service.modules.cabinet.api.CabinetManagementService;

import java.util.List;
import java.util.Optional;

public class CabinetManagementServiceImpl implements CabinetManagementService {

    private final CabinetMedicaleRepository cabinetRepository;

    public CabinetManagementServiceImpl(CabinetMedicaleRepository cabinetRepository) {
        this.cabinetRepository = cabinetRepository;
    }

    @Override
    public void createCabinet(CabinetMedicale cabinet) throws Exception {
        try {
            // Le Repository gère déjà la transaction (BaseEntity + cabinet_medicale)
            // et l'ouverture/fermeture de la connexion via SessionFactory.
            cabinetRepository.create(cabinet);
        } catch (Exception e) {
            // On encapsule l'erreur SQL dans une exception métier pour la vue
            throw new Exception("Impossible de créer le cabinet : " + e.getMessage());
        }
    }

    @Override
    public List<CabinetMedicale> getAllCabinets() {
        return cabinetRepository.findAll();
    }

    @Override
    public Optional<CabinetMedicale> getCabinetById(Long id) {
        return cabinetRepository.findById(id);
    }

    @Override
    public void updateCabinet(CabinetMedicale cabinet) throws Exception {
        try {
            cabinetRepository.update(cabinet);
        } catch (Exception e) {
            throw new Exception("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void deleteCabinet(Long id) throws Exception {
        try {
            cabinetRepository.deleteById(id);
        } catch (Exception e) {
            throw new Exception("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}