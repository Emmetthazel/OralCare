package ma.oralCare.service.modules.users.dto;

import java.time.LocalDate;
import ma.oralCare.entities.enums.Sexe;

public record UpdateUserProfileRequest(
        Long id,
        String nom,
        String email,
        String adresse,
        String tel,
        Sexe sexe,
        LocalDate dateNaissance
) {}
