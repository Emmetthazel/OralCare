package ma.oralCare.repository.common;

import ma.oralCare.entities.enums.*;
import ma.oralCare.entities.base.*;
import ma.oralCare.entities.patient.*;
import ma.oralCare.entities.dossierMedical.*;
import ma.oralCare.entities.users.*;
import ma.oralCare.entities.cabinet.*;
import ma.oralCare.entities.agenda.*;

import java.sql.*;

public final class RowMappers {

    private RowMappers(){}

    private static Ordonnance createOrdonnanceRef(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        Ordonnance ref = new Ordonnance();
        ref.setIdEntite(id);
        return ref;
    }

    private static Medicament createMedicamentRef(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        Medicament ref = new Medicament();
        ref.setIdEntite(id);
        return ref;
    }
    private static DossierMedicale createDossierRef(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        DossierMedicale ref = new DossierMedicale();
        ref.setIdEntite(id);
        return ref;
    }

    private static Consultation createConsultationRef(Long id) {
        // NOTE: rs.getLong() retourne 0 si la colonne SQL est NULL.
        // C'est pourquoi on vérifie si l'ID est <= 0.
        if (id == null || id <= 0) {
            return null;
        }
        Consultation ref = new Consultation();
        ref.setIdEntite(id);
        return ref;
    }

    private static Patient createPatientRef(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        Patient ref = new Patient();
        ref.setIdEntite(id);
        return ref;
    }

    private static Medecin createMedecinRef(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        Medecin ref = new Medecin();
        ref.setIdEntite(id);
        return ref;
    }
    private static SituationFinanciere createSituationFinanciereRef(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        SituationFinanciere ref = new SituationFinanciere();
        ref.setIdEntite(id);
        return ref;
    }
    private static CabinetMedicale createCabinetRef(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        CabinetMedicale ref = new CabinetMedicale();
        ref.setIdEntite(id);
        return ref;
    }

    private static Acte createActeRef(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        Acte ref = new Acte();
        ref.setIdEntite(id);
        return ref;
    }

    private static void mapBaseEntityFields(ResultSet rs, BaseEntity entity) throws SQLException {
        entity.setIdEntite(rs.getLong("id_entite"));
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        entity.setDateCreation(dateCreation != null ? dateCreation.toLocalDateTime() : null);
        Timestamp dateModification = rs.getTimestamp("date_derniere_modification");
        entity.setDateDerniereModification(dateModification != null ? dateModification.toLocalDateTime() : null);

        long creeParValue = rs.getLong("cree_par");
        if (rs.wasNull()) {
            entity.setCreePar(null);
        } else {
            entity.setCreePar(creeParValue);
        }

        long modifieParValue = rs.getLong("modifie_par");
        if (rs.wasNull()) {
            entity.setModifiePar(null); // Si NULL en DB, assigner NULL à l'objet Java (évite le 0)
        } else {
            entity.setModifiePar(modifieParValue);
        }
    }

    public static Antecedent mapAntecedent(ResultSet rs) throws SQLException {
        Antecedent a = new Antecedent();
        mapBaseEntityFields(rs, a);

        a.setNom(rs.getString("nom"));
        String categorieStr = rs.getString("categorie");
        if (categorieStr != null) {
            a.setCategorie(CategorieAntecedent.valueOf(categorieStr));
        } else {
            a.setCategorie(null);
        }
        a.setNiveauDeRisque(NiveauDeRisque.valueOf(rs.getString("niveau_de_risque")));
        return a;
    }

    public static Adresse mapAdresse(ResultSet rs) throws SQLException {
        Adresse a = new Adresse();

        // Mappage des champs de l'Adresse
        a.setNumero(rs.getString("numero"));
        a.setRue(rs.getString("rue"));
        a.setCodePostal(rs.getString("code_postal"));
        a.setVille(rs.getString("ville"));
        a.setPays(rs.getString("pays"));

        // Le champ 'complement' peut être NULL en BD
        a.setComplement(rs.getString("complement"));

        return a;
    }

    public static CabinetMedicale mapCabinetMedicale(ResultSet rs) throws SQLException {
        CabinetMedicale cm = new CabinetMedicale();
        mapBaseEntityFields(rs, cm);
        cm.setNom(rs.getString("nom"));
        cm.setEmail(rs.getString("email"));
        cm.setLogo(rs.getString("logo"));
        cm.setAdresse(mapAdresse(rs));
        cm.setCin(rs.getString("cin"));
        cm.setTel1(rs.getString("tel1"));
        cm.setTel2(rs.getString("tel2"));
        cm.setSiteWeb(rs.getString("siteWeb"));
        cm.setInstagram(rs.getString("instagram"));
        cm.setFacebook(rs.getString("facebook"));
        cm.setDescription(rs.getString("description"));
        return cm;
    }

