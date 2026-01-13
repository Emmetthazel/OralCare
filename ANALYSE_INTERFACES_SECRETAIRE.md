# Analyse des Interfaces Secrétaire - Selon les Diagrammes de Cas d'Utilisation

## 📋 Résumé des Cas d'Utilisation du Secrétaire

D'après les diagrammes de cas d'utilisation fournis, voici les fonctionnalités requises :

### 1. ✅ Gérer patients
- Consulter liste patients
- Ajouter patient
- Modifier patient
- Supprimer patient
- Consulter patient
- Affecter Antécédent → Gérer Antécédents (Ajouter, Modifier, Consulter, Supprimer)

### 2. ✅ Gérer Dossier Médical
- Consulter dossier Médical
- Créer dossier Médical

### 3. ✅ Gérer la caisse
- Consulter statistiques de caisse
- Exporter rapport

### 4. ❌ Consulter Situation Financière
- Consulter SF d'un patient
- Lister les SF de tous les patients

### 5. ✅ Gérer rendez-vous patients
- Gérer liste d'attente
- Annuler rdv
- Consulter rdv
- Consulter historiques RDV
- Planifier rdv
- Confirmer rdv
- Modifier rdv
- Consulter planning
- Envoyer email au patient

### 6. ✅ Gérer l'agenda médecin
- Consulter l'agenda
- Supprimer agenda
- Créer agenda Mensuel
- Marquer jours indisponibles
- Modifier plages horaires

### 7. ✅ Gérer factures
- Consulter facture
- Annuler Facture
- Générer Facture
- Modifier Facture
- Imprimer facture
- Enregistrer paiement

### 8. ✅ Gérer dashboard
- Personnaliser Dashboard (Masquer widget, Ajouter widget)
- Consulter dashboard
- Consulter La file d'attente
- Consulter notifications (Marquer notification lue)
- Consulter les derniers alertes
- Consulter alertes

---

## 🔍 État Actuel des Interfaces Secrétaire

### Interfaces Existantes :
1. ✅ **DashboardSecretairePanel** - Dashboard
2. ✅ **PatientManagementPanel** - Gestion des patients
3. ✅ **DossierMedicalSecretairePanel** - Gestion des dossiers médicaux
4. ✅ **VisualAgendaPanel** - Visualisation de l'agenda
5. ✅ **AgendaManagementPanel** - Gestion des RDV
6. ✅ **CaisseFacturationPanel** - Gestion de la caisse et facturation

---

## 🚀 Interfaces à Créer

### 1. **SituationFinanciereSecretairePanel**
**Fonctionnalités :**
- Consulter la situation financière d'un patient spécifique
- Lister toutes les situations financières de tous les patients
- Filtrer par patient, statut (PAID, PENDING, etc.)
- Afficher : Total des actes, Total payé, Crédit restant
- Voir les factures associées à chaque situation financière

**Localisation :** `src/main/java/ma/oralCare/mvc/ui1/secretaire/SituationFinanciereSecretairePanel.java`

### 2. **FileAttentePanel** (ou intégrer dans Dashboard)
**Fonctionnalités :**
- Afficher la liste d'attente des patients
- Gérer l'ordre d'arrivée
- Notifier le médecin
- Déplacer les patients dans la liste

**Localisation :** `src/main/java/ma/oralCare/mvc/ui1/secretaire/FileAttentePanel.java`
**Alternative :** Intégrer dans DashboardSecretairePanel comme widget

### 3. **NotificationsPanel** (ou widget dans Dashboard)
**Fonctionnalités :**
- Afficher les notifications
- Marquer comme lue
- Filtrer par type et priorité
- Afficher les alertes

**Localisation :** `src/main/java/ma/oralCare/mvc/ui1/secretaire/NotificationsPanel.java`
**Alternative :** Widget dans DashboardSecretairePanel

### 4. **ExportRapportDialog** (ou intégrer dans CaisseFacturationPanel)
**Fonctionnalités :**
- Exporter les statistiques de caisse
- Choix du format (PDF, Excel)
- Période à exporter
- Prévisualisation

**Localisation :** `src/main/java/ma/oralCare/mvc/ui1/secretaire/dialog/ExportRapportDialog.java`
**Alternative :** Bouton dans CaisseFacturationPanel

