package ma.oralCare.service.modules.auth.dto;

import ma.oralCare.entities.enums.RoleLibelle;

import java.util.Set;

/**
 * Représente l'utilisateur authentifié dans la couche service.
 */
public record UserPrincipal(
        Long id,
        String login,
        String nom,
        Set<RoleLibelle> roles,
        Set<String> privileges
) {}


