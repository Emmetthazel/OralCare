package ma.oralCare.mvc.ui.palette;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class InputPalette {
    private static final Border LINE_BORDER = BorderFactory.createLineBorder(ColorPalette.PRIMARY, 1, true);
    // Ajoute 5px de marge interne à gauche et à droite
    private static final Border PADDING = BorderFactory.createEmptyBorder(0, 7, 0, 7);
    private static final Border FIELD_BORDER = BorderFactory.createCompoundBorder(LINE_BORDER, PADDING);

    private static void applyStyle(JTextField field) {
        field.setFont(FontsPalette.INPUT);
        field.setBorder(FIELD_BORDER);
        field.setPreferredSize(new Dimension(200, 35));
    }

    public static JTextField text() {
        JTextField field = new JTextField();
        applyStyle(field);
        return field;
    }

    public static JPasswordField password() {
        JPasswordField field = new JPasswordField();
        applyStyle(field);
        return field;
    }
}
