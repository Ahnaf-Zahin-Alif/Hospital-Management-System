package hospital.ui;

import hospital.model.Doctor;
import hospital.services.DoctorServices;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DoctorPanel extends JPanel {

    private final Color navy = new Color(15, 39, 71);
    private final Color blue = new Color(35, 112, 190);
    private final Color blueHover = new Color(28, 95, 165);
    private final Color bgLight = new Color(244, 247, 251);
    private final Color cardBg = Color.WHITE;
    private final Color borderLight = new Color(220, 230, 240);
    private final Color textDark = new Color(35, 45, 60);
    private final Color textMuted = new Color(105, 120, 135);

    private final DoctorServices doctorServices;

    // Form fields
    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> comboSpecialty;
    private JTextField txtQualification;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtFee;
    private JTextField txtRoom;
    private JTextField txtHours;

    // Search & Filter
    private JTextField txtSearch;
    private JComboBox<String> filterSpecialty;
    private DefaultTableModel tableModel;
    private JTable doctorTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel countLabel;

    // Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    public DoctorPanel() {
        doctorServices = new DoctorServices();

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
        loadDoctorsFromDatabase();
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout(15, 0));
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 48));

        // Search + Specialty filter
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("\uD83D\uDD0D");
        lblSearch.setFont(new Font("SansSerif", Font.PLAIN, 15));

        txtSearch = new JTextField(15);
        styleInputField(txtSearch, 36);
        txtSearch.setToolTipText("Search by Doctor Name, ID, or Phone");

        String[] filterOptions = {"All Specialties", "Cardiology", "Neurology", "Pediatrics", "Orthopedics", "General Medicine", "Dermatology", "Oncology"};
        filterSpecialty = new JComboBox<>(filterOptions);
        styleComboBox(filterSpecialty);
        filterSpecialty.setPreferredSize(new Dimension(145, 36));

        JButton btnFilter = createButton("Filter", blue, Color.WHITE, 12);
        btnFilter.addActionListener(e -> applyFilters());

        JButton btnReset = createButton("Reset", new Color(235, 241, 248), navy, 12);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            filterSpecialty.setSelectedIndex(0);
            rowSorter.setRowFilter(null);
        });

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                applyFilters();
            }
        });

        filterSpecialty.addActionListener(e -> applyFilters());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(filterSpecialty);
        searchPanel.add(btnFilter);
        searchPanel.add(btnReset);

        // Stats badge
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        badgePanel.setOpaque(false);

        countLabel = new JLabel("Total Doctors: 0");
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
        String specialty = (String) filterSpecialty.getSelectedItem();

        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query)));
        }

        if (specialty != null && !specialty.equals("All Specialties")) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(specialty), 2));
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

        JLabel title = new JLabel("Doctor Details");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel subtitle = new JLabel("Register or modify medical practitioners");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(textMuted);

        cardHeader.add(title, BorderLayout.NORTH);
        cardHeader.add(subtitle, BorderLayout.SOUTH);
        card.add(cardHeader, BorderLayout.NORTH);

        // Fields Container
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        txtId = new JTextField(doctorServices.getNextDoctorId());
        txtId.setEditable(false);
        txtId.setBackground(new Color(245, 248, 252));
        styleInputField(txtId, 32);

        txtName = new JTextField();
        styleInputField(txtName, 32);

        String[] specialties = {"General Medicine", "Cardiology", "Neurology", "Pediatrics", "Orthopedics", "Dermatology", "Oncology", "General Surgery"};
        comboSpecialty = new JComboBox<>(specialties);
        styleComboBox(comboSpecialty);

        txtQualification = new JTextField();
        styleInputField(txtQualification, 32);

        txtPhone = new JTextField();
        styleInputField(txtPhone, 32);

        txtEmail = new JTextField();
        styleInputField(txtEmail, 32);

        txtFee = new JTextField();
        styleInputField(txtFee, 32);

        txtRoom = new JTextField();
        styleInputField(txtRoom, 32);

        txtHours = new JTextField();
        styleInputField(txtHours, 32);

        // Add fields
        fieldsPanel.add(createFieldRow("Doctor ID:", txtId));
        fieldsPanel.add(Box.createVerticalStrut(6));
        fieldsPanel.add(createFieldRow("Doctor Name *:", txtName));
        fieldsPanel.add(Box.createVerticalStrut(6));
        fieldsPanel.add(createFieldRow("Specialization:", comboSpecialty));
        fieldsPanel.add(Box.createVerticalStrut(6));
        fieldsPanel.add(createFieldRow("Degree / Qualification:", txtQualification));
        fieldsPanel.add(Box.createVerticalStrut(6));

        // Phone & Email Row
        JPanel contactRow = new JPanel(new GridLayout(1, 2, 8, 0));
        contactRow.setOpaque(false);
        contactRow.add(createFieldRow("Phone *:", txtPhone));
        contactRow.add(createFieldRow("Email:", txtEmail));
        fieldsPanel.add(contactRow);
        fieldsPanel.add(Box.createVerticalStrut(6));

        // Fee & Room Row
        JPanel feeRoomRow = new JPanel(new GridLayout(1, 2, 8, 0));
        feeRoomRow.setOpaque(false);
        feeRoomRow.add(createFieldRow("Fee ($) *:", txtFee));
        feeRoomRow.add(createFieldRow("Room No *:", txtRoom));
        fieldsPanel.add(feeRoomRow);
        fieldsPanel.add(Box.createVerticalStrut(6));

        fieldsPanel.add(createFieldRow("Visiting Hours:", txtHours));

        JScrollPane formScroll = new JScrollPane(fieldsPanel);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        card.add(formScroll, BorderLayout.CENTER);

        // Action Buttons Row
        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnAdd = createButton("Add Doctor", blue, Color.WHITE, 12);
        btnUpdate = createButton("Update", new Color(40, 150, 90), Color.WHITE, 12);
        btnDelete = createButton("Delete", new Color(215, 60, 60), Color.WHITE, 12);
        btnClear = createButton("Clear", new Color(235, 241, 248), navy, 12);

        btnAdd.addActionListener(e -> addDoctor());
        btnUpdate.addActionListener(e -> updateDoctor());
        btnDelete.addActionListener(e -> deleteDoctor());
        btnClear.addActionListener(e -> clearForm());

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
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

        JLabel title = new JLabel("Doctors & Specialists Directory");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel info = new JLabel("Select a doctor row to review schedule or edit details");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(textMuted);

        cardHeader.add(title, BorderLayout.WEST);
        cardHeader.add(info, BorderLayout.EAST);
        card.add(cardHeader, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Doctor Name", "Specialization", "Qualification", "Phone", "Fee ($)", "Room", "Visiting Hours"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        doctorTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        doctorTable.setRowSorter(rowSorter);

        doctorTable.setRowHeight(32);
        doctorTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        doctorTable.setGridColor(new Color(235, 240, 245));
        doctorTable.setShowVerticalLines(false);
        doctorTable.setSelectionBackground(new Color(220, 235, 252));
        doctorTable.setSelectionForeground(navy);

        JTableHeader th = doctorTable.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setBackground(new Color(245, 248, 252));
        th.setForeground(navy);
        th.setPreferredSize(new Dimension(0, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight));

        // Center align ID, Fee, Room
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        doctorTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        doctorTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        doctorTable.getColumnModel().getColumn(5).setPreferredWidth(55);
        doctorTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        doctorTable.getColumnModel().getColumn(6).setPreferredWidth(55);
        doctorTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        doctorTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        doctorTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        doctorTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        doctorTable.getColumnModel().getColumn(7).setPreferredWidth(140);

        // Click to populate form
        doctorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = doctorTable.getSelectedRow();
                if (row != -1) {
                    int modelRow = doctorTable.convertRowIndexToModel(row);
                    txtId.setText(tableModel.getValueAt(modelRow, 0).toString());
                    txtName.setText(tableModel.getValueAt(modelRow, 1).toString());
                    comboSpecialty.setSelectedItem(tableModel.getValueAt(modelRow, 2).toString());
                    txtQualification.setText(tableModel.getValueAt(modelRow, 3).toString());
                    txtPhone.setText(tableModel.getValueAt(modelRow, 4).toString());
                    txtFee.setText(tableModel.getValueAt(modelRow, 5).toString());
                    txtRoom.setText(tableModel.getValueAt(modelRow, 6).toString());
                    txtHours.setText(tableModel.getValueAt(modelRow, 7).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderLight, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void addDoctor() {
        String name = txtName.getText().trim();
        String specialty = (String) comboSpecialty.getSelectedItem();
        String qual = txtQualification.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String fee = txtFee.getText().trim();
        String room = txtRoom.getText().trim();
        String hours = txtHours.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || fee.isEmpty() || room.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name, Phone, Fee, and Room No are required fields!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Double.parseDouble(fee.replace("$", "").trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric consultation fee.",
                    "Invalid Fee",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = txtId.getText().trim();
        Doctor d = new Doctor(id, name, specialty, qual, phone, email, fee, room, hours);

        boolean saved = doctorServices.addDoctor(d);
        if (saved) {
            tableModel.addRow(new Object[]{id, name, specialty, qual, phone, fee, room, hours});
            JOptionPane.showMessageDialog(this,
                    "Dr. " + name + " (" + id + ") registered successfully in database!",
                    "Doctor Added",
                    JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            updateCount();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save doctor to database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a doctor from the table to update.",
                    "No Doctor Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = doctorTable.convertRowIndexToModel(selectedRow);

        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String specialty = (String) comboSpecialty.getSelectedItem();
        String qual = txtQualification.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String fee = txtFee.getText().trim();
        String room = txtRoom.getText().trim();
        String hours = txtHours.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || fee.isEmpty() || room.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name, Phone, Fee, and Room No are required fields!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Double.parseDouble(fee.replace("$", "").trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric consultation fee.",
                    "Invalid Fee",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Doctor d = new Doctor(id, name, specialty, qual, phone, email, fee, room, hours);
        boolean updated = doctorServices.updateDoctor(d);

        if (updated) {
            tableModel.setValueAt(name, modelRow, 1);
            tableModel.setValueAt(specialty, modelRow, 2);
            tableModel.setValueAt(qual, modelRow, 3);
            tableModel.setValueAt(phone, modelRow, 4);
            tableModel.setValueAt(fee, modelRow, 5);
            tableModel.setValueAt(room, modelRow, 6);
            tableModel.setValueAt(hours, modelRow, 7);

            JOptionPane.showMessageDialog(this,
                    "Doctor details updated successfully in database!",
                    "Update Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update doctor in database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a doctor from the table to delete.",
                    "No Doctor Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = doctorTable.convertRowIndexToModel(selectedRow);
        String docName = tableModel.getValueAt(modelRow, 1).toString();
        String docId = tableModel.getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove " + docName + " (" + docId + ") from database?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = doctorServices.deleteDoctor(docId);
            if (deleted) {
                tableModel.removeRow(modelRow);
                clearForm();
                updateCount();
                JOptionPane.showMessageDialog(this,
                        "Doctor record removed from database.",
                        "Doctor Removed",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to remove doctor from database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtId.setText(doctorServices.getNextDoctorId());
        txtName.setText("");
        comboSpecialty.setSelectedIndex(0);
        txtQualification.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtFee.setText("");
        txtRoom.setText("");
        txtHours.setText("");
        doctorTable.clearSelection();
        txtName.requestFocusInWindow();
    }

    private void updateCount() {
        countLabel.setText("Total Doctors: " + tableModel.getRowCount());
    }

    private void loadDoctorsFromDatabase() {
        tableModel.setRowCount(0);
        List<Doctor> list = doctorServices.getAllDoctors();
        for (Doctor d : list) {
            tableModel.addRow(new Object[]{
                    d.getId(),
                    d.getName(),
                    d.getSpecialty(),
                    d.getQualification(),
                    d.getPhone(),
                    d.getFee(),
                    d.getRoom(),
                    d.getVisitingHours()
            });
        }
        updateCount();
        txtId.setText(doctorServices.getNextDoctorId());
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
}
