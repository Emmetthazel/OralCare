package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class CertificatPanel extends JPanel {

    private JTable tableCertificats;
    private DefaultTableModel model;

    // 🔍 Barre de recherche unique
    private JTextField txtSearch;

    private JLabel lblDate, lblMotif, lblDuree;
    private JTextArea areaNotes;

    public CertificatPanel() {
        setLayout(new BorderLayout(10,10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10,10,10,10));

        add(createSearchBar(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        enableSearchFilter();   // Activation du filtrage
    }

    // ================= UI =================

    private JPanel createSearchBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        JLabel icon = new JLabel("🔍");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 16));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(0, 35));
        txtSearch.setToolTipText("Rechercher par date, consultation ou durée...");

        p.add(icon, BorderLayout.WEST);
        p.add(txtSearch, BorderLayout.CENTER);

        return p;
    }

    private JSplitPane createMainContent() {

        model = new DefaultTableModel(
                new String[]{"Date", "Consultation", "Durée"}, 0
        );

        tableCertificats = new JTable(model);
        JScrollPane left = new JScrollPane(tableCertificats);
        left.setBorder(new TitledBorder("Certificats du patient"));

        JPanel right = new JPanel(new BorderLayout(8,8));
        right.setBorder(new TitledBorder("Détails du certificat"));

        JPanel info = new JPanel(new GridLayout(3,2,8,8));
        info.setOpaque(false);

        lblDate = new JLabel("-");
        lblMotif = new JLabel("-");
        lblDuree = new JLabel("-");

        info.add(new JLabel("Date émission :"));
        info.add(lblDate);
        info.add(new JLabel("Consultation :"));
        info.add(lblMotif);
        info.add(new JLabel("Durée :"));
        info.add(lblDuree);

        areaNotes = new JTextArea(5,20);
        areaNotes.setEditable(false);
        JScrollPane notes = new JScrollPane(areaNotes);
        notes.setBorder(new TitledBorder("Notes du médecin"));

        right.add(info, BorderLayout.NORTH);
        right.add(notes, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(380);

        tableCertificats.getSelectionModel().addListSelectionListener(e -> {
            int row = tableCertificats.getSelectedRow();
            if(row >= 0){
                int modelRow = tableCertificats.convertRowIndexToModel(row);
                lblDate.setText(model.getValueAt(modelRow,0).toString());
                lblMotif.setText(model.getValueAt(modelRow,1).toString());
                lblDuree.setText(model.getValueAt(modelRow,2).toString());
                // Les notes seront récupérées depuis la base de données via le controller/service
                areaNotes.setText("");
            }
        });

        return split;
    }

    // ================= SEARCH FILTER =================

    private void enableSearchFilter() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tableCertificats.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
    }

}
