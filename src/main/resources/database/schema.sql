-- =====================================================
-- Schema de base de donnees pour le systeme OralCare
-- =====================================================

-- Creation de la base de donnees
CREATE DATABASE IF NOT EXISTS oralcare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE oralcare_db;

-- =====================================================
-- Tables de base (heritage de BaseEntity)
-- =====================================================

-- Table des utilisateurs
CREATE TABLE utilisateurs (
    id_user BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    numero VARCHAR(10),
    rue VARCHAR(255),
    code_postal VARCHAR(10),
    ville VARCHAR(100),
    pays VARCHAR(100),
    complement VARCHAR(255),
    cin VARCHAR(20) UNIQUE,
    tel VARCHAR(20),
    sexe ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    login VARCHAR(100) UNIQUE NOT NULL,
    mot_de_pass VARCHAR(255) NOT NULL,
    last_login_date DATE,
    date_naissance DATE,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100)
);

-- Table des roles
CREATE TABLE roles (
    id_role BIGINT PRIMARY KEY AUTO_INCREMENT,
    libelle ENUM('ADMIN', 'DOCTOR', 'SECRETARY', 'RECEPTIONIST') NOT NULL,
    privileges JSON,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100)
);

-- Table de liaison utilisateurs-roles (relation many-to-many)
CREATE TABLE utilisateur_roles (
    id_user BIGINT,
    id_role BIGINT,
    PRIMARY KEY (id_user, id_role),
    FOREIGN KEY (id_user) REFERENCES utilisateurs(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_role) REFERENCES roles(id_role) ON DELETE CASCADE
);

-- Table des notifications
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titre ENUM('APPOINTMENT_REMINDER', 'PAYMENT_DUE', 'NEW_MESSAGE', 'SYSTEM_UPDATE', 'EMERGENCY') NOT NULL,
    message TEXT NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    type ENUM('ALERT', 'INFO', 'WARNING', 'SUCCESS') NOT NULL,
    priorite ENUM('HIGH', 'MEDIUM', 'LOW') NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100)
);

-- Table de liaison utilisateurs-notifications (relation many-to-many)
CREATE TABLE utilisateur_notifications (
    id_user BIGINT,
    id_notification BIGINT,
    lu BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id_user, id_notification),
    FOREIGN KEY (id_user) REFERENCES utilisateurs(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_notification) REFERENCES notifications(id) ON DELETE CASCADE
);

-- =====================================================
-- Tables du personnel medical
-- =====================================================

-- Table du personnel (Staff)
CREATE TABLE staff (
    id_user BIGINT PRIMARY KEY,
    salaire DECIMAL(10,2),
    prime DECIMAL(10,2),
    date_recrutement DATE,
    solde_conge INT DEFAULT 0,
    id_cabinet BIGINT,
    FOREIGN KEY (id_user) REFERENCES utilisateurs(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_cabinet) REFERENCES cabinets_medicaux(id_cabinet) ON DELETE SET NULL
);

-- Table des medecins
CREATE TABLE medecins (
    id_user BIGINT PRIMARY KEY,
    specialite VARCHAR(100) NOT NULL,
    id_agenda BIGINT,
    FOREIGN KEY (id_user) REFERENCES staff(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_agenda) REFERENCES agendas_mensuels(id_agenda) ON DELETE SET NULL
);

-- Table des secretaires
CREATE TABLE secretaires (
    id_user BIGINT PRIMARY KEY,
    num_cnss VARCHAR(50),
    commission DECIMAL(5,2),
    FOREIGN KEY (id_user) REFERENCES staff(id_user) ON DELETE CASCADE
);

-- Table des administrateurs
CREATE TABLE admins (
    id_user BIGINT PRIMARY KEY,
    FOREIGN KEY (id_user) REFERENCES staff(id_user) ON DELETE CASCADE
);

-- =====================================================
-- Tables des cabinets medicaux
-- =====================================================

-- Table des cabinets medicaux
CREATE TABLE cabinets_medicaux (
    id_cabinet BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    logo VARCHAR(500),
    numero VARCHAR(10),
    rue VARCHAR(255),
    code_postal VARCHAR(10),
    ville VARCHAR(100),
    pays VARCHAR(100),
    complement VARCHAR(255),
    cin VARCHAR(20),
    tel1 VARCHAR(20),
    tel2 VARCHAR(20),
    site_web VARCHAR(500),
    instagram VARCHAR(100),
    facebook VARCHAR(500),
    description TEXT,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100)
);

