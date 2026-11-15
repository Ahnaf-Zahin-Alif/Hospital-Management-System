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
    private JButton btnBilling;
    private JButton btnWards;
    private JButton btnPharmacy;
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
        setSize(1200, 750);
        setMinimumSize(new Dimension(1020, 650));
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

        contentCards.add(new PatientPanel(currentRole), "PATIENTS");
        contentCards.add(new DoctorPanel(currentRole), "DOCTORS");
        contentCards.add(new AppointmentPanel(), "APPOINTMENTS");
        contentCards.add(new BillingPanel(), "BILLING");
        contentCards.add(new WardPanel(currentRole), "WARDS");
        contentCards.add(new PharmacyPanel(currentRole), "PHARMACY");

        mainContainer.add(contentCards, BorderLayout.CENTER);
        root.add(mainContainer, BorderLayout.CENTER);
        setContentPane(root);

        if ("Doctor".equalsIgnoreCase(currentRole)) {
            selectNavigation(btnAppointments, "APPOINTMENTS", "Clinical Appointments", "Review patient consultation queue and clinical schedules");
        } else {
            selectNavigation(btnPatients, "PATIENTS", "Patient Management", "Register, manage, and view patient health records");
        }
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

        JLabel navLabel = new JLabel("MENU (" + currentRole.toUpperCase() + ")");
        navLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        navLabel.setForeground(new Color(110, 140, 175));
        navLabel.setBorder(new EmptyBorder(0, 10, 8, 0));
        navMenu.add(navLabel);

        boolean isAdmin = "Admin".equalsIgnoreCase(currentRole);
        boolean isDoctor = "Doctor".equalsIgnoreCase(currentRole);

        if (isDoctor) {
            btnAppointments = addNavEntry(navMenu, "Appointments", "APPOINTMENTS", "Appointment Queue", "Review patient consultation schedules and clinical records");
            btnPatients = addNavEntry(navMenu, "Patients", "PATIENTS", "Patient Records", "View and update patient clinical history and diagnoses");
            btnWards = addNavEntry(navMenu, "Wards & Beds", "WARDS", "Inpatient Wards", "Monitor ward bed occupancy and admitted patient status");
            btnPharmacy = addNavEntry(navMenu, "Pharmacy Stock", "PHARMACY", "Pharmacy Catalog", "Check medication stock levels and therapeutic availability");
        } else if (!isAdmin) {
            btnPatients = addNavEntry(navMenu, "Patients", "PATIENTS", "Patient Registration", "Register, manage, and check-in patient records");
            btnAppointments = addNavEntry(navMenu, "Appointments", "APPOINTMENTS", "Appointment Booking", "Schedule, reschedule, or cancel patient appointments");
            btnBilling = addNavEntry(navMenu, "Billing & Invoices", "BILLING", "Billing & Cashier", "Process patient consultations, generate invoices, and print receipts");
            btnWards = addNavEntry(navMenu, "Wards & Beds", "WARDS", "Admissions & Beds", "Allocate hospital beds and process patient discharge charges");
            btnPharmacy = addNavEntry(navMenu, "Pharmacy Stock", "PHARMACY", "Pharmacy Dispense", "Dispense prescribed medications and review current inventory");
            btnDoctors = addNavEntry(navMenu, "Doctors", "DOCTORS", "Doctor Directory", "Check visiting hours, room numbers, and physician specialties");
        } else {
            btnPatients = addNavEntry(navMenu, "Patients", "PATIENTS", "Patient Management", "Register, manage, and view patient health records");
            btnDoctors = addNavEntry(navMenu, "Doctors", "DOCTORS", "Doctor Directory", "Manage hospital staff, medical specialists, and departments");
            btnAppointments = addNavEntry(navMenu, "Appointments", "APPOINTMENTS", "Appointment Scheduling", "Schedule and manage doctor appointments and bookings");
            btnBilling = addNavEntry(navMenu, "Billing & Invoices", "BILLING", "Billing & Invoicing", "Manage patient bills, consultations, and payment receipts");
            btnWards = addNavEntry(navMenu, "Wards & Beds", "WARDS", "Ward & Bed Management", "Track hospital bed occupancy, admissions, and discharge");
            btnPharmacy = addNavEntry(navMenu, "Pharmacy Stock", "PHARMACY", "Pharmacy & Inventory", "Manage medicine stock, pricing, and catalog");
        }

        sidebar.add(navMenu, BorderLayout.CENTER);

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
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));

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

    private JButton addNavEntry(JPanel container, String label, String cardName, String title, String subtitle) {
        JButton btn = createNavButton(label);
        btn.addActionListener(e -> selectNavigation(btn, cardName, title, subtitle));
        if (container.getComponentCount() > 1) {
            container.add(Box.createVerticalStrut(6));
        }
        container.add(btn);
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
        Color badgeBg;
        Color badgeBorder;
        Color badgeFg;
        String roleIcon;

        if ("Doctor".equalsIgnoreCase(currentRole)) {
            badgeBg = new Color(236, 253, 245);
            badgeBorder = new Color(167, 243, 208);
            badgeFg = new Color(4, 120, 87);
            roleIcon = "●";
        } else if ("Admin".equalsIgnoreCase(currentRole)) {
            badgeBg = new Color(243, 232, 255);
            badgeBorder = new Color(216, 180, 254);
            badgeFg = new Color(109, 40, 217);
            roleIcon = "●";
        } else {
            badgeBg = new Color(254, 243, 199);
            badgeBorder = new Color(253, 230, 138);
            badgeFg = new Color(180, 83, 9);
            roleIcon = "●";
        }

        userBadge.setBackground(badgeBg);
        userBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(badgeBorder, 1),
                new EmptyBorder(3, 10, 3, 12)
        ));

        JLabel userIcon = new JLabel(roleIcon);
        userIcon.setFont(new Font("SansSerif", Font.PLAIN, 10));
        userIcon.setForeground(badgeFg);

        JLabel userInfo = new JLabel(currentUser + " (" + currentRole + ")");
        userInfo.setFont(new Font("SansSerif", Font.BOLD, 12));
        userInfo.setForeground(badgeFg);

        userBadge.add(userIcon);
        userBadge.add(userInfo);

        JButton logoutBtn = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? new Color(250, 220, 220)
                        : (getModel().isRollover() ? new Color(255, 230, 230) : new Color(255, 242, 242));
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(245, 195, 195));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        logoutBtn.setForeground(new Color(195, 45, 45));
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

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
