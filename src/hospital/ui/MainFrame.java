package hospital.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    private final Color navy = new Color(15, 39, 71);
    private final Color navHover = new Color(28, 58, 96);
    private final Color navActive = new Color(35, 112, 190);
    private final Color headerBg = Color.WHITE;
    private final Color contentBg = new Color(244, 247, 251);
    private final Color textDark = new Color(25, 35, 50);
    private final Color textMuted = new Color(110, 125, 140);
    private final Color borderLight = new Color(225, 232, 240);

    private final String currentUser;
    private final String currentRole;

    private CardLayout cardLayout;
    private JPanel contentCards;
    private JLabel pageTitleLabel;
    private JLabel pageSubtitleLabel;

    private JButton btnPatients;
    private JButton btnDoctors;
    private JButton btnAppointments;
    private JButton activeButton;

    public MainFrame() {
        this("admin", "Admin");
    }

    public MainFrame(String username, String role) {
        this.currentUser = (username != null && !username.isEmpty()) ? username : "admin";
        this.currentRole = (role != null && !role.isEmpty()) ? role : "Admin";

        initUI();
    }

    private void initUI() {
        setTitle("MediCare Hospital Management System");
        setSize(1150, 720);
        setMinimumSize(new Dimension(980, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(contentBg);
        root.add(createSidebar(), BorderLayout.WEST);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.add(createHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(contentBg);
        contentCards.setBorder(new EmptyBorder(15, 20, 15, 20));

        contentCards.add(new PatientPanel(), "PATIENTS");
        contentCards.add(new DoctorPanel(), "DOCTORS");
        contentCards.add(new AppointmentPanel(), "APPOINTMENTS");

        mainContainer.add(contentCards, BorderLayout.CENTER);
        root.add(mainContainer, BorderLayout.CENTER);
        setContentPane(root);

        selectNavigation(btnPatients, "PATIENTS", "Patient Management", "Register, manage, and view patient health records");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(navy);

        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel iconLabel = new JLabel("\u2665");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        iconLabel.setForeground(new Color(60, 150, 245));

        JLabel titleLabel = new JLabel("MediCare");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("HOSPITAL SYSTEM");
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        subtitleLabel.setForeground(new Color(140, 175, 210));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        titleRow.add(iconLabel);

        JPanel nameCol = new JPanel();
        nameCol.setOpaque(false);
        nameCol.setLayout(new BoxLayout(nameCol, BoxLayout.Y_AXIS));
        nameCol.add(titleLabel);
        nameCol.add(subtitleLabel);
        titleRow.add(nameCol);

        brandPanel.add(titleRow);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        JPanel navMenu = new JPanel();
        navMenu.setOpaque(false);
        navMenu.setLayout(new BoxLayout(navMenu, BoxLayout.Y_AXIS));
        navMenu.setBorder(new EmptyBorder(15, 12, 15, 12));

        JLabel navLabel = new JLabel("MENU");
        navLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        navLabel.setForeground(new Color(110, 140, 175));
        navLabel.setBorder(new EmptyBorder(0, 10, 8, 0));
        navMenu.add(navLabel);

        btnPatients = createNavButton("\uD83D\uDC65  Patients");
        btnDoctors = createNavButton("\uD83E\uDE7A  Doctors");
        btnAppointments = createNavButton("\uD83D\uDCC5  Appointments");

        btnPatients.addActionListener(e -> selectNavigation(
                btnPatients, "PATIENTS", "Patient Management", "Register, manage, and view patient health records"));

        btnDoctors.addActionListener(e -> selectNavigation(
                btnDoctors, "DOCTORS", "Doctor Directory", "Manage hospital staff, medical specialists, and departments"));

        btnAppointments.addActionListener(e -> selectNavigation(
                btnAppointments, "APPOINTMENTS", "Appointment Scheduling", "Schedule and manage doctor appointments and bookings"));

        navMenu.add(btnPatients);
        navMenu.add(Box.createVerticalStrut(8));
        navMenu.add(btnDoctors);
        navMenu.add(Box.createVerticalStrut(8));
        navMenu.add(btnAppointments);

        sidebar.add(navMenu, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(15, 20, 18, 20));

        JLabel statusLabel = new JLabel("\u25CF System Online");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(90, 205, 135));

        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(125, 155, 185));

        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(versionLabel, BorderLayout.EAST);
        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(210, 44));
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(new Color(210, 225, 245));
        btn.setBackground(navy);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != activeButton) {
                    btn.setBackground(navHover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != activeButton) {
                    btn.setBackground(navy);
                }
            }
        });

        return btn;
    }

    private void selectNavigation(JButton button, String cardName, String title, String subtitle) {
        if (activeButton != null) {
            activeButton.setBackground(navy);
            activeButton.setForeground(new Color(210, 225, 245));
        }

        activeButton = button;
        activeButton.setBackground(navActive);
        activeButton.setForeground(Color.WHITE);

        cardLayout.show(contentCards, cardName);
        pageTitleLabel.setText(title);
        pageSubtitleLabel.setText(subtitle);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 68));
        header.setBackground(headerBg);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight),
                new EmptyBorder(10, 25, 10, 25)
        ));

        JPanel titleArea = new JPanel();
        titleArea.setOpaque(false);
        titleArea.setLayout(new BoxLayout(titleArea, BoxLayout.Y_AXIS));

        pageTitleLabel = new JLabel("Dashboard");
        pageTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        pageTitleLabel.setForeground(textDark);

        pageSubtitleLabel = new JLabel("Hospital Management Overview");
        pageSubtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pageSubtitleLabel.setForeground(textMuted);

        titleArea.add(pageTitleLabel);
        titleArea.add(Box.createVerticalStrut(2));
        titleArea.add(pageSubtitleLabel);
        header.add(titleArea, BorderLayout.WEST);

        JPanel rightArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        rightArea.setOpaque(false);

        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        userBadge.setBackground(new Color(238, 244, 252));
        userBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 225, 240), 1),
                new EmptyBorder(3, 10, 3, 12)
        ));

        JLabel userIcon = new JLabel("\uD83D\uDC64");
        userIcon.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel userInfo = new JLabel(currentUser + " (" + currentRole + ")");
        userInfo.setFont(new Font("SansSerif", Font.BOLD, 12));
        userInfo.setForeground(navy);

        userBadge.add(userIcon);
        userBadge.add(userInfo);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        logoutBtn.setForeground(new Color(195, 45, 45));
        logoutBtn.setBackground(new Color(255, 240, 240));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 200, 200), 1),
                new EmptyBorder(6, 14, 6, 14)
        ));

        logoutBtn.addActionListener(e -> handleLogout());

        rightArea.add(userBadge);
        rightArea.add(logoutBtn);
        header.add(rightArea, BorderLayout.EAST);
        return header;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out of MediCare?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new MainFrame("Admin", "Administrator").setVisible(true));
    }
}
