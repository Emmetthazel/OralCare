package ma.oralCare.repository.common;
import ma.oralCare.entities.patient.*;
import ma.oralCare.entities.acte.*;
import ma.oralCare.entities.agenda.*;
import ma.oralCare.entities.cabinet.*;
import ma.oralCare.entities.common.*;
import ma.oralCare.entities.consultation.*;
import ma.oralCare.entities.dossier.*;
import ma.oralCare.entities.facture.*;
import ma.oralCare.entities.staff.*;
import ma.oralCare.entities.enums.*;
import ma.oralCare.entities.rdv.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Date;
import java.sql.Timestamp;


public final class RowMappers {
    private RowMappers(){}

    public static Patient mapPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();

        patient.setId(rs.getLong("id"));
        patient.setNom(rs.getString("nom"));
        patient.setPrenom(rs.getString("prenom"));
        patient.setAdresse(rs.getString("adresse"));
        patient.setTelephone(rs.getString("telephone"));
        patient.setEmail(rs.getString("email"));

        var dn = rs.getDate("dateNaissance");
        if (dn != null) {
            patient.setDateNaissance(dn.toLocalDate());
        }

        var dc = rs.getTimestamp("dateCreation");
        if (dc != null) {
            patient.setDateCreation(dc.toLocalDateTime());
        }

        // --- ENUMS ---
        String sexeValue = rs.getString("sexe");
        if (sexeValue != null) {
            patient.setSexe(Sexe.valueOf(sexeValue));
        }

        String assuranceValue = rs.getString("assurance");
        if (assuranceValue != null) {
            patient.setAssurance(Assurance.valueOf(assuranceValue));
        }
        patient.setAntecedents(null); // chargé plus tard dans RepositoryImpl

