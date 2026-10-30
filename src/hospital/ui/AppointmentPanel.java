package hospital.ui;

import hospital.model.Appointment;
import hospital.services.AppointmentServices;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentPanel extends JPanel {

    private final Color navy = new Color(15, 39, 71);
    private final Color blue = new Color(35, 112, 190);
    private final Color blueHover = new Color(28, 95, 165);
    private final Color bgLight = new Color(244, 247, 251);
    private final Color cardBg = Color.WHITE;
    private final Color borderLight = new Color(220, 230, 240);
    private final Color textDark = new Color(35, 45, 60);
    private final Color textMuted = new Color(105, 120, 135);

    private final AppointmentServices appointmentServices;

    // Form fields
    private JTextField txtApptId;
    private JTextField txtPatient;
    private JTextField txtDoctor;
    private JTextField txtDate;
    private JComboBox<String> comboTime;
    private JComboBox<String> comboDepartment;
    private JComboBox<String> comboStatus;
    private JTextArea txtNotes;

    // Search & Filter
    private JTextField txtSearch;
    private JComboBox<String> filterStatus;
    private DefaultTableModel tableModel;
    private JTable appointmentTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel countLabel;

    // Buttons
    private JButton btnBook;
    private JButton btnUpdate;
    private JButton btnCancel;
    private JButton btnClear;

    public AppointmentPanel() {
        appointmentServices = new AppointmentServices();

        setLayout(new BorderLayout(15, 15));
        setBackground(bgLight);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        // Top Filter Bar
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // Center Split (Form Card on left, Table Card on right)
        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        mainContent.setOpaque(false);

        JPanel formCard = createFormCard();
        formCard.setPreferredSize(new Dimension(340, 0));
        mainContent.add(formCard, BorderLayout.WEST);

        JPanel tableCard = createTableCard();
        mainContent.add(tableCard, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // Load data from MySQL
        loadAppointmentsFromDatabase();
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout(15, 0));
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 48));

        // Search + Status Filter
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("\uD83D\uDD0D");
        lblSearch.setFont(new Font("SansSerif", Font.PLAIN, 15));

        txtSearch = new JTextField(16);
        styleInputField(txtSearch, 36);
        txtSearch.setToolTipText("Search by Patient, Doctor, or ID");

        String[] statusFilters = {"All Statuses", "Scheduled", "Completed", "Cancelled"};
        filterStatus = new JComboBox<>(statusFilters);
        styleComboBox(filterStatus);
        filterStatus.setPreferredSize(new Dimension(135, 36));

        JButton btnFilter = createButton("Filter", blue, Color.WHITE, 12);
        btnFilter.addActionListener(e -> applyFilters());

        JButton btnReset = createButton("Reset", new Color(235, 241, 248), navy, 12);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            filterStatus.setSelectedIndex(0);
            rowSorter.setRowFilter(null);
        });

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                applyFilters();
            }
        });

        filterStatus.addActionListener(e -> applyFilters());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(filterStatus);
        searchPanel.add(btnFilter);
        searchPanel.add(btnReset);

        // Stats badge
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        badgePanel.setOpaque(false);

        countLabel = new JLabel("Total Appointments: 0");
        countLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        countLabel.setForeground(navy);
        countLabel.setBackground(Color.WHITE);
        countLabel.setOpaque(true);
        countLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        badgePanel.add(countLabel);

        bar.add(searchPanel, BorderLayout.WEST);
        bar.add(badgePanel, BorderLayout.EAST);
        return bar;
    }

    private void applyFilters() {
        String query = txtSearch.getText().trim();
        String status = (String) filterStatus.getSelectedItem();

        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query)));
        }

        if (status != null && !status.equals("All Statuses")) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(status), 6));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        // Header
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel title = new JLabel("Appointment Booking");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel subtitle = new JLabel("Schedule or update patient visits");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(textMuted);

        cardHeader.add(title, BorderLayout.NORTH);
        cardHeader.add(subtitle, BorderLayout.SOUTH);
        card.add(cardHeader, BorderLayout.NORTH);

        // Fields Container
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        txtApptId = new JTextField(appointmentServices.getNextAppointmentId());
        txtApptId.setEditable(false);
        txtApptId.setBackground(new Color(245, 248, 252));
        styleInputField(txtApptId, 32);

        txtPatient = new JTextField();
        styleInputField(txtPatient, 32);
        txtPatient.setToolTipText("e.g. Emma Watson");

        txtDoctor = new JTextField();
        styleInputField(txtDoctor, 32);
        txtDoctor.setToolTipText("e.g. Dr. Alexander Wright");

        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        txtDate = new JTextField(today);
        styleInputField(txtDate, 32);

        String[] timeSlots = {"09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM"};
        comboTime = new JComboBox<>(timeSlots);
        styleComboBox(comboTime);

        String[] depts = {"General Medicine", "Cardiology", "Neurology", "Pediatrics", "Orthopedics", "Dermatology", "Oncology"};
        comboDepartment = new JComboBox<>(depts);
        styleComboBox(comboDepartment);

        String[] statuses = {"Scheduled", "Completed", "Cancelled"};
        comboStatus = new JComboBox<>(statuses);
        styleComboBox(comboStatus);

        txtNotes = new JTextArea(3, 20);
        txtNotes.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtNotes.setForeground(textDark);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));

        JScrollPane notesScroll = new JScrollPane(txtNotes);
        notesScroll.setBorder(null);

        // Add fields
        fieldsPanel.add(createFieldRow("Appointment ID:", txtApptId));
        fieldsPanel.add(Box.createVerticalStrut(6));
        fieldsPanel.add(createFieldRow("Patient (ID / Name) *:", txtPatient));
        fieldsPanel.add(Box.createVerticalStrut(6));
        fieldsPanel.add(createFieldRow("Doctor (ID / Name) *:", txtDoctor));
        fieldsPanel.add(Box.createVerticalStrut(6));

        // Date & Time Row
        JPanel dateTimeRow = new JPanel(new GridLayout(1, 2, 8, 0));
        dateTimeRow.setOpaque(false);
        dateTimeRow.add(createFieldRow("Date (YYYY-MM-DD) *:", txtDate));
        dateTimeRow.add(createFieldRow("Time Slot:", comboTime));
        fieldsPanel.add(dateTimeRow);
        fieldsPanel.add(Box.createVerticalStrut(6));

        // Department & Status Row
        JPanel deptStatusRow = new JPanel(new GridLayout(1, 2, 8, 0));
        deptStatusRow.setOpaque(false);
        deptStatusRow.add(createFieldRow("Department:", comboDepartment));
        deptStatusRow.add(createFieldRow("Status:", comboStatus));
        fieldsPanel.add(deptStatusRow);
        fieldsPanel.add(Box.createVerticalStrut(6));

        fieldsPanel.add(createFieldRow("Notes / Symptoms:", notesScroll));

        JScrollPane formScroll = new JScrollPane(fieldsPanel);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        card.add(formScroll, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnBook = createButton("Book Visit", blue, Color.WHITE, 12);
        btnUpdate = createButton("Update", new Color(40, 150, 90), Color.WHITE, 12);
        btnCancel = createButton("Cancel Appt", new Color(215, 60, 60), Color.WHITE, 12);
        btnClear = createButton("Clear", new Color(235, 241, 248), navy, 12);

        btnBook.addActionListener(e -> bookAppointment());
        btnUpdate.addActionListener(e -> updateAppointment());
        btnCancel.addActionListener(e -> cancelAppointment());
        btnClear.addActionListener(e -> clearForm());

        actions.add(btnBook);
        actions.add(btnUpdate);
        actions.add(btnCancel);
        actions.add(btnClear);

        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createFieldRow(String labelText, JComponent component) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(textDark);

        p.add(label, BorderLayout.NORTH);
        p.add(component, BorderLayout.CENTER);
        return p;
    }

    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        // Header
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel title = new JLabel("Appointment Schedule");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel info = new JLabel("Select an appointment to change status or reschedule");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(textMuted);

        cardHeader.add(title, BorderLayout.WEST);
        cardHeader.add(info, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        // Table
        String[] columns = {"Appt ID", "Patient", "Doctor", "Date", "Time", "Department", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        appointmentTable.setRowSorter(rowSorter);

        appointmentTable.setRowHeight(32);
        appointmentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        appointmentTable.setGridColor(new Color(235, 240, 245));
        appointmentTable.setShowVerticalLines(false);
        appointmentTable.setSelectionBackground(new Color(220, 235, 252));
        appointmentTable.setSelectionForeground(navy);

        JTableHeader th = appointmentTable.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setBackground(new Color(245, 248, 252));
        th.setForeground(navy);
        th.setPreferredSize(new Dimension(0, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight));

        // Center align ID, Date, Time, Status
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        appointmentTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        appointmentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        appointmentTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        appointmentTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        appointmentTable.getColumnModel().getColumn(4).setPreferredWidth(75);
        appointmentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Custom status badge renderer
        appointmentTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if (!isSelected) {
                    if ("Completed".equalsIgnoreCase(val)) {
                        c.setForeground(new Color(30, 140, 70));
                    } else if ("Cancelled".equalsIgnoreCase(val)) {
                        c.setForeground(new Color(210, 40, 40));
                    } else {
                        c.setForeground(new Color(210, 120, 20));
                    }
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        });

        appointmentTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        appointmentTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        appointmentTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        appointmentTable.getColumnModel().getColumn(6).setPreferredWidth(85);

        // Click to populate
        appointmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = appointmentTable.getSelectedRow();
                if (row != -1) {
                    int modelRow = appointmentTable.convertRowIndexToModel(row);
                    txtApptId.setText(tableModel.getValueAt(modelRow, 0).toString());
                    txtPatient.setText(tableModel.getValueAt(modelRow, 1).toString());
                    txtDoctor.setText(tableModel.getValueAt(modelRow, 2).toString());
                    txtDate.setText(tableModel.getValueAt(modelRow, 3).toString());
                    comboTime.setSelectedItem(tableModel.getValueAt(modelRow, 4).toString());
                    comboDepartment.setSelectedItem(tableModel.getValueAt(modelRow, 5).toString());
                    comboStatus.setSelectedItem(tableModel.getValueAt(modelRow, 6).toString());
                    txtNotes.setText(tableModel.getValueAt(modelRow, 7).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderLight, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void bookAppointment() {
        String patient = txtPatient.getText().trim();
        String doctor = txtDoctor.getText().trim();
        String date = txtDate.getText().trim();
        String time = (String) comboTime.getSelectedItem();
        String dept = (String) comboDepartment.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();
        String notes = txtNotes.getText().trim();

        if (patient.isEmpty() || doctor.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Patient, Doctor, and Date are required fields!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = txtApptId.getText().trim();
        Appointment a = new Appointment(id, patient, doctor, date, time, dept, status, notes);

        boolean saved = appointmentServices.addAppointment(a);
        if (saved) {
            tableModel.addRow(new Object[]{id, patient, doctor, date, time, dept, status, notes});
            JOptionPane.showMessageDialog(this,
                    "Appointment " + id + " booked successfully in database!",
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            updateCount();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save appointment to database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment from the table to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);

        String id = txtApptId.getText().trim();
        String patient = txtPatient.getText().trim();
        String doctor = txtDoctor.getText().trim();
        String date = txtDate.getText().trim();
        String time = (String) comboTime.getSelectedItem();
        String dept = (String) comboDepartment.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();
        String notes = txtNotes.getText().trim();

        if (patient.isEmpty() || doctor.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Patient, Doctor, and Date are required fields!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Appointment a = new Appointment(id, patient, doctor, date, time, dept, status, notes);
        boolean updated = appointmentServices.updateAppointment(a);

        if (updated) {
            tableModel.setValueAt(patient, modelRow, 1);
            tableModel.setValueAt(doctor, modelRow, 2);
            tableModel.setValueAt(date, modelRow, 3);
            tableModel.setValueAt(time, modelRow, 4);
            tableModel.setValueAt(dept, modelRow, 5);
            tableModel.setValueAt(status, modelRow, 6);
            tableModel.setValueAt(notes, modelRow, 7);

            JOptionPane.showMessageDialog(this,
                    "Appointment details updated successfully in database!",
                    "Update Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update appointment in database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment from the table to cancel.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        String apptId = tableModel.getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you want to mark Appointment " + apptId + " as Cancelled in database?",
                "Cancel Appointment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean cancelled = appointmentServices.cancelAppointment(apptId);
            if (cancelled) {
                tableModel.setValueAt("Cancelled", modelRow, 6);
                comboStatus.setSelectedItem("Cancelled");
                JOptionPane.showMessageDialog(this,
                        "Appointment " + apptId + " has been marked as Cancelled in database.",
                        "Appointment Cancelled",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update appointment status in database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtApptId.setText(appointmentServices.getNextAppointmentId());
        txtPatient.setText("");
        txtDoctor.setText("");
        txtDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        comboTime.setSelectedIndex(0);
        comboDepartment.setSelectedIndex(0);
        comboStatus.setSelectedIndex(0);
        txtNotes.setText("");
        appointmentTable.clearSelection();
        txtPatient.requestFocusInWindow();
    }

    private void updateCount() {
        countLabel.setText("Total Appointments: " + tableModel.getRowCount());
    }

    private void loadAppointmentsFromDatabase() {
        tableModel.setRowCount(0);
        List<Appointment> list = appointmentServices.getAllAppointments();
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getPatient(),
                    a.getDoctor(),
                    a.getDate(),
                    a.getTime(),
                    a.getDepartment(),
                    a.getStatus(),
                    a.getNotes()
            });
        }
        updateCount();
        txtApptId.setText(appointmentServices.getNextAppointmentId());
    }

    private void styleInputField(JTextField field, int height) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(textDark);
        field.setBackground(new Color(250, 252, 255));
        field.setCaretColor(blue);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, height));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(textDark);
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 32));
    }

    private JButton createButton(String text, Color bg, Color fg, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                new EmptyBorder(8, 14, 8, 14)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.equals(blue) ? blueHover : bg.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Appointment Panel Preview");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new AppointmentPanel());
            frame.setVisible(true);
        });
    }
}