    public static Patient mapPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();

        mapBaseEntityFields(rs, p);

        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        Date sqlDateNaissance = rs.getDate("date_de_naissance");
        if (sqlDateNaissance != null) {
            p.setDateDeNaissance(sqlDateNaissance.toLocalDate());
        } else {
            p.setDateDeNaissance(null);
        }
        p.setEmail(rs.getString("email"));
        p.setSexe(Sexe.valueOf(rs.getString("sexe")));
        p.setAdresse(rs.getString("adresse"));
        p.setTelephone(rs.getString("telephone"));
        p.setAssurance(Assurance.valueOf(rs.getString("assurance")));

        return p;
    }

    public static Medicament mapMedicament(ResultSet rs) throws SQLException {
        Medicament m = new Medicament();

        mapBaseEntityFields(rs, m);

        m.setNom(rs.getString("nom"));
        m.setLaboratoire(rs.getString("laboratoire"));
        m.setType(rs.getString("type"));
        m.setForme(FormeMedicament.valueOf(rs.getString("forme")));
        m.setRemboursable(rs.getBoolean("remboursable"));
        m.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));

        m.setDescription(rs.getString("description"));

        return m;
    }

    public static Acte mapActe(ResultSet rs) throws SQLException {
        Acte a = new Acte();

        mapBaseEntityFields(rs, a);

        a.setLibelle(rs.getString("libelle"));
        a.setCategorie(rs.getString("categorie"));
        a.setPrixDeBase(rs.getBigDecimal("prix_de_base"));

        return a;
    }

    public static Role mapRole(ResultSet rs) throws SQLException {
        Role r = new Role();

        mapBaseEntityFields(rs, r);

        r.setLibelle(RoleLibelle.valueOf(rs.getString("libelle")));

        return r;
    }

    public static void mapUtilisateurFields(ResultSet rs, Utilisateur u) throws SQLException {
        // 1. Mappage des champs hérités de BaseEntity
        mapBaseEntityFields(rs, u);

        // 2. Mappage des champs spécifiques à Utilisateur
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setCin(rs.getString("cin"));
        u.setTel(rs.getString("tel"));
        u.setLogin(rs.getString("login"));
        u.setMotDePass(rs.getString("mot_de_pass"));

        String sexeStr = rs.getString("sexe");
        if (sexeStr != null) {
            u.setSexe(Sexe.valueOf(sexeStr));
        } else {
            u.setSexe(null);
        }
        Date sqlDateNaissance = rs.getDate("date_naissance");
        if (sqlDateNaissance != null) {
            u.setDateNaissance(sqlDateNaissance.toLocalDate());
        }

        Date sqlLastLogin = rs.getDate("last_login_date");
        if (sqlLastLogin != null) {
            u.setLastLoginDate(sqlLastLogin.toLocalDate());
        }
        u.setAdresse(mapAdresse(rs));
    }

    public static Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        mapUtilisateurFields(rs, u);
        return u;
    }

    public static void mapStaffFields(ResultSet rs, Staff s) throws SQLException {

        mapUtilisateurFields(rs, s);

        s.setSalaire(rs.getBigDecimal("salaire"));
        s.setPrime(rs.getBigDecimal("prime"));

        Date sqlDateRecrutement = rs.getDate("date_recrutement");
        s.setDateRecrutement(sqlDateRecrutement != null ? sqlDateRecrutement.toLocalDate() : null);

        int soldeConge = rs.getInt("solde_conge");
        s.setSoldeConge(rs.wasNull() ? null : soldeConge);

        // RÉFACTORISATION : CabinetMedicale
        s.setCabinetMedicale(createCabinetRef(rs.getLong("cabinet_id")));
    }

    public static Staff mapStaff(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        mapStaffFields(rs, s);
        return s;
    }

    public static Secretaire mapSecretaire(ResultSet rs) throws SQLException {
        Secretaire s = new Secretaire();

         mapStaffFields(rs, s);

        s.setNumCNSS(rs.getString("num_cnss"));
        s.setCommission(rs.getBigDecimal("commission"));

        return s;
    }

    public static Medecin mapMedecin(ResultSet rs) throws SQLException {
        Medecin m = new Medecin();

        mapStaffFields(rs, m);

        m.setSpecialite(rs.getString("specialite"));

        return m;
    }

    public static Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin a = new Admin();

        mapUtilisateurFields(rs, a);

        return a;
    }

    public static SituationFinanciere mapSituationFinanciere(ResultSet rs) throws SQLException {
        SituationFinanciere sf = new SituationFinanciere();
        mapBaseEntityFields(rs, sf);

        sf.setTotaleDesActes(rs.getBigDecimal("totale_des_actes"));
        sf.setTotalePaye(rs.getBigDecimal("totale_paye"));
        sf.setCredit(rs.getBigDecimal("credit"));

        String statutStr = rs.getString("statut");
        sf.setStatut(statutStr != null ? StatutSituationFinanciere.valueOf(statutStr) : null);

        String enPromoStr = rs.getString("en_promo");
        sf.setEnPromo(enPromoStr != null ? EnPromo.valueOf(enPromoStr) : null);

        // RÉFACTORISATION : DossierMedicale
        sf.setDossierMedicale(createDossierRef(rs.getLong("dossier_medicale_id")));

        return sf;
    }

    public static DossierMedicale mapDossierMedicale(ResultSet rs) throws SQLException {
        DossierMedicale dm = new DossierMedicale();
        mapBaseEntityFields(rs, dm);

        // RÉFACTORISATION : Patient (NOT NULL)
        dm.setPatient(createPatientRef(rs.getLong("patient_id")));

        // RÉFACTORISATION : Medecin (NULLABLE)
        dm.setMedecin(createMedecinRef(rs.getLong("medecin_id")));

        return dm;
    }

    public static Consultation mapConsultation(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        mapBaseEntityFields(rs, c);

        Date sqlDate = rs.getDate("date");
        c.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);

        String statutStr = rs.getString("statut");
        c.setStatut(statutStr != null ? StatutConsultation.valueOf(statutStr) : null);

        c.setObservationMedecin(rs.getString("observation_medecin"));

        // RÉFACTORISATION : DossierMedicale
        c.setDossierMedicale(createDossierRef(rs.getLong("dossier_medicale_id")));

        return c;
    }

    public static Facture mapFacture(ResultSet rs) throws SQLException {
        Facture f = new Facture();
        mapBaseEntityFields(rs, f);

        f.setTotaleFacture(rs.getBigDecimal("totale_facture"));
        f.setTotalePaye(rs.getBigDecimal("totale_paye"));
        f.setReste(rs.getBigDecimal("reste"));

        String statutStr = rs.getString("statut");
        f.setStatut(statutStr != null ? StatutFacture.valueOf(statutStr) : null);

        Timestamp sqlDateFacture = rs.getTimestamp("date_facture");
        f.setDateFacture(sqlDateFacture != null ? sqlDateFacture.toLocalDateTime() : null);

        // RÉFACTORISATION : Consultation
        f.setConsultation(createConsultationRef(rs.getLong("consultation_id")));

        // RÉFACTORISATION : SituationFinanciere
        f.setSituationFinanciere(createSituationFinanciereRef(rs.getLong("situation_financiere_id")));

        return f;
    }

    public static Certificat mapCertificat(ResultSet rs) throws SQLException {
        Certificat certif = new Certificat();
        mapBaseEntityFields(rs, certif);

        Date sqlDateDebut = rs.getDate("date_debut");
        certif.setDateDebut(sqlDateDebut != null ? sqlDateDebut.toLocalDate() : null);

        Date sqlDateFin = rs.getDate("date_fin");
        certif.setDateFin(sqlDateFin != null ? sqlDateFin.toLocalDate() : null);

        certif.setDuree(rs.getInt("duree"));
        certif.setNoteMedecin(rs.getString("note_medecin"));

        certif.setConsultation(createConsultationRef(rs.getLong("consultation_id")));

        return certif;
    }

    public static RDV mapRDV(ResultSet rs) throws SQLException {
        RDV rdv = new RDV();

        mapBaseEntityFields(rs, rdv);

        Date sqlDate = rs.getDate("date");
        rdv.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);
        Time sqlTime = rs.getTime("heure");
        rdv.setHeure(sqlTime != null ? sqlTime.toLocalTime() : null);
        rdv.setMotif(rs.getString("motif"));
        String statutStr = rs.getString("statut");
        rdv.setStatut(statutStr != null ? StatutRDV.valueOf(statutStr) : null);
        rdv.setNoteMedecin(rs.getString("note_medecin"));
        rdv.setDossierMedicale(createDossierRef(rs.getLong("dossier_medicale_id")));
        rdv.setConsultation(createConsultationRef(rs.getLong("consultation_id")));

        return rdv;
    }

    public static Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        Ordonnance o = new Ordonnance();

        mapBaseEntityFields(rs, o);

        Date sqlDate = rs.getDate("date_ordonnance");
        o.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);
        o.setDossierMedicale(createDossierRef(rs.getLong("dossier_medicale_id")));
        o.setConsultation(createConsultationRef(rs.getLong("consultation_id")));

        return o;
    }

    public static Prescription mapPrescription(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();

        mapBaseEntityFields(rs, p);

        p.setQuantite(rs.getInt("quantite"));
        p.setFrequence(rs.getString("frequence"));
        int duree = rs.getInt("duree_en_jours");
        p.setDureeEnJours(rs.wasNull() ? 0 : duree);
        p.setOrdonnance(createOrdonnanceRef(rs.getLong("ordonnance_id")));
        p.setMedicament(createMedicamentRef(rs.getLong("medicament_id")));

        return p;
    }

    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        AgendaMensuel am = new AgendaMensuel();

        mapBaseEntityFields(rs, am);

        String moisStr = rs.getString("mois");
        am.setMois(moisStr != null ? Mois.valueOf(moisStr) : null);
        am.setAnnee(rs.getInt("annee"));
        am.setMedecin(createMedecinRef(rs.getLong("medecin_id")));

        return am;
    }

    public static Notification mapNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();

        mapBaseEntityFields(rs, n);

        String titreStr = rs.getString("titre");
        if (titreStr != null) {
            n.setTitre(NotificationTitre.valueOf(titreStr));
        }
        n.setMessage(rs.getString("message"));
        Date sqlDate = rs.getDate("date");
        n.setDate(sqlDate != null ? sqlDate.toLocalDate() : null);
        Time sqlTime = rs.getTime("time");
        n.setTime(sqlTime != null ? sqlTime.toLocalTime() : null);
        String typeStr = rs.getString("type");
        if (typeStr != null) {
            n.setType(NotificationType.valueOf(typeStr));
        }
        String prioriteStr = rs.getString("priorite");
        if (prioriteStr != null) {
            n.setPriorite(NotificationPriorite.valueOf(prioriteStr));
        }

        return n;
    }

    public static Revenues mapRevenues(ResultSet rs) throws SQLException {
        Revenues r = new Revenues();

        mapBaseEntityFields(rs, r);

        r.setTitre(rs.getString("titre"));
        r.setDescription(rs.getString("description"));
        r.setMontant(rs.getBigDecimal("montant"));
        Timestamp sqlDate = rs.getTimestamp("date");
        r.setDate(sqlDate != null ? sqlDate.toLocalDateTime() : null);
        r.setCabinetMedicale(createCabinetRef(rs.getLong("cabinet_medicale_id")));

        return r;
    }

    public static Statistiques mapStatistiques(ResultSet rs) throws SQLException {
        Statistiques s = new Statistiques();

        mapBaseEntityFields(rs, s);

        s.setNom(rs.getString("nom"));
        String categorieStr = rs.getString("categorie");
        if (categorieStr != null) {
            s.setCategorie(StatistiqueCategorie.valueOf(categorieStr));
        }
        s.setChiffre(rs.getBigDecimal("chiffre"));
        Date sqlDate = rs.getDate("date_calcul");
        s.setDateCalcul(sqlDate != null ? sqlDate.toLocalDate() : null);
        s.setCabinetMedicale(createCabinetRef(rs.getLong("cabinet_medicale_id")));

        return s;
    }

    public static Charges mapCharges(ResultSet rs) throws SQLException {
        Charges c = new Charges();

        mapBaseEntityFields(rs, c);

        c.setTitre(rs.getString("titre"));
        c.setDescription(rs.getString("description"));
        c.setMontant(rs.getBigDecimal("montant"));
        Timestamp sqlDate = rs.getTimestamp("date");
        c.setDate(sqlDate != null ? sqlDate.toLocalDateTime() : null);
        Long cabinetId = rs.getLong("cabinet_id");
        if (!rs.wasNull()) {
            c.setCabinetMedicale(createCabinetRef(cabinetId));
        } else {
            c.setCabinetMedicale(null);
        }

        return c;
    }

    public static InterventionMedecin mapInterventionMedecin(ResultSet rs) throws SQLException {
        InterventionMedecin i = new InterventionMedecin();

        mapBaseEntityFields(rs, i);

        i.setPrixDePatient(rs.getBigDecimal("prix_de_patient"));
        int numDent = rs.getInt("num_dent");
        i.setNumDent(rs.wasNull() ? null : numDent);
        Long consultationId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            i.setConsultation(createConsultationRef(consultationId));
        }
        Long acteId = rs.getLong("acte_id");
        if (!rs.wasNull()) {
            i.setActe(createActeRef(acteId));
        }

        return i;
    }


}