---

## 🔧 Modifications à Apporter aux Interfaces Existantes

### 1. **PatientManagementPanel**
**À ajouter/compléter :**
- ✅ Bouton "Affecter Antécédent" qui ouvre un dialogue
- ✅ Gestion complète des antécédents (CRUD) via AntecedentDialog
- ✅ Vérifier que toutes les opérations CRUD sont présentes (Modifier, Supprimer, Consulter)

### 2. **DashboardSecretairePanel**
**À ajouter/compléter :**
- ✅ Widget "File d'attente" (ou lien vers FileAttentePanel)
- ✅ Widget "Notifications" (ou lien vers NotificationsPanel)
- ✅ Widget "Dernières alertes"
- ✅ Personnalisation du dashboard (masquer/afficher widgets)
- ✅ Vérifier l'affichage des statistiques de caisse

### 3. **CaisseFacturationPanel**
**À ajouter/compléter :**
- ✅ Bouton "Exporter rapport" avec dialogue d'export
- ✅ Vérifier l'affichage des statistiques de caisse
- ✅ Vérifier la gestion complète des factures (CRUD)

### 4. **AgendaManagementPanel**
**À ajouter/compléter :**
- ✅ Gestion de la liste d'attente
- ✅ Fonctionnalité "Envoyer email au patient" (bouton dans les détails du RDV)
- ✅ Vérifier toutes les opérations sur les RDV (Annuler, Confirmer, Modifier, Consulter historique)

### 5. **VisualAgendaPanel** / **AgendaManagementPanel**
**À ajouter/compléter :**
- ✅ Créer agenda Mensuel
- ✅ Marquer jours indisponibles
- ✅ Modifier plages horaires
- ✅ Supprimer agenda
- (Vérifier si AgendaMensuelDialog couvre ces fonctionnalités)

### 6. **DossierMedicalSecretairePanel**
**À vérifier :**
- ✅ Vérifier que la création de dossier médical est possible
- ✅ Vérifier que la consultation est complète
- ✅ Vérifier l'accès aux informations financières du patient

---

## 📊 Architecture Recommandée

```
src/main/java/ma/oralCare/mvc/ui1/secretaire/
├── DashboardSecretairePanel.java (✅ Existe - À modifier)
├── PatientManagementPanel.java (✅ Existe - À compléter)
├── DossierMedicalSecretairePanel.java (✅ Existe - À vérifier)
├── VisualAgendaPanel.java (✅ Existe - À vérifier)
├── AgendaManagementPanel.java (✅ Existe - À compléter)
├── CaisseFacturationPanel.java (✅ Existe - À compléter)
├── SituationFinanciereSecretairePanel.java (❌ À créer)
├── FileAttentePanel.java (❌ À créer ou widget)
├── NotificationsPanel.java (❌ À créer ou widget)
└── dialog/
    ├── PatientDialog.java (✅ Existe)
    ├── AntecedentDialog.java (✅ Existe)
    ├── RendezVousDialog.java (✅ Existe)
    ├── AgendaMensuelDialog.java (✅ Existe)
    ├── DossierMedicalDialog.java (✅ Existe)
    └── ExportRapportDialog.java (❌ À créer)
```

---

## 🎯 Priorités d'Implémentation

### Priorité 1 (Fonctionnalités manquantes critiques)
1. **SituationFinanciereSecretairePanel** - Nécessaire pour la gestion financière
2. **Export Rapport** - Important pour les rapports de caisse

### Priorité 2 (Améliorations UX)
3. **File d'attente** - Amélioration du workflow
4. **Notifications/Alertes** - Meilleure visibilité

### Priorité 3 (Compléments)
5. **Personnalisation Dashboard** - Optionnel mais utile
6. **Envoi d'email** - Fonctionnalité avancée

---

## ✅ Checklist de Validation

Pour chaque interface, vérifier :
- [ ] Toutes les fonctionnalités du cas d'utilisation sont présentes
- [ ] Les opérations CRUD sont complètes
- [ ] L'interface est cohérente avec le reste de l'application
- [ ] Les controllers/services sont appelés correctement
- [ ] Aucune donnée mock/hardcodée
- [ ] Les erreurs sont gérées proprement
- [ ] L'interface est responsive
