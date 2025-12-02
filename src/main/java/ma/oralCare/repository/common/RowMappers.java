package ma.oralCare.repository.common;

import ma.oralCare.entities.acte.Acte;
import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.entities.common.Adresse;
import ma.oralCare.entities.consultation.Certificat;
import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.consultation.InterventionMedecin;
import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.enums.Assurance;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.EnPromo;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.enums.NotificationPriorite;
import ma.oralCare.entities.enums.NotificationTitre;
import ma.oralCare.entities.enums.NotificationType;
import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.enums.StatistiqueCategorie;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.enums.StatutFacture;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.entities.enums.StatutSituationFinanciere;
import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.facture.SituationFinanciere;
import ma.oralCare.entities.medicament.Medicament;
import ma.oralCare.entities.medicament.Prescription;
import ma.oralCare.entities.notification.Notification;
import ma.oralCare.entities.notification.Role;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.entities.staff.Admin;
import ma.oralCare.entities.staff.Medecin;
import ma.oralCare.entities.staff.Secretaire;
import ma.oralCare.entities.staff.Staff;
import ma.oralCare.entities.staff.Utilisateur;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public final class RowMappers {

    private RowMappers() {
    }

    public static Patient mapPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getLong("id"));
        patient.setNom(rs.getString("nom"));
        patient.setPrenom(rs.getString("prenom"));
        patient.setAdresse(rs.getString("adresse"));
        patient.setTelephone(rs.getString("telephone"));
        patient.setEmail(rs.getString("email"));

        Date dn = rs.getDate("dateNaissance");
        if (dn != null) {
            patient.setDateNaissance(dn.toLocalDate());
        }

        Timestamp dc = rs.getTimestamp("dateCreation");
        if (dc != null) {
            patient.setDateCreation(dc.toLocalDateTime());
        }

        String sexeValue = rs.getString("sexe");
        if (sexeValue != null) {
            patient.setSexe(Sexe.valueOf(sexeValue));
        }

        String assuranceValue = rs.getString("assurance");
        if (assuranceValue != null) {
            patient.setAssurance(Assurance.valueOf(assuranceValue));
        }

        patient.setAntecedents(null);
        return patient;
    }

    public static Antecedent mapAntecedent(ResultSet rs) throws SQLException {
        Antecedent antecedent = new Antecedent();
        antecedent.setId(rs.getLong("id"));
        antecedent.setNom(rs.getString("nom"));

        String catValue = rs.getString("categorie");
        if (catValue != null) {
            antecedent.setCategorie(CategorieAntecedent.valueOf(catValue));
        }

        String risqueValue = rs.getString("niveauRisque");
        if (risqueValue != null) {
            antecedent.setNiveauRisque(NiveauDeRisque.valueOf(risqueValue));
        }

        antecedent.setPatients(null);
        return antecedent;
    }

    public static Acte mapActe(ResultSet rs) throws SQLException {
        Acte acte = new Acte();
        acte.setId(rs.getLong("id"));
        acte.setLibelle(rs.getString("libelle"));
        acte.setCategorie(rs.getString("categorie"));
        acte.setPrixDeBase(rs.getDouble("prixDeBase"));
        acte.setInterventionsMedecin(null);
        return acte;
    }

    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        AgendaMensuel agenda = new AgendaMensuel();
        agenda.setId(rs.getLong("id"));

        String moisValue = rs.getString("mois");
        if (moisValue != null) {
            agenda.setMois(Mois.valueOf(moisValue));
        }

        agenda.setJoursNonDisponible(null);
        agenda.setMedecin(null);
        return agenda;
    }

    public static CabinetMedicale mapCabinetMedicale(ResultSet rs) throws SQLException {
        CabinetMedicale cab = new CabinetMedicale();
        cab.setId(rs.getLong("id"));
        cab.setNom(rs.getString("nom"));
        cab.setEmail(rs.getString("email"));
        cab.setLogo(rs.getString("logo"));
        cab.setCin(rs.getString("cin"));
        cab.setTel1(rs.getString("tel1"));
        cab.setTel2(rs.getString("tel2"));
        cab.setSiteWeb(rs.getString("siteWeb"));
        cab.setInstagram(rs.getString("instagram"));
        cab.setFacebook(rs.getString("facebook"));
        cab.setDescription(rs.getString("description"));
        cab.setAdresse(null);
        cab.setCharges(null);
        cab.setRevenues(null);
        cab.setStatistiques(null);
        cab.setStaff(null);
        return cab;
    }

    public static Charges mapCharges(ResultSet rs) throws SQLException {
        Charges charge = new Charges();
        charge.setId(rs.getLong("id"));
        charge.setTitre(rs.getString("titre"));
        charge.setDescription(rs.getString("description"));
        charge.setMontant(rs.getDouble("montant"));

        Timestamp dt = rs.getTimestamp("date");
        if (dt != null) {
            charge.setDate(dt.toLocalDateTime());
        }

        charge.setCabinetMedicale(null);
        return charge;
    }

    public static Revenues mapRevenues(ResultSet rs) throws SQLException {
        Revenues rev = new Revenues();
        rev.setId(rs.getLong("id"));
        rev.setTitre(rs.getString("titre"));
        rev.setDescription(rs.getString("description"));
        rev.setMontant(rs.getDouble("montant"));

        Timestamp dt = rs.getTimestamp("date");
        if (dt != null) {
            rev.setDate(dt.toLocalDateTime());
        }

        rev.setCabinetMedicale(null);
        return rev;
    }

    public static Statistiques mapStatistiques(ResultSet rs) throws SQLException {
        Statistiques stat = new Statistiques();
        stat.setId(rs.getLong("id"));
        stat.setNom(rs.getString("nom"));

        String catValue = rs.getString("categorie");
        if (catValue != null) {
            stat.setCategorie(StatistiqueCategorie.valueOf(catValue));
        }

        stat.setChiffre(rs.getDouble("chiffre"));

        Date dc = rs.getDate("dateCalcul");
        if (dc != null) {
            stat.setDateCalcul(dc.toLocalDate());
        }

        stat.setCabinetMedicale(null);
        return stat;
    }

    public static Adresse mapAdresse(ResultSet rs) throws SQLException {
        Adresse adr = new Adresse();
        adr.setNumero(rs.getString("numero"));
        adr.setRue(rs.getString("rue"));
        adr.setCodePostal(rs.getString("codePostal"));
        adr.setVille(rs.getString("ville"));
        adr.setPays(rs.getString("pays"));
        adr.setComplement(rs.getString("complement"));
        return adr;
    }

    public static Certificat mapCertificat(ResultSet rs) throws SQLException {
        Certificat cert = new Certificat();
        cert.setId(rs.getLong("id"));

        Date db = rs.getDate("dateDebut");
        if (db != null) {
            cert.setDateDebut(db.toLocalDate());
        }

        Date df = rs.getDate("dateFin");
        if (df != null) {
            cert.setDateFin(df.toLocalDate());
        }

        cert.setDuree(rs.getInt("duree"));
        cert.setNoteMedecin(rs.getString("noteMedecin"));
        cert.setDossierMedicale(null);
        cert.setConsultation(null);
        return cert;
    }

    public static Consultation mapConsultation(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();
        c.setId(rs.getLong("id"));

        Date d = rs.getDate("date");
        if (d != null) {
            c.setDate(d.toLocalDate());
        }

        String st = rs.getString("statut");
        if (st != null) {
            c.setStatut(StatutConsultation.valueOf(st));
        }

        c.setObservationMedecin(rs.getString("observationMedecin"));
        c.setDossierMedicale(null);
        c.setInterventionsMedecin(null);
        c.setFactures(null);
        c.setOrdonnances(null);
        c.setCertificat(null);
        c.setRendezVous(null);
        return c;
    }

    public static InterventionMedecin mapInterventionMedecin(ResultSet rs) throws SQLException {
        InterventionMedecin im = new InterventionMedecin();
        im.setId(rs.getLong("id"));
        im.setPrixDePatient(rs.getDouble("prixDePatient"));

        int nd = rs.getInt("numDent");
        if (!rs.wasNull()) {
            im.setNumDent(nd);
        } else {
            im.setNumDent(null);
        }

        im.setConsultation(null);
        im.setActe(null);
        return im;
    }

    public static Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        Ordonnance o = new Ordonnance();
        o.setId(rs.getLong("id"));

        Date d = rs.getDate("date");
        if (d != null) {
            o.setDate(d.toLocalDate());
        }

        o.setDossierMedicale(null);
        o.setPrescriptions(null);
        o.setConsultation(null);
        return o;
    }

    public static DossierMedicale mapDossierMedicale(ResultSet rs) throws SQLException {
        DossierMedicale dm = new DossierMedicale();
        dm.setId(rs.getLong("id"));

        Date d = rs.getDate("dateDeCreation");
        if (d != null) {
            dm.setDateDeCreation(d.toLocalDate());
        }

        dm.setPatient(null);
        dm.setConsultations(null);
        dm.setOrdonnances(null);
        dm.setCertificats(null);
        dm.setSituationFinanciere(null);
        dm.setRendezVous(null);
        dm.setMedecin(null);
        return dm;
    }

    public static SituationFinanciere mapSituationFinanciere(ResultSet rs) throws SQLException {
        SituationFinanciere sf = new SituationFinanciere();
        sf.setId(rs.getLong("id"));
        sf.setTotaleDesActes(rs.getDouble("totaleDesActes"));
        sf.setTotalePaye(rs.getDouble("totalePaye"));
        sf.setCredit(rs.getDouble("credit"));

        String statut = rs.getString("statut");
        if (statut != null) {
            sf.setStatut(StatutSituationFinanciere.valueOf(statut));
        }

        String enPromo = rs.getString("enPromo");
        if (enPromo != null) {
            sf.setEnPromo(EnPromo.valueOf(enPromo));
        }

        sf.setDossierMedicale(null);
        sf.setFactures(null);
        return sf;
    }

    public static Facture mapFacture(ResultSet rs) throws SQLException {
        Facture f = new Facture();
        f.setId(rs.getLong("id"));
        f.setTotaleFacture(rs.getDouble("totaleFacture"));
        f.setTotalePaye(rs.getDouble("totalePaye"));
        f.setReste(rs.getDouble("reste"));

        String statut = rs.getString("statut");
        if (statut != null) {
            f.setStatut(StatutFacture.valueOf(statut));
        }

        Timestamp dt = rs.getTimestamp("dateFacture");
        if (dt != null) {
            f.setDateFacture(dt.toLocalDateTime());
        }

        f.setSituationFinanciere(null);
        f.setConsultation(null);
        return f;
    }

    public static Medicament mapMedicament(ResultSet rs) throws SQLException {
        Medicament m = new Medicament();
        m.setId(rs.getLong("id"));
        m.setNom(rs.getString("nom"));
        m.setLaboratoire(rs.getString("laboratoire"));
        m.setType(rs.getString("type"));

        String forme = rs.getString("forme");
        if (forme != null) {
            m.setForme(FormeMedicament.valueOf(forme));
        }

        m.setRemboursable(rs.getObject("remboursable") != null ? rs.getBoolean("remboursable") : null);
        m.setPrixUnitaire(rs.getDouble("prixUnitaire"));
        m.setDescription(rs.getString("description"));
        m.setPrescriptions(null);
        return m;
    }

    public static Prescription mapPrescription(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setId(rs.getLong("id"));
        p.setQuantite(rs.getInt("quantite"));
        p.setFrequence(rs.getString("frequence"));
        p.setDureeEnJours(rs.getInt("dureeEnJours"));
        p.setOrdonnance(null);
        p.setMedicament(null);
        return p;
    }

    public static Notification mapNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getLong("id"));

        String titre = rs.getString("titre");
        if (titre != null) {
            n.setTitre(NotificationTitre.valueOf(titre));
        }

        n.setMessage(rs.getString("message"));

        Date d = rs.getDate("date");
        if (d != null) {
            n.setDate(d.toLocalDate());
        }

        Time t = rs.getTime("time");
        if (t != null) {
            n.setTime(t.toLocalTime());
        }

        String type = rs.getString("type");
        if (type != null) {
            n.setType(NotificationType.valueOf(type));
        }

        String priorite = rs.getString("priorite");
        if (priorite != null) {
            n.setPriorite(NotificationPriorite.valueOf(priorite));
        }

        n.setUtilisateurs(null);
        return n;
    }

    public static Role mapRole(ResultSet rs) throws SQLException {
        Role r = new Role();
        r.setId(rs.getLong("id"));

        String libelle = rs.getString("libelle");
        if (libelle != null) {
            r.setLibelle(RoleLibelle.valueOf(libelle));
        }

        r.setPrivileges(null);
        r.setUtilisateurs(null);
        return r;
    }

    public static Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getLong("id"));
        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setCin(rs.getString("cin"));
        u.setTel(rs.getString("tel"));

        String sexeValue = rs.getString("sexe");
        if (sexeValue != null) {
            u.setSexe(Sexe.valueOf(sexeValue));
        }

        u.setLogin(rs.getString("login"));
        u.setMotDePass(rs.getString("motDePass"));

        Date lastLogin = rs.getDate("lastLoginDate");
        if (lastLogin != null) {
            u.setLastLoginDate(lastLogin.toLocalDate());
        }

        Date dateNaissance = rs.getDate("dateNaissance");
        if (dateNaissance != null) {
            u.setDateNaissance(dateNaissance.toLocalDate());
        }

        u.setAdresse(null);
        u.setRoles(null);
        u.setNotifications(null);
        return u;
    }

    public static Staff mapStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getLong("id"));
        staff.setNom(rs.getString("nom"));
        staff.setEmail(rs.getString("email"));
        staff.setCin(rs.getString("cin"));
        staff.setTel(rs.getString("tel"));

        String sexeValue = rs.getString("sexe");
        if (sexeValue != null) {
            staff.setSexe(Sexe.valueOf(sexeValue));
        }

        staff.setLogin(rs.getString("login"));
        staff.setMotDePass(rs.getString("motDePass"));

        Date lastLogin = rs.getDate("lastLoginDate");
        if (lastLogin != null) {
            staff.setLastLoginDate(lastLogin.toLocalDate());
        }

        Date dateNaissance = rs.getDate("dateNaissance");
        if (dateNaissance != null) {
            staff.setDateNaissance(dateNaissance.toLocalDate());
        }

        staff.setSalaire(rs.getDouble("salaire"));
        staff.setPrime(rs.getDouble("prime"));

        Date recrutement = rs.getDate("dateRecrutement");
        if (recrutement != null) {
            staff.setDateRecrutement(recrutement.toLocalDate());
        }

        staff.setSoldeConge(rs.getInt("soldeConge"));
        staff.setCabinetMedicale(null);
        staff.setAdresse(null);
        staff.setRoles(null);
        staff.setNotifications(null);
        return staff;
    }

    public static Medecin mapMedecin(ResultSet rs) throws SQLException {
        Medecin medecin = new Medecin();
        medecin.setId(rs.getLong("id"));
        medecin.setNom(rs.getString("nom"));
        medecin.setEmail(rs.getString("email"));
        medecin.setCin(rs.getString("cin"));
        medecin.setTel(rs.getString("tel"));

        String sexeValue = rs.getString("sexe");
        if (sexeValue != null) {
            medecin.setSexe(Sexe.valueOf(sexeValue));
        }

        medecin.setLogin(rs.getString("login"));
        medecin.setMotDePass(rs.getString("motDePass"));

        Date lastLogin = rs.getDate("lastLoginDate");
        if (lastLogin != null) {
            medecin.setLastLoginDate(lastLogin.toLocalDate());
        }

        Date dateNaissance = rs.getDate("dateNaissance");
        if (dateNaissance != null) {
            medecin.setDateNaissance(dateNaissance.toLocalDate());
        }

        medecin.setSalaire(rs.getDouble("salaire"));
        medecin.setPrime(rs.getDouble("prime"));

        Date recrutement = rs.getDate("dateRecrutement");
        if (recrutement != null) {
            medecin.setDateRecrutement(recrutement.toLocalDate());
        }

        medecin.setSoldeConge(rs.getInt("soldeConge"));
        medecin.setSpecialite(rs.getString("specialite"));
        medecin.setAgendaMensuel(null);
        medecin.setDossierMedicaux(null);
        medecin.setCabinetMedicale(null);
        medecin.setAdresse(null);
        medecin.setRoles(null);
        medecin.setNotifications(null);
        return medecin;
    }

    public static Secretaire mapSecretaire(ResultSet rs) throws SQLException {
        Secretaire secretaire = new Secretaire();
        secretaire.setId(rs.getLong("id"));
        secretaire.setNom(rs.getString("nom"));
        secretaire.setEmail(rs.getString("email"));
        secretaire.setCin(rs.getString("cin"));
        secretaire.setTel(rs.getString("tel"));

        String sexeValue = rs.getString("sexe");
        if (sexeValue != null) {
            secretaire.setSexe(Sexe.valueOf(sexeValue));
        }

        secretaire.setLogin(rs.getString("login"));
        secretaire.setMotDePass(rs.getString("motDePass"));

        Date lastLogin = rs.getDate("lastLoginDate");
        if (lastLogin != null) {
            secretaire.setLastLoginDate(lastLogin.toLocalDate());
        }

        Date dateNaissance = rs.getDate("dateNaissance");
        if (dateNaissance != null) {
            secretaire.setDateNaissance(dateNaissance.toLocalDate());
        }

        secretaire.setSalaire(rs.getDouble("salaire"));
        secretaire.setPrime(rs.getDouble("prime"));

        Date recrutement = rs.getDate("dateRecrutement");
        if (recrutement != null) {
            secretaire.setDateRecrutement(recrutement.toLocalDate());
        }

        secretaire.setSoldeConge(rs.getInt("soldeConge"));
        secretaire.setNumCNSS(rs.getString("numCNSS"));
        secretaire.setCommission(rs.getDouble("commission"));
        secretaire.setCabinetMedicale(null);
        secretaire.setAdresse(null);
        secretaire.setRoles(null);
        secretaire.setNotifications(null);
        return secretaire;
    }

    public static Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getLong("id"));
        admin.setNom(rs.getString("nom"));
        admin.setEmail(rs.getString("email"));
        admin.setCin(rs.getString("cin"));
        admin.setTel(rs.getString("tel"));

        String sexeValue = rs.getString("sexe");
        if (sexeValue != null) {
            admin.setSexe(Sexe.valueOf(sexeValue));
        }

        admin.setLogin(rs.getString("login"));
        admin.setMotDePass(rs.getString("motDePass"));

        Date lastLogin = rs.getDate("lastLoginDate");
        if (lastLogin != null) {
            admin.setLastLoginDate(lastLogin.toLocalDate());
        }

        Date dateNaissance = rs.getDate("dateNaissance");
        if (dateNaissance != null) {
            admin.setDateNaissance(dateNaissance.toLocalDate());
        }

        admin.setPermissions(null);
        admin.setAdresse(null);
        admin.setRoles(null);
        admin.setNotifications(null);
        return admin;
    }

    public static RDV mapRDV(ResultSet rs) throws SQLException {
        RDV rdv = new RDV();
        rdv.setId(rs.getLong("id"));

        Date date = rs.getDate("date");
        if (date != null) {
            rdv.setDate(date.toLocalDate());
        }

        Time heure = rs.getTime("heure");
        if (heure != null) {
            rdv.setHeure(heure.toLocalTime());
        }

        rdv.setMotif(rs.getString("motif"));

        String statutValue = rs.getString("statut");
        if (statutValue != null) {
            rdv.setStatut(StatutRDV.valueOf(statutValue));
        }

        rdv.setNoteMedecin(rs.getString("noteMedecin"));
        rdv.setConsultation(null);
        rdv.setDossierMedicale(null);
        return rdv;
    }
}


