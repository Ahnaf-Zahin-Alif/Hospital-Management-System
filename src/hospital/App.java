package hospital;

import hospital.ui.LoginFrame;
import hospital.utils.DatabaseSetup;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        DatabaseSetup.initialize();

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}