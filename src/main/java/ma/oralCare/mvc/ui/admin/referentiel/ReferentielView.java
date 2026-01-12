package ma.oralCare.mvc.ui.admin.referentiel;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.mvc.controllers.admin.api.SystemReferentielController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

public class ReferentielView extends JPanel {

    private final SystemReferentielController controller;
    private DefaultTableModel medicModel, antecedentModel, acteModel;

    public ReferentielView(SystemReferentielController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Gestion des Référentiels Systèmes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("💊 Médicaments", createReferencePanel("MEDIC", "Catalogue Pharmaceutique",
                new String[]{"Date", "Nom", "Laboratoire", "Type", "Forme", "Remb.", "Prix (DH)"}));
        tabbedPane.addTab("🧬 Antécédents", createReferencePanel("ANTECEDENT", "Référentiel des Antécédents",
                new String[]{"Date", "Nom", "Catégorie", "Risque"}));
        tabbedPane.addTab("🦷 Actes", createReferencePanel("ACTE", "Catalogue des Actes",
                new String[]{"Date", "Libellé", "Catégorie", "Prix de Base"}));

        add(tabbedPane, BorderLayout.CENTER);
        refreshAll();
    }

    // =========================================================================
    // ✅ GESTION DES FORMULAIRES (AJOUT & MODIF)
    // =========================================================================

    private void showMedicForm(Medicament m) {
        boolean isNew = (m.getIdEntite() == null);
        JTextField nom = new JTextField(m.getNom());
        JTextField labo = new JTextField(m.getLaboratoire());
        JTextField type = new JTextField(m.getType());

        JComboBox<FormeMedicament> forme = new JComboBox<>(FormeMedicament.values());
        forme.setSelectedItem(m.getForme() != null ? m.getForme() : FormeMedicament.TABLET);

        String prixStr = (m.getPrixUnitaire() != null) ? m.getPrixUnitaire().toString() : "";
        JTextField prix = new JTextField(prixStr);
        JCheckBox remb = new JCheckBox("Remboursable", m.getRemboursable() != null ? m.getRemboursable() : false);

        JPanel container = createMedicContainer(nom, labo, type, forme, prix, remb);
        String title = isNew ? "Ajouter Médicament" : "Modifier Médicament";

        if (JOptionPane.showConfirmDialog(this, container, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                m.setNom(nom.getText());
                m.setLaboratoire(labo.getText());
                m.setType(type.getText());
                m.setForme((FormeMedicament) forme.getSelectedItem());
                m.setRemboursable(remb.isSelected());

                String p = prix.getText().trim().replace(",", ".");
                m.setPrixUnitaire(p.isEmpty() ? BigDecimal.ZERO : new BigDecimal(p));

                controller.updateMedicament(m);
                refreshMedicaments();
            } catch (Exception e) { showError("Erreur saisie prix ou données : " + e.getMessage()); }
        }
    }

    private void showAntecedentForm(Antecedent a) {
        boolean isNew = (a.getIdEntite() == null);
        JTextField nom = new JTextField(a.getNom());
        JComboBox<CategorieAntecedent> cat = new JComboBox<>(CategorieAntecedent.values());
        cat.setSelectedItem(a.getCategorie() != null ? a.getCategorie() : CategorieAntecedent.AUTRE);

        JComboBox<NiveauDeRisque> risque = new JComboBox<>(NiveauDeRisque.values());
        risque.setSelectedItem(a.getNiveauDeRisque() != null ? a.getNiveauDeRisque() : NiveauDeRisque.LOW);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(createFieldGroup("Nom / Libellé", nom));
        container.add(createFieldGroup("Catégorie", cat));
        container.add(createFieldGroup("Niveau de Risque", risque));

        String title = isNew ? "Ajouter Antécédent" : "Modifier Antécédent";
        if (JOptionPane.showConfirmDialog(this, container, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            a.setNom(nom.getText());
            a.setCategorie((CategorieAntecedent) cat.getSelectedItem());
            a.setNiveauDeRisque((NiveauDeRisque) risque.getSelectedItem());
            controller.updateAntecedent(a);
            refreshAntecedents();
        }
    }

    private void showActeForm(Acte act) {
        boolean isNew = (act.getIdEntite() == null);
        JTextField libelle = new JTextField(act.getLibelle());
        JTextField cat = new JTextField(act.getCategorie());

        String prixStr = (act.getPrixDeBase() != null) ? act.getPrixDeBase().toString() : "";
        JTextField prix = new JTextField(prixStr);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(createFieldGroup("Libellé de l'acte", libelle));
        container.add(createFieldGroup("Catégorie", cat));
        container.add(createFieldGroup("Prix de Base (DH)", prix));

        String title = isNew ? "Ajouter Acte" : "Modifier Acte";
        if (JOptionPane.showConfirmDialog(this, container, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                act.setLibelle(libelle.getText());
                act.setCategorie(cat.getText());
                String p = prix.getText().trim().replace(",", ".");
                act.setPrixDeBase(p.isEmpty() ? BigDecimal.ZERO : new BigDecimal(p));
                controller.updateActe(act);
                refreshActes();
            } catch (Exception e) { showError("Prix invalide."); }
        }
    }

    // =========================================================================
    // ✅ LOGIQUE TABLEAUX ET BOUTONS (CORRIGÉ POUR SUPPRESSION RÉELLE)
    // =========================================================================

    private JPanel createReferencePanel(String type, String title, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        if (type.equals("MEDIC")) medicModel = model;
        else if (type.equals("ANTECEDENT")) antecedentModel = model;
        else acteModel = model;

        JTable table = new JTable(model);
        table.setRowHeight(30);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnAdd = new JButton("➕ Ajouter");
        JButton btnEdit = new JButton("✏️ Modifier");
        JButton btnDelete = new JButton("🗑️ Supprimer");

        btnAdd.addActionListener(e -> {
            if(type.equals("MEDIC")) showMedicForm(new Medicament());
            else if(type.equals("ANTECEDENT")) showAntecedentForm(new Antecedent());
            else showActeForm(new Acte());
        });

        btnEdit.addActionListener(e -> handleEditAction(type, table));

        // ✅ CORRECTION : Suppression réelle en cascade
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                showError("Veuillez sélectionner un élément.");
                return;
            }

            if (JOptionPane.showConfirmDialog(this, "Supprimer cet élément définitivement ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String key = (String) table.getValueAt(row, 1);
                Long idToDelete = null;

                // Récupération de l'ID via le controller
                if (type.equals("MEDIC")) {
                    idToDelete = controller.loadMedicaments().stream().filter(m -> m.getNom().equals(key)).findFirst().map(m -> m.getIdEntite()).orElse(null);
                } else if (type.equals("ANTECEDENT")) {
                    idToDelete = controller.loadAntecedents().stream().filter(a -> a.getNom().equals(key)).findFirst().map(a -> a.getIdEntite()).orElse(null);
                } else if (type.equals("ACTE")) {
                    idToDelete = controller.loadActes().stream().filter(act -> act.getLibelle().equals(key)).findFirst().map(act -> act.getIdEntite()).orElse(null);
                }

                if (idToDelete != null) {
                    controller.deleteEntity(type, idToDelete);
                    refreshAll(); // On rafraîchit tout pour être sûr de l'état de la BDD
                }
            }
        });

