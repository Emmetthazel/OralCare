package ma.oralCare.mvc.ui.dashboard.Dialog;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

public class AgendaSemaine extends JFrame {
    private LocalDate dateReference = LocalDate.now();
    private JLayeredPane layeredPane;
    private JPanel grilleHeures;
    private JLabel labelDateRange;
    private JComboBox<String> comboMois;
    private boolean estEnTrainDeMettreAJour = false;

    private final int HAUTEUR_HEURE = 60; // 1 heure = 60 pixels (Précision : 1 min = 1 px)
    private final Color BLEU_GOOGLE = new Color(26, 115, 232);
    private final Color BLEU_EVENT = new Color(26, 115, 232, 200);

    public AgendaSemaine() {
        setTitle("Agenda Professionnel - Style Google");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        creerHeader();

        // Le LayeredPane permet de superposer la grille et les blocs d'événements
        layeredPane = new JLayeredPane();
        JScrollPane scroll = new JScrollPane(layeredPane);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        mettreAJourAffichage();
    }

    private void creerHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        navigation.setOpaque(false);

        // Navigation "<"
        JLabel btnPrev = creerBoutonNav("<");
        btnPrev.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dateReference = dateReference.minusWeeks(1);
                mettreAJourAffichage();
            }
        });

        // Navigation ">"
        JLabel btnNext = creerBoutonNav(">");
        btnNext.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dateReference = dateReference.plusWeeks(1);
                mettreAJourAffichage();
            }
        });

        labelDateRange = new JLabel();
        labelDateRange.setFont(new Font("SansSerif", Font.BOLD, 18));

        comboMois = new JComboBox<>(new String[]{"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"});
        comboMois.addActionListener(e -> {
            if (!estEnTrainDeMettreAJour) {
                dateReference = LocalDate.of(dateReference.getYear(), comboMois.getSelectedIndex() + 1, 1);
                mettreAJourAffichage();
            }
        });

        navigation.add(btnPrev);
        navigation.add(btnNext);
        navigation.add(comboMois);
        navigation.add(labelDateRange);

        header.add(navigation, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
    }

    private JLabel creerBoutonNav(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setFont(new Font("Monospaced", Font.BOLD, 22));
        l.setPreferredSize(new Dimension(30, 30));
        l.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return l;
    }

    private void mettreAJourAffichage() {
        estEnTrainDeMettreAJour = true;
        layeredPane.removeAll();

        // 1. Calcul de la semaine (Lundi au Dimanche)
        LocalDate lundi = dateReference.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate dimanche = lundi.plusDays(6);

        // Formatage du titre : "5 - 11 Janvier 2026"
        String range = lundi.getDayOfMonth() + " - " + dimanche.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
        labelDateRange.setText(range);
        comboMois.setSelectedIndex(lundi.getMonthValue() - 1);

        // 2. Création du container de la grille
        int largeurTotale = 1000;
        int hauteurTotale = 24 * HAUTEUR_HEURE + 40; // +40 pour le header des jours

        JPanel containerGrille = new JPanel(new BorderLayout());
        containerGrille.setBounds(0, 0, largeurTotale, hauteurTotale);
        containerGrille.setBackground(Color.WHITE);

        // Header des jours
        JPanel joursHeader = new JPanel(new GridLayout(1, 8));
        joursHeader.setPreferredSize(new Dimension(largeurTotale, 40));
        joursHeader.setBackground(Color.WHITE);
        joursHeader.add(new JLabel("")); // Coin vide

        for (int i = 0; i < 7; i++) {
            LocalDate d = lundi.plusDays(i);
            JLabel jl = new JLabel(d.format(DateTimeFormatter.ofPattern("EEE d", Locale.FRENCH)).toUpperCase(), SwingConstants.CENTER);
            if(d.equals(LocalDate.now())) jl.setForeground(BLEU_GOOGLE);
            jl.setBorder(new MatteBorder(0, 1, 1, 0, Color.LIGHT_GRAY));
            joursHeader.add(jl);
        }
        containerGrille.add(joursHeader, BorderLayout.NORTH);

        // Grille des heures
        grilleHeures = new JPanel(new GridLayout(24, 8));
        grilleHeures.setBackground(Color.WHITE);

        for (int h = 0; h < 24; h++) {
            JLabel lblH = new JLabel(h + ":00 ", SwingConstants.RIGHT);
            lblH.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblH.setForeground(Color.GRAY);
            lblH.setBorder(new MatteBorder(1, 0, 0, 0, new Color(240, 240, 240)));
            grilleHeures.add(lblH);

            for (int j = 0; j < 7; j++) {
                JPanel cellule = new JPanel(null);
                cellule.setBackground(Color.WHITE);
                cellule.setBorder(new MatteBorder(1, 1, 0, 0, new Color(240, 240, 240)));

                final int jourIndex = j;
                final int heureH = h;

                cellule.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        String input = JOptionPane.showInputDialog("Durée en minutes (ex: 40, 130) :");
                        if (input != null && !input.isEmpty()) {
                            try {
                                int minutes = Integer.parseInt(input);
                                ajouterEvenementGraphique(jourIndex, heureH, minutes);
                            } catch (Exception ex) {}
                        }
                    }
                });
                grilleHeures.add(cellule);
            }
        }
        containerGrille.add(grilleHeures, BorderLayout.CENTER);

        layeredPane.setPreferredSize(new Dimension(largeurTotale, hauteurTotale));
        layeredPane.add(containerGrille, JLayeredPane.DEFAULT_LAYER);

        layeredPane.revalidate();
        layeredPane.repaint();
        estEnTrainDeMettreAJour = false;
    }

    private void ajouterEvenementGraphique(int jourIdx, int heureIdx, int minutes) {
        // Calcul des dimensions
        int largeurCol = grilleHeures.getWidth() / 8;
        int x = (jourIdx + 1) * largeurCol;
        int y = (heureIdx * HAUTEUR_HEURE) + 40; // +40 à cause du header des jours

        int hauteurPx = minutes; // 1 min = 1 px

        JPanel event = new JPanel(new BorderLayout());
        event.setBackground(BLEU_EVENT);
        event.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JLabel lbl = new JLabel(minutes + " min", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        event.add(lbl);

        // On définit la position exacte. Si minutes > 60, ça débordera naturellement
        // sur les cellules du dessous car il est sur une couche supérieure.
        event.setBounds(x + 2, y + 1, largeurCol - 4, hauteurPx - 2);

        layeredPane.add(event, JLayeredPane.PALETTE_LAYER);
        layeredPane.repaint();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new AgendaSemaine().setVisible(true));
    }
}