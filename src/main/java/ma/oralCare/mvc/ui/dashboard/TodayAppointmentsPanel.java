package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.service.modules.agenda.api.RDVDisplayModel;
import ma.oralCare.mvc.ui.palette.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel réutilisable pour afficher les rendez-vous du jour.
 */
public class TodayAppointmentsPanel extends JPanel {

    private JTable table;
    private AppointmentsTableModel model;

    public TodayAppointmentsPanel() {
        setLayout(new BorderLayout(0, 15));
        setOpaque(false);

        JLabel title = new JLabel("Prochains rendez-vous du jour");
        title.setFont(FontsPalette.BUTTON);
        title.setForeground(ColorPalette.TEXT);

        model = new AppointmentsTableModel();
        table = new JTable(model);

        table.setRowHeight(45);
        table.setFont(FontsPalette.LABEL);
        table.getTableHeader().setFont(FontsPalette.BUTTON);
        table.getTableHeader().setBackground(Color.WHITE);
        table.setGridColor(ColorPalette.CARD_BORDER);
        table.setShowVerticalLines(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(ColorPalette.CARD_BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Met à jour les rendez-vous affichés.
     *
     * @param rdvs Liste des rendez-vous à afficher
     */
    public void updateAppointments(List<RDVDisplayModel> rdvs) {
        model.setData(rdvs);
    }
}