        toolbar.add(btnAdd); toolbar.add(btnEdit); toolbar.add(btnDelete);
        panel.add(new JLabel("<html><b>" + title + "</b></html>"), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(toolbar, BorderLayout.SOUTH);
        return panel;
    }

    private void handleEditAction(String type, JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Sélectionnez une ligne."); return; }
        String key = (String) table.getValueAt(row, 1);

        if (type.equals("MEDIC")) {
            controller.loadMedicaments().stream().filter(m -> m.getNom().equals(key)).findFirst().ifPresent(this::showMedicForm);
        } else if (type.equals("ANTECEDENT")) {
            controller.loadAntecedents().stream().filter(a -> a.getNom().equals(key)).findFirst().ifPresent(this::showAntecedentForm);
        } else {
            controller.loadActes().stream().filter(a -> a.getLibelle().equals(key)).findFirst().ifPresent(this::showActeForm);
        }
    }

    // =========================================================================
    // ✅ RAFRAÎCHISSEMENT DES DONNÉES
    // =========================================================================

    private void refreshMedicaments() {
        medicModel.setRowCount(0);
        controller.loadMedicaments().forEach(m -> medicModel.addRow(new Object[]{
                m.getDateCreation(), m.getNom(), m.getLaboratoire(), m.getType(),
                m.getForme() != null ? m.getForme().getLibelle() : "-",
                m.getRemboursable() ? "Oui" : "Non", m.getPrixUnitaire() + " DH"}));
    }

    private void refreshAntecedents() {
        antecedentModel.setRowCount(0);
        controller.loadAntecedents().forEach(a -> antecedentModel.addRow(new Object[]{
                a.getDateCreation(), a.getNom(),
                a.getCategorie() != null ? a.getCategorie().name() : "-",
                a.getNiveauDeRisque() != null ? a.getNiveauDeRisque().getLibelle() : "-"}));
    }

    private void refreshActes() {
        acteModel.setRowCount(0);
        controller.loadActes().forEach(a -> acteModel.addRow(new Object[]{
                a.getDateCreation(), a.getLibelle(), a.getCategorie(), a.getPrixDeBase() + " DH"}));
    }

    // --- Helpers UI ---
    private JPanel createMedicContainer(JTextField n, JTextField l, JTextField t, JComboBox f, JTextField p, JCheckBox r) {
        JPanel c = new JPanel(); c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(createFieldGroup("Nom Commercial", n));
        c.add(createFieldGroup("Laboratoire", l));
        c.add(createFieldGroup("Type / Molécule", t));
        c.add(createFieldGroup("Forme Pharmaceutique", f));
        c.add(createFieldGroup("Prix Unitaire (DH)", p));
        c.add(r); return c;
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel group = new JPanel(new BorderLayout(0, 5));
        group.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        group.add(label, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        group.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return group;
    }

    private void refreshAll() { refreshMedicaments(); refreshAntecedents(); refreshActes(); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE); }
}