-- Table des charges
CREATE TABLE charges (
    id_charge BIGINT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    montant DECIMAL(10,2) NOT NULL,
    date DATETIME NOT NULL,
    id_cabinet BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_cabinet) REFERENCES cabinets_medicaux(id_cabinet) ON DELETE CASCADE
);

-- Table des revenus
CREATE TABLE revenues (
    id_revenue BIGINT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    montant DECIMAL(10,2) NOT NULL,
    date DATETIME NOT NULL,
    id_cabinet BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_cabinet) REFERENCES cabinets_medicaux(id_cabinet) ON DELETE CASCADE
);

-- Table des statistiques
CREATE TABLE statistiques (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    categorie ENUM('REVENUE', 'EXPENSE', 'PATIENT_COUNT', 'APPOINTMENT_COUNT', 'TREATMENT_COUNT') NOT NULL,
    chiffre DECIMAL(15,2) NOT NULL,
    date_calcul DATE NOT NULL,
    id_cabinet BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_cabinet) REFERENCES cabinets_medicaux(id_cabinet) ON DELETE CASCADE
);

-- =====================================================
-- Tables des patients et dossiers medicaux
-- =====================================================

-- Table des patients
CREATE TABLE patients (
    id_patient BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    date_de_naissance DATE NOT NULL,
    sexe ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    adresse TEXT,
    telephone VARCHAR(20),
    assurance ENUM('CNOPS', 'CNSS', 'RAMED', 'NONE') NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100)
);

-- Table des dossiers medicaux
CREATE TABLE dossiers_medicaux (
    id_dm BIGINT PRIMARY KEY AUTO_INCREMENT,
    date_de_creation DATE NOT NULL,
    id_patient BIGINT NOT NULL UNIQUE,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_patient) REFERENCES patients(id_patient) ON DELETE CASCADE
);

-- Table des antecedents
CREATE TABLE antecedents (
    id_antecedent BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    categorie VARCHAR(100) NOT NULL,
    niveau_de_risque ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL,
    id_patient BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_patient) REFERENCES patients(id_patient) ON DELETE CASCADE
);

-- Table des factures
CREATE TABLE factures (
    id_facture BIGINT PRIMARY KEY AUTO_INCREMENT,
    totale_facture DECIMAL(10,2) NOT NULL,
    totale_paye DECIMAL(10,2) DEFAULT 0,
    reste DECIMAL(10,2) NOT NULL,
    statut ENUM('PAID', 'PENDING', 'OVERDUE') NOT NULL,
    date_facture DATETIME NOT NULL,
    id_patient BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_patient) REFERENCES patients(id_patient) ON DELETE CASCADE
);

-- Table des situations financieres
CREATE TABLE situations_financieres (
    id_sf BIGINT PRIMARY KEY AUTO_INCREMENT,
    totale_des_actes DECIMAL(10,2) NOT NULL,
    totale_paye DECIMAL(10,2) NOT NULL,
    credit DECIMAL(10,2) NOT NULL,
    statut ENUM('ACTIVE', 'CLOSED', 'ARCHIVED') NOT NULL,
    en_promo BOOLEAN DEFAULT FALSE,
    id_dm BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_dm) REFERENCES dossiers_medicaux(id_dm) ON DELETE CASCADE
);

-- =====================================================
-- Tables des consultations et interventions
-- =====================================================

-- Table des consultations
CREATE TABLE consultations (
    id_consultation BIGINT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    statut ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') NOT NULL,
    observation_medecin TEXT,
    id_dm BIGINT NOT NULL,
    id_medecin BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_dm) REFERENCES dossiers_medicaux(id_dm) ON DELETE CASCADE,
    FOREIGN KEY (id_medecin) REFERENCES medecins(id_user) ON DELETE CASCADE
);

-- Table des interventions medicales
CREATE TABLE interventions_medecin (
    id_im BIGINT PRIMARY KEY AUTO_INCREMENT,
    prix_de_patient DECIMAL(10,2) NOT NULL,
    num_dent INT,
    id_consultation BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_consultation) REFERENCES consultations(id_consultation) ON DELETE CASCADE
);

-- Table des actes medicaux
CREATE TABLE actes (
    id_acte BIGINT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(255) NOT NULL,
    categorie VARCHAR(100) NOT NULL,
    prix_de_base DECIMAL(10,2) NOT NULL,
    id_im BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_im) REFERENCES interventions_medecin(id_im) ON DELETE CASCADE
);

-- =====================================================
-- Tables des rendez-vous et agenda
-- =====================================================

