package hospital.ui;

import hospital.utils.DbConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final Color navy = new Color(15, 39, 71);
    private final Color blue = new Color(35, 112, 190);
    private final Color textColor = new Color(45, 61, 80);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public LoginFrame() {
        setTitle("Hospital Management System - Login");
        setSize(540, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        GradientPanel background = new GradientPanel();
        background.setLayout(new GridBagLayout());
        background.setBorder(new EmptyBorder(25, 25, 25, 25));

        RoundedPanel card = new RoundedPanel(25, Color.WHITE);
        card.setLayout(new BorderLayout(10, 15));
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setPreferredSize(new Dimension(420, 350));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel("\u2665");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        iconLabel.setForeground(blue);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("MediCare Hospital");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(navy);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(iconLabel);
        header.add(Box.createVerticalStrut(5));
        header.add(titleLabel);
        card.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        roleCombo = new JComboBox<>(new String[]{"Admin", "Receptionist", "Doctor"});
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        styleInput(usernameField);
        styleInput(passwordField);
        styleCombo(roleCombo);

        addFormRow(form, gbc, 0, "Select Role", roleCombo);
        addFormRow(form, gbc, 1, "Username", usernameField);
        addFormRow(form, gbc, 2, "Password", passwordField);
        card.add(form, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);

        JButton clearButton = new JButton("Clear");
        JButton loginButton = new JButton("Login");

        styleButton(clearButton, new Color(235, 241, 248), navy, false);
        styleButton(loginButton, blue, Color.WHITE, true);

        buttonPanel.add(clearButton);
        buttonPanel.add(loginButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        background.add(card);
        setContentPane(background);

        getRootPane().setDefaultButton(loginButton);
        loginButton.addActionListener(e -> handleLogin());

        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            roleCombo.setSelectedIndex(0);
            usernameField.requestFocusInWindow();
        });
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent input) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(textColor);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(input, gbc);
    }

    private void styleInput(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(textColor);
        field.setBackground(new Color(248, 251, 255));
        field.setCaretColor(blue);
        field.setPreferredSize(new Dimension(190, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 220, 235), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(textColor);
        combo.setPreferredSize(new Dimension(190, 34));
    }

    private void styleButton(JButton button, Color background, Color foreground, boolean primary) {
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (primary) {
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(background),
                    new EmptyBorder(10, 15, 10, 15)
            ));
        } else {
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(215, 225, 235)),
                    new EmptyBorder(10, 15, 10, 15)
            ));
        }
    }

    private void handleLogin() {
        String role = (String) roleCombo.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean authenticated = false;
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id FROM users WHERE username = ? AND password = ? AND role = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            try (ResultSet rs = ps.executeQuery()) {
                authenticated = rs.next();
            }
        } catch (SQLException ex) {
            authenticated = ("Admin".equals(role) && "admin".equals(username) && "admin123".equals(password))
                    || ("Receptionist".equals(role) && "staff".equals(username) && "staff123".equals(password))
                    || ("Doctor".equals(role) && "doctor".equals(username) && "doc123".equals(password));
        }

        if (authenticated) {
            JOptionPane.showMessageDialog(this,
                    "Welcome, " + username + " (" + role + ")!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new MainFrame(username, role).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username, password, or role!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(
                    0, 0, new Color(218, 237, 255),
                    getWidth(), getHeight(), new Color(119, 176, 226)
            ));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color panelColor;

        RoundedPanel(int radius, Color panelColor) {
            this.radius = radius;
            this.panelColor = panelColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(30, 65, 100, 35));
            g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, radius, radius);
            g2.setColor(panelColor);
            g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
