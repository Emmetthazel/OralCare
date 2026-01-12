package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation corrigée utilisant le SessionFactory de manière dynamique.
 * Cette approche garantit que la connexion est toujours valide avant chaque requête.
 */
public class MedicamentRepositoryImpl implements MedicamentRepository {

    // ✅ ÉTAPE 1 : On supprime le champ 'final Connection' pour éviter les références mortes.

    public MedicamentRepositoryImpl() {
        // Constructeur vide : le repository est désormais autonome.
    }

    // --- Requêtes SQL ---
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";

    private static final String SELECT_BASE_FIELDS =
            " m.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Medicament m JOIN BaseEntity b ON m.id_entite = b.id_entite ";

    private static final String FIND_ALL_SQL = "SELECT " + SELECT_BASE_FIELDS;
    private static final String CREATE_SQL = "INSERT INTO Medicament (id_entite, nom, laboratoire, type, forme, remboursable, prix_unitaire, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Medicament SET nom = ?, laboratoire = ?, type = ?, forme = ?, remboursable = ?, prix_unitaire = ?, description = ? WHERE id_entite = ?";
    private static final String DELETE_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // --- Méthode d'exécution générique ---
    private List<Medicament> executeFindQuery(String sql, Object... params) {
        List<Medicament> medicaments = new ArrayList<>();
        try {
            // ✅ ÉTAPE 2 : On demande une connexion fraîche au SessionFactory
            Connection conn = SessionFactory.getInstance().getConnection();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        medicaments.add(RowMappers.mapMedicament(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB-ERROR] MedicamentRepository: " + e.getMessage());
            throw new RuntimeException("Erreur de lecture Médicament", e);
        }
        return medicaments;
    }

    // --- Opérations d'écriture (CRUD) ---

    @Override
    public void create(Medicament entity) {
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            entity.setDateCreation(LocalDateTime.now());

            // 1. Créer la BaseEntity
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_BASE_ENTITY_SQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setTimestamp(1, Timestamp.valueOf(entity.getDateCreation()));
                stmt.setObject(2, entity.getCreePar());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) entity.setIdEntite(rs.getLong(1));
                }
            }

            // 2. Créer le Médicament
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setString(2, entity.getNom());
                stmt.setString(3, entity.getLaboratoire());
                stmt.setString(4, entity.getType());
                stmt.setString(5, entity.getForme().name());
                stmt.setBoolean(6, entity.getRemboursable());
                stmt.setBigDecimal(7, entity.getPrixUnitaire());
                stmt.setString(8, entity.getDescription());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du médicament.", e);
        }
    }

    @Override
    public void update(Medicament entity) {
        try {
            Connection conn = SessionFactory.getInstance().getConnection();

            // 1. Update Médicament
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setString(1, entity.getNom());
                stmt.setString(2, entity.getLaboratoire());
                stmt.setString(3, entity.getType());
                stmt.setString(4, entity.getForme().name());
                stmt.setBoolean(5, entity.getRemboursable());
                stmt.setBigDecimal(6, entity.getPrixUnitaire());
                stmt.setString(7, entity.getDescription());
                stmt.setLong(8, entity.getIdEntite());
                stmt.executeUpdate();
            }

            // 2. Update BaseEntity
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_BASE_ENTITY_SQL)) {
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setObject(2, entity.getModifiePar());
                stmt.setLong(3, entity.getIdEntite());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du médicament.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            Connection conn = SessionFactory.getInstance().getConnection();
            // On supprime la BaseEntity, la cascade SQL s'occupe du reste (ou delete manuel)
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du médicament.", e);
        }
    }

    // --- Recherches ---

    @Override
    public List<Medicament> findAll() {
        return executeFindQuery(FIND_ALL_SQL + " ORDER BY m.nom ASC");
    }

    @Override
    public Optional<Medicament> findById(Long id) {
        return executeFindQuery("SELECT " + SELECT_BASE_FIELDS + " WHERE m.id_entite = ?", id)
                .stream().findFirst();
    }

    @Override
    public List<Medicament> findByNomContaining(String nom) {
        return executeFindQuery("SELECT " + SELECT_BASE_FIELDS + " WHERE m.nom LIKE ?", "%" + nom + "%");
    }

    @Override
    public void save(Medicament m) {
        if (m.getIdEntite() == null) create(m); else update(m);
    }

    @Override
    public void delete(Medicament m) {
        if (m != null) deleteById(m.getIdEntite());
    }

    @Override
    public List<Medicament> findByLaboratoire(String lab) {
        return executeFindQuery("SELECT " + SELECT_BASE_FIELDS + " WHERE m.laboratoire = ?", lab);
    }

    @Override
    public List<Medicament> findByForme(FormeMedicament f) {
        return executeFindQuery("SELECT " + SELECT_BASE_FIELDS + " WHERE m.forme = ?", f.name());
    }

    @Override
    public List<Medicament> findByRemboursable(Boolean r) {
        return executeFindQuery("SELECT " + SELECT_BASE_FIELDS + " WHERE m.remboursable = ?", r);
    }
}