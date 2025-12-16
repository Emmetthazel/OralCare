CREATE DATABASE IF NOT EXISTS oralcare_db;
USE oralcare_db;

CREATE TABLE BaseEntity (
    id_entite BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_creation DATETIME,
    date_derniere_modification DATETIME,
    cree_par BIGINT,
    modifie_par BIGINT
);

CREATE TABLE Antecedent (
    id_entite BIGINT PRIMARY KEY,  -- correspond à BaseEntity.id_entite

    nom VARCHAR(255) NOT NULL,

    categorie VARCHAR(50),  -- si tu as un enum CategorieAntecedent, sinon VARCHAR
    niveau_de_risque VARCHAR(10) NOT NULL CHECK (
        niveau_de_risque IN ('LOW', 'MEDIUM', 'HIGH')
    ),

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE cabinet_medicale (
    id_entite BIGINT PRIMARY KEY,

    nom VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    logo VARCHAR(500),

    -- Adresse (embedded)
    numero VARCHAR(50),
    rue VARCHAR(255) NOT NULL,
    code_postal VARCHAR(20) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    pays VARCHAR(100) NOT NULL,
    complement VARCHAR(255),

    cin VARCHAR(20) UNIQUE,
    tel1 VARCHAR(20) UNIQUE,
    tel2 VARCHAR(20),
    siteWeb VARCHAR(255) UNIQUE,
    instagram VARCHAR(255),
    facebook VARCHAR(255),
    description TEXT,

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE Patient (
    id_entite BIGINT PRIMARY KEY,   -- correspond à BaseEntity.id_entite

    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    date_de_naissance DATE,
    email VARCHAR(120),
    sexe VARCHAR(10) NOT NULL,
    adresse VARCHAR(255),
    telephone VARCHAR(20),

    assurance VARCHAR(10) NOT NULL,

    CONSTRAINT chk_patient_sexe CHECK (sexe IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_patient_assurance CHECK (assurance IN ('CNOPS', 'CNSS', 'RAMED', 'NONE')),

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE
);





CREATE TABLE Medicament (
    id_entite BIGINT PRIMARY KEY,  -- correspond à BaseEntity.id_entite

    nom VARCHAR(255) NOT NULL,
    laboratoire VARCHAR(255),
    type VARCHAR(255),
    forme VARCHAR(20) NOT NULL CHECK (
        forme IN ('TABLET', 'CAPSULE', 'SYRUP', 'INJECTION', 'CREAM', 'OINTMENT', 'DROPS')
    ),
    remboursable BOOLEAN DEFAULT FALSE,
    prix_unitaire DECIMAL(10,2),
    description TEXT,

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE acte (
    id_entite BIGINT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    categorie VARCHAR(255),
    prix_de_base DECIMAL(10,2),
    CONSTRAINT fk_acte_base FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite) ON DELETE CASCADE
);

CREATE TABLE role (
    id_entite BIGINT PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL,
    CONSTRAINT fk_role_base FOREIGN KEY (id_entite) REFERENCES BaseEntity(id_entite) ON DELETE CASCADE,
    CONSTRAINT chk_role_libelle CHECK (libelle IN ('ADMIN', 'DOCTOR', 'SECRETARY', 'RECEPTIONIST'))
);

CREATE TABLE utilisateur (
    id_entite BIGINT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    cin VARCHAR(50) UNIQUE,
    tel VARCHAR(50) UNIQUE,
    sexe VARCHAR(50),
    login VARCHAR(255) UNIQUE NOT NULL,
    mot_de_pass VARCHAR(255),
    date_naissance DATE,
    last_login_date DATE,
    numero VARCHAR(50),
    rue VARCHAR(255),
    code_postal VARCHAR(20),
    ville VARCHAR(100),
    pays VARCHAR(100),
    complement VARCHAR(255),
    CONSTRAINT fk_utilisateur_base FOREIGN KEY (id_entite) REFERENCES BaseEntity(id_entite) ON DELETE CASCADE
);

CREATE TABLE Staff (
    id_entite BIGINT PRIMARY KEY,  -- correspond à Utilisateur.id_entite
    salaire DECIMAL(10,2),
    prime DECIMAL(10,2),
    date_recrutement DATE,
    solde_conge INT,
    cabinet_id BIGINT NULL,

    -- FK vers Utilisateur
    CONSTRAINT fk_staff_utilisateur
        FOREIGN KEY (id_entite)
        REFERENCES utilisateur(id_entite)
        ON DELETE CASCADE,

    CONSTRAINT fk_staff_cabinet
        FOREIGN KEY (cabinet_id)
        REFERENCES cabinet_medicale(id_entite)
        ON DELETE SET NULL
);

CREATE TABLE Secretaire (
    id_entite BIGINT PRIMARY KEY,

    num_cnss VARCHAR(50) UNIQUE,
    commission DECIMAL(10, 2),

    CONSTRAINT fk_secretaire_staff
        FOREIGN KEY (id_entite)
        REFERENCES Staff(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE Medecin (
    id_entite BIGINT PRIMARY KEY,
    specialite VARCHAR(255),

    CONSTRAINT fk_medecin_staff
        FOREIGN KEY (id_entite)
        REFERENCES Staff(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE Admin (
    id_entite BIGINT PRIMARY KEY,
    FOREIGN KEY (id_entite) REFERENCES utilisateur(id_entite) ON DELETE CASCADE
);


CREATE TABLE DossierMedicale (
    id_entite BIGINT PRIMARY KEY,
    patient_id BIGINT UNIQUE NOT NULL,
    medecin_id BIGINT NULL,

    FOREIGN KEY (id_entite) REFERENCES BaseEntity(id_entite) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES Patient(id_entite) ON DELETE CASCADE,
    FOREIGN KEY (medecin_id) REFERENCES Medecin(id_entite) ON DELETE SET NULL
);

CREATE TABLE SituationFinanciere (
    id_entite BIGINT PRIMARY KEY,

    totale_des_actes DECIMAL(10,2),
    totale_paye DECIMAL(10,2),
    credit DECIMAL(10,2),

    statut VARCHAR(20) NOT NULL CHECK (
        statut IN ('ACTIVE', 'ARCHIVED', 'CLOSED')
    ),

    en_promo VARCHAR(10) NOT NULL CHECK (
        en_promo IN ('YES', 'NO')
    ),

    dossier_medicale_id BIGINT UNIQUE NULL,

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE,

    CONSTRAINT fk_situation_dossier
            FOREIGN KEY (dossier_medicale_id)
            REFERENCES DossierMedicale(id_entite)
            ON DELETE CASCADE
);

CREATE TABLE Consultation (
    id_entite BIGINT PRIMARY KEY,

    date DATE,
    statut VARCHAR(20) NOT NULL CHECK (
        statut IN ('SCHEDULED', 'COMPLETED', 'CANCELLED')
    ),
    observation_medecin TEXT,

    dossier_medicale_id BIGINT NOT NULL,

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE,

    FOREIGN KEY (dossier_medicale_id)
        REFERENCES DossierMedicale(id_entite)
        ON DELETE CASCADE

);

CREATE TABLE Facture (
    id_entite BIGINT PRIMARY KEY, -- Héritage de BaseEntity

    -- Champs Facture
    totale_facture DECIMAL(10, 2),
    totale_paye DECIMAL(10, 2),
    reste DECIMAL(10, 2),

    statut VARCHAR(20) NOT NULL CHECK (
        statut IN ('PAID', 'PENDING', 'OVERDUE', 'CANCELLED') -- Mappage de StatutFacture
    ),

    date_facture DATETIME NOT NULL,

    consultation_id BIGINT UNIQUE NULL,

    situation_financiere_id BIGINT UNIQUE NULL,

    CONSTRAINT fk_facture_base FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite) ON DELETE CASCADE,

    CONSTRAINT fk_facture_consultation FOREIGN KEY (consultation_id)
        REFERENCES Consultation(id_entite) ON DELETE SET NULL, -- Suppression en cascade non nécessaire ici si Facture est dépendante

    CONSTRAINT fk_facture_situation_financiere FOREIGN KEY (situation_financiere_id)
        REFERENCES SituationFinanciere(id_entite) ON DELETE SET NULL
);

CREATE TABLE Certificat (
    id_entite BIGINT PRIMARY KEY,
    date_debut DATE,
    date_fin DATE,
    duree INT,
    note_medecin VARCHAR(500),
    consultation_id BIGINT UNIQUE NOT NULL,
    FOREIGN KEY (id_entite) REFERENCES BaseEntity(id_entite) ON DELETE CASCADE,
    FOREIGN KEY (consultation_id) REFERENCES Consultation(id_entite) ON DELETE CASCADE
);

CREATE TABLE RDV (
    id_entite BIGINT PRIMARY KEY,  -- correspond à BaseEntity.id_entite

    date DATE NOT NULL,
    heure TIME NOT NULL,
    motif VARCHAR(255),
    statut VARCHAR(20) NOT NULL CHECK (
        statut IN ('CONFIRMED', 'PENDING', 'CANCELLED', 'COMPLETED')
    ),
    note_medecin TEXT,

    consultation_id BIGINT NULL,

    dossier_medicale_id BIGINT NOT NULL,

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE,

    FOREIGN KEY (consultation_id)
        REFERENCES Consultation(id_entite)
        ON DELETE SET NULL,

    FOREIGN KEY (dossier_medicale_id)
        REFERENCES DossierMedicale(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE Ordonnance (
    id_entite BIGINT PRIMARY KEY,
    date_ordonnance DATE,

    dossier_medicale_id BIGINT NOT NULL,
    consultation_id BIGINT NULL,

    FOREIGN KEY (id_entite) REFERENCES BaseEntity(id_entite) ON DELETE CASCADE,
    FOREIGN KEY (dossier_medicale_id) REFERENCES DossierMedicale(id_entite) ON DELETE CASCADE,
    FOREIGN KEY (consultation_id) REFERENCES Consultation(id_entite) ON DELETE SET NULL
);

CREATE TABLE Prescription (
    id_entite BIGINT PRIMARY KEY,
    quantite INT NOT NULL,
    frequence VARCHAR(255),
    duree_en_jours INT,

    ordonnance_id BIGINT NOT NULL,

    medicament_id BIGINT NOT NULL,

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE,

    FOREIGN KEY (ordonnance_id)
        REFERENCES Ordonnance(id_entite)
        ON DELETE CASCADE,

    FOREIGN KEY (medicament_id)
        REFERENCES Medicament(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE AgendaMensuel (
    id_entite BIGINT PRIMARY KEY,

    mois VARCHAR(20) NOT NULL,

    annee INT NOT NULL CHECK (annee >= 2025),

    medecin_id BIGINT NOT NULL,

    CONSTRAINT chk_agenda_mois CHECK (
        mois IN (
            'JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE',
            'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER'
        )
    ),

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE,

    FOREIGN KEY (medecin_id)
        REFERENCES Medecin(id_entite)
        ON DELETE CASCADE,

    CONSTRAINT unique_medecin_mois_annee UNIQUE (medecin_id, mois, annee)
);

CREATE TABLE AgendaMensuel_JourNonDisponible (
    agenda_id BIGINT NOT NULL,
    jour_non_disponible VARCHAR(20) NOT NULL CHECK (
        jour_non_disponible IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') -- Ajustez selon votre Enum Jour
    ),
    PRIMARY KEY (agenda_id, jour_non_disponible),
    FOREIGN KEY (agenda_id)
        REFERENCES AgendaMensuel(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE Notification (
    id_entite BIGINT PRIMARY KEY,

    titre VARCHAR(50) NOT NULL,
    message TEXT,
    date DATE,
    time TIME,
    type VARCHAR(50),
    priorite VARCHAR(50),

    CONSTRAINT chk_notification_titre CHECK (
        titre IN ('APPOINTMENT_REMINDER', 'PAYMENT_DUE', 'NEW_MESSAGE', 'SYSTEM_UPDATE', 'EMERGENCY')
    ),
    CONSTRAINT chk_notification_type CHECK (
        type IN ('ALERT', 'INFO', 'WARNING', 'SUCCESS')
    ),
    CONSTRAINT chk_notification_priorite CHECK (
        priorite IN ('HIGH', 'MEDIUM', 'LOW')
    ),

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE notification_utilisateur (
    notification_id BIGINT NOT NULL,
    utilisateur_id BIGINT NOT NULL,
    est_lu BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (notification_id, utilisateur_id),

    FOREIGN KEY (notification_id)
        REFERENCES Notification(id_entite)
        ON DELETE CASCADE,

    FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE role_privileges (
    role_id BIGINT NOT NULL,
    privilege VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, privilege),
    CONSTRAINT fk_role_privilege FOREIGN KEY (role_id) REFERENCES role(id_entite) ON DELETE CASCADE
);

CREATE TABLE utilisateur_role (
    utilisateur_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (utilisateur_id, role_id),
    CONSTRAINT fk_utilisateur_role FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id_entite) ON DELETE CASCADE,
    CONSTRAINT fk_role_utilisateur FOREIGN KEY (role_id) REFERENCES role(id_entite) ON DELETE CASCADE
);

CREATE TABLE Revenues (
    id_entite BIGINT PRIMARY KEY,

    titre VARCHAR(255) NOT NULL,
    description TEXT,
    montant DECIMAL(10,2) NOT NULL,
    date DATETIME NOT NULL,

    cabinet_medicale_id BIGINT NOT NULL,

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE,

    FOREIGN KEY (cabinet_medicale_id)
        REFERENCES cabinet_medicale(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE Statistiques (
    id_entite BIGINT PRIMARY KEY,

    nom VARCHAR(255) NOT NULL,
    categorie VARCHAR(50) NOT NULL CHECK (
        categorie IN ('REVENUE', 'EXPENSE', 'PATIENT_COUNT', 'APPOINTMENT_COUNT', 'TREATMENT_COUNT')
    ),
    chiffre DECIMAL(10,2),
    date_calcul DATE,

    cabinet_medicale_id BIGINT NOT NULL,

    FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite)
        ON DELETE CASCADE,

    FOREIGN KEY (cabinet_medicale_id)
        REFERENCES cabinet_medicale(id_entite)
        ON DELETE CASCADE
);

CREATE TABLE charges (
    id_entite BIGINT PRIMARY KEY,
    titre VARCHAR(255),
    description TEXT,
    montant DECIMAL(10,2),
    date DATETIME,
    cabinet_id BIGINT NULL,
    CONSTRAINT fk_charges_base FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite) ON DELETE CASCADE,
    CONSTRAINT fk_charges_cabinet FOREIGN KEY (cabinet_id)
        REFERENCES cabinet_medicale(id_entite) ON DELETE SET NULL
);


CREATE TABLE intervention_medecin (
    id_entite BIGINT PRIMARY KEY,
    prix_de_patient DECIMAL(10,2),
    num_dent INT,
    consultation_id BIGINT NULL,
    acte_id BIGINT,
    CONSTRAINT fk_intervention_base FOREIGN KEY (id_entite)
        REFERENCES BaseEntity(id_entite) ON DELETE CASCADE,
    CONSTRAINT fk_intervention_consultation FOREIGN KEY (consultation_id)
        REFERENCES Consultation(id_entite) ON DELETE SET NULL,
    CONSTRAINT fk_intervention_acte FOREIGN KEY (acte_id)
        REFERENCES acte(id_entite) ON DELETE SET NULL
);


CREATE TABLE Patient_Antecedent (
    patient_id BIGINT,
    antecedent_id BIGINT,
    PRIMARY KEY (patient_id, antecedent_id),
    FOREIGN KEY (patient_id) REFERENCES Patient(id_entite) ON DELETE CASCADE,
    FOREIGN KEY (antecedent_id) REFERENCES Antecedent(id_entite) ON DELETE CASCADE
);

