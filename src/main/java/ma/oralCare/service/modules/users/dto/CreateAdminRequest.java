package ma.oralCare.service.modules.users.dto;

import java.time.LocalDate;
import ma.oralCare.entities.enums.Sexe;

public record CreateAdminRequest(
        String nom,
        String email,
        String adresse,
        String cin,
        String tel,
        Sexe sexe,
        String login,
        String motDePasse,
        LocalDate dateNaissance,
        Double salaire,
        Double prime,
        LocalDate dateRecrutement,
        Integer soldeConge
) {}

