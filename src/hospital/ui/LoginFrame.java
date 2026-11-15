package hospital.ui;

import hospital.utils.DbConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

        roleCombo = new JComboBox<>(
                new String[]{"Admin", "Receptionist", "Doctor"});

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

        JButton clearButton = createStyledButton("Clear", new Color(235, 241, 248), navy);
        JButton loginButton = createStyledButton("Login", blue, Color.WHITE);
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

  
    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            JComponent input) {

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
                BorderFactory.createLineBorder(
                        new Color(205, 220, 235), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
    }

    
    private void styleCombo(JComboBox<String> combo) {

        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(textColor);
        combo.setPreferredSize(new Dimension(190, 34));
    }

    
    private JButton createStyledButton(String text, Color bg, Color fg) {
        Color hover = bg.equals(blue) ? new Color(28, 95, 165) : bg.darker();
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(225, 230, 238));
                } else if (getModel().isPressed()) {
                    g2.setColor(hover.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hover);
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                if (bg.getRed() > 220 && bg.getGreen() > 220 && bg.getBlue() > 220) {
                    g2.setColor(new Color(205, 218, 230));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return btn;
    }

   
    private void handleLogin() {

        String role = (String) roleCombo.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

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

        } catch (Exception ex) {

            if ("Admin".equals(role)
                    && "admin".equals(username)
                    && "admin123".equals(password)) {

                authenticated = true;

            } else if ("Receptionist".equals(role)
                    && "staff".equals(username)
                    && "staff123".equals(password)) {

                authenticated = true;

            } else if ("Doctor".equals(role)
                    && "doctor".equals(username)
                    && "doc123".equals(password)) {

                authenticated = true;
            }
        }

        if (authenticated) {

           
            showLoginSuccessDialog(username, role);

           
            dispose();

            new MainFrame(username, role).setVisible(true);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username, password, or role!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );

            passwordField.setText("");
        }
    }

    private void showLoginSuccessDialog(String username, String role) {

        JDialog dialog = new JDialog(
                this, "Login Successful", true);

        dialog.setSize(400, 330);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE);

        Color navy = new Color(15, 39, 71);
        Color blue = new Color(35, 112, 190);

        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(
                new EmptyBorder(20, 25, 20, 25));

        // Success Icon
        JLabel icon = new JLabel(
                "\u2714", SwingConstants.CENTER);

        icon.setFont(new Font("SansSerif", Font.BOLD, 30));
        icon.setForeground(Color.WHITE);
        icon.setOpaque(true);
        icon.setBackground(blue);
        icon.setPreferredSize(new Dimension(60, 60));

        JPanel iconPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER));

        iconPanel.setBackground(Color.WHITE);
        iconPanel.add(icon);

        // Heading
        JLabel title = new JLabel(
                "Login Successful!", SwingConstants.CENTER);

        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(navy);

        // Welcome Message
        JLabel welcome = new JLabel(
                "Welcome, " + username + "!",
                SwingConstants.CENTER);

        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        welcome.setForeground(new Color(65, 80, 100));

        // Role Message
        JLabel message = new JLabel(
                "You have successfully logged in as " + role + ".",
                SwingConstants.CENTER);

        message.setFont(new Font("SansSerif", Font.PLAIN, 12));
        message.setForeground(Color.GRAY);

        // Text Panel
        JPanel textPanel = new JPanel();

        textPanel.setBackground(Color.WHITE);
        textPanel.setLayout(
                new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(iconPanel);
        textPanel.add(Box.createVerticalStrut(12));
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(welcome);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(message);

        
        JButton continueButton = createStyledButton("Continue", blue, Color.WHITE);
        continueButton.setPreferredSize(new Dimension(150, 42));
        continueButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER));

        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(continueButton);

      
        mainPanel.add(textPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);

        dialog.getRootPane().setDefaultButton(continueButton);

       
        dialog.setVisible(true);
    }

   

    private static class GradientPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g2.setPaint(new GradientPaint(
                    0, 0, new Color(218, 237, 255),
                    getWidth(), getHeight(),
                    new Color(119, 176, 226)
            ));

            g2.fillRect(
                    0, 0, getWidth(), getHeight());

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

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

           
            g2.setColor(new Color(30, 65, 100, 35));

            g2.fillRoundRect(
                    4, 6,
                    getWidth() - 8,
                    getHeight() - 8,
                    radius, radius);

            
            g2.setColor(panelColor);

            g2.fillRoundRect(
                    0, 0,
                    getWidth() - 8,
                    getHeight() - 8,
                    radius, radius);

            g2.dispose();

            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());

        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(
                () -> new LoginFrame().setVisible(true));
    }
}