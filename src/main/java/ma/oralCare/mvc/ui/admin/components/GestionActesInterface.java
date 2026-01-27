package ma.oralCare.mvc.ui.admin.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GestionActesInterface extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private List<ActeData> actesData;

    public GestionActesInterface() {
        setTitle("Type d'actes: 14");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Initialiser les données
        initializeData();

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Pagination Panel
        JPanel paginationPanel = createPaginationPanel();
        mainPanel.add(paginationPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private void initializeData() {
        actesData = new ArrayList<>();
        actesData.add(new ActeData("45", "acte", "1500 Dh", new Color(220, 53, 69)));
        actesData.add(new ActeData("D734", "Dégagement chirurgical de la couronne d'une dent permanente incluse", "525 Dh", new Color(232, 62, 140)));
        actesData.add(new ActeData("D739", "Exérèse chirurgicale d'un kyste de petit volume par voie alvéolaire élargie", "262.5 Dh", new Color(23, 162, 184)));
        actesData.add(new ActeData("D740", "Exérèse chirurgicale d'un kyste étendu aux apex de deux dents et nécessitant une trépanation osseuse", "525 Dh", new Color(0, 123, 255)));
        actesData.add(new ActeData("D741", "Exérèse chirurgicale d'un kyste étendu à un segment important du maxillaire", "875 Dh", new Color(220, 53, 69)));
        actesData.add(new ActeData("D742", "Gingivectomie partielle", "87.5 Dh", new Color(40, 167, 69)));
        actesData.add(new ActeData("D743", "Gingivectomie étendue à un sextant : (de canine à canine, de prémolaire à dent de sagesse)", "350 Dh", new Color(23, 162, 184)));
        actesData.add(new ActeData("D744", "Traitement d'une hémorragie post-opératoire dans une séance autre que celle de l'intervention", "175 Dh", new Color(111, 66, 193)));
        actesData.add(new ActeData("D807", "Appareillage de contention ou de réduction pré-opératoire et post- opératoire du maxillaire ou de la mandibule (résection chirurgicale ou greffe)", "1625 Dh", new Color(111, 66, 193)));
        actesData.add(new ActeData("---", "Gingivectomie partielle avec laser", "0 Dh", new Color(144, 238, 144)));
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(Color.WHITE);

        // Info text
        JLabel infoLabel = new JLabel("ℹ Vous pouvez ajouter des types existants ou ajouter des types manuellement");
        infoLabel.setForeground(new Color(13, 110, 253));
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel searchPlaceholder = new JLabel("Rechercher");
        searchPlaceholder.setForeground(Color.GRAY);

        categoryCombo = new JComboBox<>(new String[]{"Catégorie", "Chirurgie", "Consultation", "Radiologie"});
        categoryCombo.setPreferredSize(new Dimension(180, 40));
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        searchPanel.add(searchField);
        searchPanel.add(categoryCombo);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton newTypeBtn = createStyledButton("+ Nouveau type", new Color(13, 110, 253));
        JButton addExistingBtn = createStyledButton("⊕ Ajouter types existants", new Color(13, 110, 253));

        buttonsPanel.add(newTypeBtn);
        buttonsPanel.add(addExistingBtn);

        // Combine panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(infoLabel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(searchPanel, BorderLayout.WEST);
        middlePanel.add(buttonsPanel, BorderLayout.EAST);

        topPanel.add(middlePanel, BorderLayout.CENTER);

        headerPanel.add(topPanel);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        // Créer le modèle de table
        String[] columnNames = {"Code", "Acte", "Prix", "Couleur", "", ""};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 4; // Seulement les colonnes d'action sont éditables
            }
        };

        // Remplir la table
        for (ActeData acte : actesData) {
            tableModel.addRow(new Object[]{acte.code, acte.name, acte.price, acte.color, "✏", "🗑"});
        }

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setGridColor(new Color(222, 226, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Personnaliser le renderer
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        // Configurer les colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(600);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(50);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        paginationPanel.setBackground(Color.WHITE);

        JLabel countLabel = new JLabel("Nombre de types: 14");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton firstBtn = createPaginationButton("⟨⟨");
        JButton prevBtn = createPaginationButton("⟨");
        JButton page1Btn = createPaginationButton("1");
        page1Btn.setBackground(new Color(13, 110, 253));
        page1Btn.setForeground(Color.WHITE);
        JButton page2Btn = createPaginationButton("2");
        JButton nextBtn = createPaginationButton("⟩");
        JButton lastBtn = createPaginationButton("⟩⟩");

        JComboBox<String> pageSizeCombo = new JComboBox<>(new String[]{"10", "20", "50", "100"});
        pageSizeCombo.setPreferredSize(new Dimension(80, 35));

        paginationPanel.add(countLabel);
        paginationPanel.add(firstBtn);
        paginationPanel.add(prevBtn);
        paginationPanel.add(page1Btn);
        paginationPanel.add(page2Btn);
        paginationPanel.add(nextBtn);
        paginationPanel.add(lastBtn);
        paginationPanel.add(pageSizeCombo);

        return paginationPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(text.length() * 10 + 20, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createPaginationButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(45, 35));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Classe interne pour les données
    class ActeData {
        String code;
        String name;
        String price;
        Color color;

        ActeData(String code, String name, String price, Color color) {
            this.code = code;
            this.name = name;
            this.price = price;
            this.color = color;
        }
    }

    // Custom cell renderer
    class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 3 && value instanceof Color) {
                JPanel colorPanel = new JPanel();
                colorPanel.setBackground((Color) value);
                colorPanel.setPreferredSize(new Dimension(30, 30));
                colorPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
                wrapper.setBackground(Color.WHITE);
                wrapper.add(colorPanel);
                return wrapper;
            }

            if (column == 4 || column == 5) {
                JButton actionBtn = new JButton(value.toString());
                actionBtn.setBackground(new Color(248, 249, 250));
                actionBtn.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
                actionBtn.setFocusPainted(false);
                actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return actionBtn;
            }

            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            if (column == 2) {
                setForeground(new Color(13, 110, 253));
                setFont(new Font("Arial", Font.BOLD, 13));
            } else {
                setForeground(Color.BLACK);
            }

            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GestionActesInterface().setVisible(true);
        });
    }
}