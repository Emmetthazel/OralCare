package ma.oralCare.mvc.ui1.secretaire.dialog;

import javax.swing.*;
import java.awt.*;

public class ConfirmationDialog {
    public static boolean confirm(Component parent, String message) {
        int response = JOptionPane.showConfirmDialog(
                parent,
                message,
                "Confirmation requise",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return response == JOptionPane.YES_OPTION;
    }
}