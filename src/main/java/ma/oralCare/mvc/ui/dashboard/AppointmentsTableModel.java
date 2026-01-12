package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.service.modules.agenda.api.RDVDisplayModel;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsTableModel extends AbstractTableModel {

    private final String[] columns = {"Heure", "Nom Patient", "Prénom Patient", "Acte", "Statut"};
    private List<RDVDisplayModel> data = new ArrayList<>();

    public void setData(List<RDVDisplayModel> rdvs) {
        this.data = rdvs;
        fireTableDataChanged(); // Force le rafraîchissement visuel du tableau
    }

    @Override
    public int getRowCount() { return data.size(); }

    @Override
    public int getColumnCount() { return columns.length; }

    @Override
    public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        if (data.isEmpty()) return null;

        RDVDisplayModel r = data.get(row);
        return switch (col) {
            case 0 -> r.getHeure();
            case 1 -> r.getPatientNom();
            case 2 -> r.getPatientPrenom();
            case 3 -> r.getTypeSoin();
            case 4 -> r.getStatut(); // Récupère le vrai statut (CONFIRMED, PENDING, etc.)
            default -> "";
        };
    }
}