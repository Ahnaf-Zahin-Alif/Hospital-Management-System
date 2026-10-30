package hospital;

import hospital.ui.LoginFrame;
import hospital.utils.DbConnection;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Initialize MySQL database tables if needed
        DbConnection.initializeTables();

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