-- Table des agendas mensuels
CREATE TABLE agendas_mensuels (
    id_agenda BIGINT PRIMARY KEY AUTO_INCREMENT,
    mois ENUM('JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE', 
              'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER') NOT NULL,
    jours_non_disponible JSON,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100)
);

-- Table des rendez-vous
CREATE TABLE rdv (
    id_rdv BIGINT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    heure TIME NOT NULL,
    motif TEXT NOT NULL,
    statut ENUM('CONFIRMED', 'PENDING', 'CANCELLED', 'COMPLETED') NOT NULL,
    note_medecin TEXT,
    id_patient BIGINT NOT NULL,
    id_medecin BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_patient) REFERENCES patients(id_patient) ON DELETE CASCADE,
    FOREIGN KEY (id_medecin) REFERENCES medecins(id_user) ON DELETE CASCADE
);

-- =====================================================
-- Tables des ordonnances et medicaments
-- =====================================================

-- Table des ordonnances
CREATE TABLE ordonnances (
    id_ord BIGINT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    id_dm BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_dm) REFERENCES dossiers_medicaux(id_dm) ON DELETE CASCADE
);

-- Table des medicaments
CREATE TABLE medicaments (
    id_mct BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    laboratoire VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    forme ENUM('TABLET', 'CAPSULE', 'SYRUP', 'INJECTION', 'CREAM', 'OINTMENT', 'DROPS') NOT NULL,
    remboursable BOOLEAN DEFAULT FALSE,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    description TEXT,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100)
);

-- Table des prescriptions
CREATE TABLE prescriptions (
    id_pr BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantite INT NOT NULL,
    frequence VARCHAR(100) NOT NULL,
    duree_en_jours INT NOT NULL,
    id_ord BIGINT NOT NULL,
    id_mct BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_ord) REFERENCES ordonnances(id_ord) ON DELETE CASCADE,
    FOREIGN KEY (id_mct) REFERENCES medicaments(id_mct) ON DELETE CASCADE
);

-- Table des certificats
CREATE TABLE certificats (
    id_certif BIGINT PRIMARY KEY AUTO_INCREMENT,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    duree INT NOT NULL,
    note_medecin TEXT,
    id_dm BIGINT NOT NULL,
    id_medecin BIGINT NOT NULL,
    -- Attributs BaseEntity
    id_entite BIGINT,
    date_creation DATE DEFAULT (CURRENT_DATE),
    date_derniere_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    modifie_par VARCHAR(100),
    cree_par VARCHAR(100),
    FOREIGN KEY (id_dm) REFERENCES dossiers_medicaux(id_dm) ON DELETE CASCADE,
    FOREIGN KEY (id_medecin) REFERENCES medecins(id_user) ON DELETE CASCADE
);

-- =====================================================
-- Index pour optimiser les performances
-- =====================================================

-- Index sur les colonnes frequemment utilisees pour les recherches
CREATE INDEX idx_patients_nom ON patients(nom);
CREATE INDEX idx_patients_telephone ON patients(telephone);
CREATE INDEX idx_rdv_date ON rdv(date);
CREATE INDEX idx_rdv_statut ON rdv(statut);
CREATE INDEX idx_factures_statut ON factures(statut);
CREATE INDEX idx_factures_date ON factures(date_facture);
CREATE INDEX idx_consultations_date ON consultations(date);
CREATE INDEX idx_consultations_statut ON consultations(statut);
CREATE INDEX idx_utilisateurs_email ON utilisateurs(email);
CREATE INDEX idx_utilisateurs_login ON utilisateurs(login);

-- =====================================================
-- Contraintes de verification
-- =====================================================

-- Verification que le montant restant est coherent
ALTER TABLE factures ADD CONSTRAINT chk_facture_reste 
CHECK (reste = totale_facture - totale_paye);

-- Verification que les dates sont coherentes
ALTER TABLE certificats ADD CONSTRAINT chk_certificat_dates 
CHECK (date_fin >= date_debut);

-- Verification que les montants sont positifs
ALTER TABLE factures ADD CONSTRAINT chk_facture_montants 
CHECK (totale_facture >= 0 AND totale_paye >= 0 AND reste >= 0);

ALTER TABLE charges ADD CONSTRAINT chk_charges_montant 
CHECK (montant >= 0);

ALTER TABLE revenues ADD CONSTRAINT chk_revenues_montant 
CHECK (montant >= 0);

-- =====================================================
-- Fin du schema
-- =====================================================