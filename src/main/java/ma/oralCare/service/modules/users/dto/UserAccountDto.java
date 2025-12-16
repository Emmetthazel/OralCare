package ma.oralCare.service.modules.users.dto;

import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.enums.Sexe;

import java.time.LocalDate;
import java.util.Set;

public record UserAccountDto(
        Long id,
        String nom,
        String email,
        String login,
        Sexe sexe,
        LocalDate dateNaissance,
        Set<RoleLibelle> roles,
        Set<String> privileges
) {}

