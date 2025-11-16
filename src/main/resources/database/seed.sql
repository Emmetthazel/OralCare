-- Jeu de donnees de test

-- Insertion des patients
INSERT INTO patients
(id_patient, nom, date_de_naissance, sexe, adresse, telephone, assurance, date_creation)
VALUES
    (1, 'Amal Zahra',   '1995-05-12', 'FEMALE', 'Rabat',      '0611111111', 'CNSS', '2025-10-25'),
    (2, 'Omar Badr',    '1989-09-23', 'MALE',   'Sale',       '0622222222', 'CNOPS', '2025-10-25'),
    (3, 'Nour Chafi',   '2000-02-02', 'FEMALE', 'Temara',     '0633333333', 'RAMED', '2025-10-24'),
    (4, 'Youssef Dari', '1992-11-01', 'MALE',   'Kenitra',    '0644444444', 'NONE', '2025-10-23'),
    (5, 'Hiba Zerouali','2001-03-14', 'FEMALE', 'Rabat',      '0655555555', 'CNSS', '2025-10-26'),
    (6, 'Mahdi ElMidaoui','1990-07-18', 'MALE', 'Casablanca','0666666666', 'RAMED', '2025-10-26');

-- Insertion des antecedents avec reference aux patients

-- Patient 1 : Amal (diabetique et allergique au latex)
INSERT INTO antecedents (nom, categorie, niveau_de_risque, id_patient) VALUES
('Allergie au latex', 'ALLERGIE', 'HIGH', 1),
('Diabete de type 2', 'MALADIE_CHRONIQUE', 'MEDIUM', 1);

-- Patient 2 : Omar (hypertension, tabagisme, allergie penicilline)
INSERT INTO antecedents (nom, categorie, niveau_de_risque, id_patient) VALUES
('Allergie a la penicilline', 'ALLERGIE', 'HIGH', 2),
('Hypertension arterielle', 'MALADIE_CHRONIQUE', 'MEDIUM', 2),
('Tabagisme chronique', 'HABITUDE_DE_VIE', 'MEDIUM', 2);

-- Patient 3 : Nour (grossesse, allergie penicilline)
INSERT INTO antecedents (nom, categorie, niveau_de_risque, id_patient) VALUES
('Allergie a la penicilline', 'ALLERGIE', 'HIGH', 3),
('Grossesse', 'CONTRE_INDICATION', 'MEDIUM', 3);

-- Patient 4 : Youssef (prothese valvulaire cardiaque)
INSERT INTO antecedents (nom, categorie, niveau_de_risque, id_patient) VALUES
('Prothese valvulaire cardiaque', 'ANTECEDENT_CHIRURGICAL', 'HIGH', 4);