        return patient;

    }

    public static Acte mapActe(ResultSet rs) throws SQLException {
        Acte acte = new Acte();

        // ----- ATTRIBUTS DE ACTE -----
        acte.setId(rs.getLong("id"));
        acte.setLibelle(rs.getString("libelle"));
        acte.setCategorie(rs.getString("categorie"));
        acte.setPrixDeBase(rs.getDouble("prixDeBase"));

        // interventionsMedecin sera chargé plus tard dans son repository
        acte.setInterventionsMedecin(null);

        // ----- ATTRIBUTS HÉRITÉS DE BaseEntity -----
        acte.setIdEntite(rs.getLong("idEntite"));

        var dc = rs.getDate("dateCreation");
        if (dc != null) acte.setDateCreation(dc.toLocalDate());

        var dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) acte.setDateDerniereModification(dm.toLocalDateTime());

        acte.setCreePar(rs.getString("creePar"));
        acte.setModifiePar(rs.getString("modifiePar"));

        return acte;
    }

    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {

        AgendaMensuel agenda = new AgendaMensuel();

        // ----- CHAMPS PROPRES A AgendaMensuel -----
        agenda.setId(rs.getLong("id"));

        // Enum Mois
        String moisValue = rs.getString("mois");
        if (moisValue != null) {
            agenda.setMois(Mois.valueOf(moisValue));
        }

        // La liste joursNonDisponible sera chargée plus tard dans AgendaRepositoryImpl
        agenda.setJoursNonDisponible(null);

        // Le médécin sera chargé plus tard
        agenda.setMedecin(null);

        // ----- ATTRIBUTS HÉRITÉS DE BaseEntity -----
        agenda.setIdEntite(rs.getLong("idEntite"));

        var dc = rs.getDate("dateCréation");
        if (dc != null) agenda.setDateCreation(dc.toLocalDate());

        var dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) agenda.setDateDerniereModification(dm.toLocalDateTime());

        agenda.setCreePar(rs.getString("creePar"));
        agenda.setModifiePar(rs.getString("modifiePar"));

        return agenda;
    }


    public static CabinetMedicale mapCabinetMedicale(ResultSet rs) throws SQLException {

        CabinetMedicale cab = new CabinetMedicale();

        // ----- CHAMPS PROPRES A CabinetMedicale -----
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

        // Adresse sera chargée plus tard (table séparée → clé étrangère)
        cab.setAdresse(null);

        // Relations 1..n chargées plus tard dans RepositoryImpl
        cab.setCharges(null);
        cab.setRevenues(null);
        cab.setStatistiques(null);
        cab.setStaff(null);

        // ----- ATTRIBUTS HÉRITÉS DE BaseEntity -----
        cab.setIdEntite(rs.getLong("idEntite"));

        var dc = rs.getDate("dateCréation");
        if (dc != null) cab.setDateCreation(dc.toLocalDate());

        var dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) cab.setDateDerniereModification(dm.toLocalDateTime());

        cab.setCreePar(rs.getString("creePar"));
        cab.setModifiePar(rs.getString("modifiePar"));

        return cab;
    }

    public static Charges mapCharges(ResultSet rs) throws SQLException {

        Charges charge = new Charges();

        // ----- CHAMPS PROPRES À Charges -----
        charge.setId(rs.getLong("id"));
        charge.setTitre(rs.getString("titre"));
        charge.setDescription(rs.getString("description"));
        charge.setMontant(rs.getDouble("montant"));

        var dt = rs.getTimestamp("date");
        if (dt != null) {
            charge.setDate(dt.toLocalDateTime());
        }

        // Relation CabinetMedicale chargée plus tard
        charge.setCabinetMedicale(null);

        // ----- ATTRIBUTS HÉRITÉS DE BaseEntity -----
        charge.setIdEntite(rs.getLong("idEntite"));

        var dc = rs.getDate("dateCréation");
        if (dc != null) charge.setDateCreation(dc.toLocalDate());

        var dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) charge.setDateDerniereModification(dm.toLocalDateTime());

        charge.setCreePar(rs.getString("creePar"));
        charge.setModifiePar(rs.getString("modifiePar"));

        return charge;
    }

    public static Revenues mapRevenues(ResultSet rs) throws SQLException {

        Revenues rev = new Revenues();

        // ----- CHAMPS PROPRES A Revenues -----
        rev.setId(rs.getLong("id"));
        rev.setTitre(rs.getString("titre"));
        rev.setDescription(rs.getString("description"));
        rev.setMontant(rs.getDouble("montant"));

        var dt = rs.getTimestamp("date");
        if (dt != null) {
            rev.setDate(dt.toLocalDateTime());
        }

        // Relation CabinetMedicale chargée plus tard
        rev.setCabinetMedicale(null);

        // ----- ATTRIBUTS HÉRITÉS DE BaseEntity -----
        rev.setIdEntite(rs.getLong("idEntite"));

        var dc = rs.getDate("dateCréation");
        if (dc != null) rev.setDateCreation(dc.toLocalDate());

        var dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) rev.setDateDerniereModification(dm.toLocalDateTime());

        rev.setCreePar(rs.getString("creePar"));
        rev.setModifiePar(rs.getString("modifiePar"));

        return rev;
    }

    public static Statistiques mapStatistiques(ResultSet rs) throws SQLException {

        Statistiques stat = new Statistiques();

        // ----- CHAMPS PROPRES A Statistiques -----
        stat.setId(rs.getLong("id"));
        stat.setNom(rs.getString("nom"));

        // Enum : StatistiqueCategorie
        String catValue = rs.getString("categorie");
        if (catValue != null) {
            stat.setCategorie(StatistiqueCategorie.valueOf(catValue));
        }

        stat.setChiffre(rs.getDouble("chiffre"));

        var dc = rs.getDate("dateCalcul");
        if (dc != null) {
            stat.setDateCalcul(dc.toLocalDate());
        }

        // Relation CabinetMedicale → chargée plus tard
        stat.setCabinetMedicale(null);

        // ----- ATTRIBUTS HÉRITÉS DE BaseEntity -----
        stat.setIdEntite(rs.getLong("idEntite"));

        var dcreat = rs.getDate("dateCreation");
        if (dcreat != null) stat.setDateCreation(dcreat.toLocalDate());

        var dmod = rs.getTimestamp("dateDerniereModification");
        if (dmod != null) stat.setDateDerniereModification(dmod.toLocalDateTime());

        stat.setCreePar(rs.getString("creePar"));
        stat.setModifiePar(rs.getString("modifiePar"));

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

        // ----- CHAMPS PROPRES A Certificat -----
        cert.setId(rs.getLong("id"));

        var db = rs.getDate("dateDebut");
        if (db != null) cert.setDateDebut(db.toLocalDate());

        var df = rs.getDate("dateFin");
        if (df != null) cert.setDateFin(df.toLocalDate());

        cert.setDuree(rs.getInt("duree"));
        cert.setNoteMedecin(rs.getString("noteMedecin"));

        // Relation : DossierMedicale (chargée plus tard)
        Long dossierId = rs.getLong("dossierMedicale_id");
        if (!rs.wasNull()) {
            DossierMedicale d = new DossierMedicale();
            d.setId(dossierId);
            cert.setDossierMedicale(d);
        } else {
            cert.setDossierMedicale(null);
        }

        // Relation : Consultation (chargée plus tard)
        Long consId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation c = new Consultation();
            c.setId(consId);
            cert.setConsultation(c);
        } else {
            cert.setConsultation(null);
        }

        // ----- ATTRIBUTS HÉRITÉS DE BaseEntity -----
        cert.setIdEntite(rs.getLong("idEntite"));

        var dcreat = rs.getDate("dateCréation");
        if (dcreat != null) cert.setDateCreation(dcreat.toLocalDate());

        var dmod = rs.getTimestamp("dateDerniereModification");
        if (dmod != null) cert.setDateDerniereModification(dmod.toLocalDateTime());

        cert.setCreePar(rs.getString("creePar"));
        cert.setModifiePar(rs.getString("modifiePar"));

        return cert;
    }

    public static Consultation mapConsultation(ResultSet rs) throws SQLException {

        Consultation c = new Consultation();

        // ----- CHAMPS PROPRES À Consultation -----
        c.setId(rs.getLong("id"));

        var d = rs.getDate("date");
        if (d != null) {
            c.setDate(d.toLocalDate());
        }

        // Enum : StatutConsultation
        String st = rs.getString("statut");
        if (st != null) {
            c.setStatut(StatutConsultation.valueOf(st));
        }

        c.setObservationMedecin(rs.getString("observationMedecin"));


        // ----- RELATIONS : CHARGÉES PLUS TARD -----

        // DossierMedicale (FK : dossierMedicale_id)
        Long dossierId = rs.getLong("dossierMedicale_id");
        if (!rs.wasNull()) {
            DossierMedicale dm = new DossierMedicale();
            dm.setId(dossierId);
            c.setDossierMedicale(dm);
        } else {
            c.setDossierMedicale(null);
        }

        // Certificat (FK : certificat_id)
        Long certId = rs.getLong("certificat_id");
        if (!rs.wasNull()) {
            Certificat cert = new Certificat();
            cert.setId(certId);
            c.setCertificat(cert);
        } else {
            c.setCertificat(null);
        }

        // Listes : chargées séparément (lazy)
        c.setInterventionsMedecin(null);
        c.setFactures(null);
        c.setOrdonnances(null);
        c.setRendezVous(null);


        // ----- ATTRIBUTS HÉRITÉS DE BaseEntity -----
        c.setIdEntite(rs.getLong("idEntite"));

        var dcreat = rs.getDate("dateCreation");
        if (dcreat != null) c.setDateCreation(dcreat.toLocalDate());

        var dmod = rs.getTimestamp("dateDerniereModification");
        if (dmod != null) c.setDateDerniereModification(dmod.toLocalDateTime());

        c.setCreePar(rs.getString("creePar"));
        c.setModifiePar(rs.getString("modifiePar"));

        return c;
    }

    public static InterventionMedecin mapInterventionMedecin(ResultSet rs) throws SQLException {

        InterventionMedecin im = new InterventionMedecin();

        // ----- CHAMPS PROPRES À InterventionMedecin -----
        im.setId(rs.getLong("id"));
        im.setPrixDePatient(rs.getDouble("prixDePatient"));

        Integer nd = rs.getInt("numDent");
        if (!rs.wasNull()) {
            im.setNumDent(nd);
        } else {
            im.setNumDent(null);
        }

        // ----- RELATIONS : CONSULTATION (FK : consultation_id) -----
        Long cId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation c = new Consultation();
            c.setId(cId);
            im.setConsultation(c);
        } else {
            im.setConsultation(null);
        }

        // ----- RELATIONS : ACTE (FK : acte_id) -----
        Long acteId = rs.getLong("acte_id");
        if (!rs.wasNull()) {
            Acte a = new Acte();
            a.setId(acteId);
            im.setActe(a);
        } else {
            im.setActe(null);
        }

        // ----- ATTRIBUTS HÉRITÉS DE BaseEntity -----
        im.setIdEntite(rs.getLong("idEntite"));

        var dcreat = rs.getDate("dateCréation");
        if (dcreat != null) {
            im.setDateCreation(dcreat.toLocalDate());
        }

        var dmod = rs.getTimestamp("dateDerniereModification");
        if (dmod != null) {
            im.setDateDerniereModification(dmod.toLocalDateTime());
        }

        im.setCreePar(rs.getString("creePar"));
        im.setModifiePar(rs.getString("modifiePar"));

        return im;
    }

    public static Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {

        Ordonnance o = new Ordonnance();

        // ----- CHAMPS PROPRES À Ordonnance -----
        o.setId(rs.getLong("id"));

        var d = rs.getDate("date");
        if (d != null) {
            o.setDate(d.toLocalDate());
        }

        // ----- RELATION : DOSSIER MEDICALE (FK : dossierMedicale_id) -----
        Long dossierId = rs.getLong("dossierMedicale_id");
        if (!rs.wasNull()) {
            DossierMedicale dm = new DossierMedicale();
            dm.setId(dossierId);
            o.setDossierMedicale(dm);
        } else {
            o.setDossierMedicale(null);
        }

        // ----- RELATION : CONSULTATION (FK : consultation_id) -----
        Long consultationId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation c = new Consultation();
            c.setId(consultationId);
            o.setConsultation(c);
        } else {
            o.setConsultation(null);
        }

        // Liste des prescriptions → chargée plus tard
        o.setPrescriptions(null);

        // ----- CHAMPS HÉRITÉS DE BaseEntity -----
        o.setIdEntite(rs.getLong("idEntite"));

        var dcreat = rs.getDate("dateCreation");
        if (dcreat != null) {
            o.setDateCreation(dcreat.toLocalDate());
        }

        var dmod = rs.getTimestamp("dateDerniereModification");
        if (dmod != null) {
            o.setDateDerniereModification(dmod.toLocalDateTime());
        }

        o.setCreePar(rs.getString("creePar"));
        o.setModifiePar(rs.getString("modifiePar"));

        return o;
    }

    public static DossierMedicale mapDossierMedicale(ResultSet rs) throws SQLException {

        DossierMedicale dm = new DossierMedicale();

        // ----- CHAMPS PROPRES À DossierMedicale -----
        dm.setId(rs.getLong("id"));

        var d = rs.getDate("dateDeCreation");
        if (d != null) {
            dm.setDateDeCreation(d.toLocalDate());
        }

        // ----- RELATION : Patient (FK : patient_id) -----
        Long patientId = rs.getLong("patient_id");
        if (!rs.wasNull()) {
            Patient p = new Patient();
            p.setId(patientId);
            dm.setPatient(p);
        } else {
            dm.setPatient(null);
        }

        // ----- RELATION : Medecin (FK : medecin_id / id_user) -----
        Long medId = rs.getLong("medecin_id");
        if (!rs.wasNull()) {
            Medecin m = new Medecin();
            m.setId(medId);
            dm.setMedecin(m);
        } else {
            dm.setMedecin(null);
        }

        // ----- RELATION : Situation Financière (FK : situationFinanciere_id) -----
        Long idSF = rs.getLong("situationFinanciere_id");
        if (!rs.wasNull()) {
            SituationFinanciere sf = new SituationFinanciere();
            sf.setId(idSF);
            dm.setSituationFinanciere(sf);
        } else {
            dm.setSituationFinanciere(null);
        }

        // ----- LISTES : chargées plus tard -----
        dm.setConsultations(null);
        dm.setOrdonnances(null);
        dm.setCertificats(null);
        dm.setRendezVous(null);

        // ----- CHAMPS HÉRITÉS DE BaseEntity -----
        dm.setIdEntite(rs.getLong("idEntite"));

        var dcreat = rs.getDate("dateCreation");
        if (dcreat != null) dm.setDateCreation(dcreat.toLocalDate());

        var dmod = rs.getTimestamp("dateDerniereModification");
        if (dmod != null) dm.setDateDerniereModification(dmod.toLocalDateTime());

        dm.setCreePar(rs.getString("creePar"));
        dm.setModifiePar(rs.getString("modifiePar"));

        return dm;
    }

    public static Staff mapStaff(ResultSet rs) throws SQLException {

        Staff staff = new Staff();

        // Champs hérités de Utilisateur
        staff.setId(rs.getLong("id"));
        staff.setNom(rs.getString("nom"));
        staff.setEmail(rs.getString("email"));
        staff.setCin(rs.getString("cin"));
        staff.setTel(rs.getString("tel"));

        String sexeValue = rs.getString("sexe");
        if (sexeValue != null) {
            staff.setSexe(Sexe.valueOf(sexeValue)); // Assure-toi que la colonne contient MALE/FEMALE/OTHER
        }

        staff.setLogin(rs.getString("login"));
        staff.setMotDePass(rs.getString("mot_de_pass"));

        Date lastLogin = rs.getDate("last_login_date");
        if (lastLogin != null) {
            staff.setLastLoginDate(lastLogin.toLocalDate());
        }

        Date dateNaissance = rs.getDate("date_naissance");
        if (dateNaissance != null) {
            staff.setDateNaissance(dateNaissance.toLocalDate());
        }

        // Champs spécifiques à Staff
        staff.setSalaire(rs.getDouble("salaire"));
        staff.setPrime(rs.getDouble("prime"));

        Date recrutement = rs.getDate("date_recrutement");
        if (recrutement != null) {
            staff.setDateRecrutement(recrutement.toLocalDate());
        }

        staff.setSoldeConge(rs.getInt("solde_conge"));

        // Cabinet médical (clé étrangère)
        Long cabinetId = rs.getLong("cabinet_id");
        if (cabinetId != null && cabinetId != 0) {
            CabinetMedicale cab = new CabinetMedicale();
            cab.setId(cabinetId);
            staff.setCabinetMedicale(cab);
        }

        // Les rôles et notifications doivent être chargés ailleurs (requêtes séparées)
        return staff;
    }

    public static Facture mapFacture(ResultSet rs) throws SQLException {

        Facture f = new Facture();

        // ----- CHAMPS PROPRES À Facture -----
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

        // ----- RELATION : SITUATION FINANCIERE (FK : situationFinanciere_id) -----
        Long sfId = rs.getLong("situationFinanciere_id");
        if (!rs.wasNull()) {
            SituationFinanciere sf = new SituationFinanciere();
            sf.setId(sfId);
            f.setSituationFinanciere(sf);
        } else {
            f.setSituationFinanciere(null);
        }

        // ----- RELATION : CONSULTATION (FK : consultation_id) -----
        Long consId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation c = new Consultation();
            c.setId(consId);
            f.setConsultation(c);
        } else {
            f.setConsultation(null);
        }

        // ----- CHAMPS HÉRITÉS DE BaseEntity -----
        f.setIdEntite(rs.getLong("idEntite"));

        Date dcreat = rs.getDate("dateCreation");
        if (dcreat != null) {
            f.setDateCreation(dcreat.toLocalDate());
        }

        Timestamp dmod = rs.getTimestamp("dateDerniereModification");
        if (dmod != null) {
            f.setDateDerniereModification(dmod.toLocalDateTime());
        }

        f.setCreePar(rs.getString("creePar"));
        f.setModifiePar(rs.getString("modifiePar"));

        return f;
    }

    public static RDV mapRDV(ResultSet rs) throws SQLException {
        RDV rdv = new RDV();

        // ----- CHAMPS PROPRES À RDV -----
        rdv.setId(rs.getLong("id"));

        var date = rs.getDate("date");
        if (date != null) {
            rdv.setDate(date.toLocalDate());
        }

        var heure = rs.getTime("heure");
        if (heure != null) {
            rdv.setHeure(heure.toLocalTime());
        }

        rdv.setMotif(rs.getString("motif"));

        String statutValue = rs.getString("statut");
        if (statutValue != null) {
            rdv.setStatut(StatutRDV.valueOf(statutValue));
        }

        rdv.setNoteMedecin(rs.getString("noteMedecin"));

        // ----- RELATIONS -----

        // Consultation (FK : consultation_id)
        Long consId = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            Consultation c = new Consultation();
            c.setId(consId);
            rdv.setConsultation(c);
        } else {
            rdv.setConsultation(null);
        }

        // DossierMedicale (FK : dossierMedicale_id)
        Long dossierId = rs.getLong("dossierMedicale_id");
        if (!rs.wasNull()) {
            DossierMedicale dm = new DossierMedicale();
            dm.setId(dossierId);
            rdv.setDossierMedicale(dm);
        } else {
            rdv.setDossierMedicale(null);
        }

        // ----- CHAMPS HÉRITÉS DE BaseEntity -----
        rdv.setIdEntite(rs.getLong("idEntite"));

        var dateCreation = rs.getDate("dateCreation");
        if (dateCreation != null) {
            rdv.setDateCreation(dateCreation.toLocalDate());
        }

        var dateModif = rs.getTimestamp("dateDerniereModification");
        if (dateModif != null) {
            rdv.setDateDerniereModification(dateModif.toLocalDateTime());
        }

        rdv.setCreePar(rs.getString("creePar"));
        rdv.setModifiePar(rs.getString("modifiePar"));

        return rdv;
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
            antecedent.setNiveauRisque(NiveauDeRisque.valueOf(risqueValue)); // ou fromLibelle si base contient le libellé
        }

        antecedent.setPatients(null); // chargement plus tard

        return antecedent;
    }


}
