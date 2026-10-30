package hospital.ui;

import hospital.model.Patient;
import hospital.services.PatientServices;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class PatientPanel extends JPanel {

    private final Color navy = new Color(15, 39, 71);
    private final Color blue = new Color(35, 112, 190);
    private final Color blueHover = new Color(28, 95, 165);
    private final Color bgLight = new Color(244, 247, 251);
    private final Color cardBg = Color.WHITE;
    private final Color borderLight = new Color(220, 230, 240);
    private final Color textDark = new Color(35, 45, 60);
    private final Color textMuted = new Color(105, 120, 135);

    private final PatientServices patientServices = new PatientServices();

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtAge;
    private JComboBox<String> comboGender;
    private JComboBox<String> comboBlood;
    private JTextField txtPhone;
    private JTextField txtDiagnosis;
    private JTextArea txtAddress;

    private JTextField txtSearch;
    private DefaultTableModel tableModel;
    private JTable patientTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel countLabel;

    public PatientPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(bgLight);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        add(createTopBar(), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        mainContent.setOpaque(false);

        JPanel formCard = createFormCard();
        formCard.setPreferredSize(new Dimension(340, 0));
        mainContent.add(formCard, BorderLayout.WEST);
        mainContent.add(createTableCard(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
        loadPatientsFromDatabase();
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout(15, 0));
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 48));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("\uD83D\uDD0D");
        lblSearch.setFont(new Font("SansSerif", Font.PLAIN, 15));

        txtSearch = new JTextField(20);
        styleInputField(txtSearch, 36);
        txtSearch.setToolTipText("Search by Name, ID, or Phone");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilter();
            }
        });

        JButton btnSearch = createButton("Filter", blue, Color.WHITE, 12);
        btnSearch.addActionListener(e -> applyFilter());

        JButton btnReset = createButton("Reset", new Color(235, 241, 248), navy, 12);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            rowSorter.setRowFilter(null);
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        badgePanel.setOpaque(false);

        countLabel = new JLabel("Total Patients: 0");
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

    private void applyFilter() {
        String query = txtSearch.getText().trim();
        rowSorter.setRowFilter(query.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Patient Details");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel subtitle = new JLabel("Enter patient records below");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(textMuted);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setOpaque(false);

        txtId = new JTextField(patientServices.getNextPatientId());
        txtId.setEditable(false);
        txtId.setBackground(new Color(245, 248, 252));
        styleInputField(txtId, 32);

        txtName = new JTextField();
        styleInputField(txtName, 32);

        txtAge = new JTextField();
        styleInputField(txtAge, 32);

        String[] genders = {"Male", "Female", "Other"};
        comboGender = new JComboBox<>(genders);
        styleComboBox(comboGender);

        String[] bloodGroups = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        comboBlood = new JComboBox<>(bloodGroups);
        styleComboBox(comboBlood);

        txtPhone = new JTextField();
        styleInputField(txtPhone, 32);

        txtDiagnosis = new JTextField();
        styleInputField(txtDiagnosis, 32);

        txtAddress = new JTextArea(3, 20);
        txtAddress.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtAddress.setForeground(textDark);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        txtAddress.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));

        JScrollPane addressScroll = new JScrollPane(txtAddress);
        addressScroll.setBorder(null);

        fields.add(createFieldRow("Patient ID:", txtId));
        fields.add(Box.createVerticalStrut(7));
        fields.add(createFieldRow("Full Name *:", txtName));
        fields.add(Box.createVerticalStrut(7));

        JPanel ageGenderRow = new JPanel(new GridLayout(1, 2, 8, 0));
        ageGenderRow.setOpaque(false);
        ageGenderRow.add(createFieldRow("Age *:", txtAge));
        ageGenderRow.add(createFieldRow("Gender:", comboGender));
        fields.add(ageGenderRow);
        fields.add(Box.createVerticalStrut(7));

        JPanel bloodPhoneRow = new JPanel(new GridLayout(1, 2, 8, 0));
        bloodPhoneRow.setOpaque(false);
        bloodPhoneRow.add(createFieldRow("Blood Group:", comboBlood));
        bloodPhoneRow.add(createFieldRow("Phone *:", txtPhone));
        fields.add(bloodPhoneRow);
        fields.add(Box.createVerticalStrut(7));

        fields.add(createFieldRow("Diagnosis / Reason:", txtDiagnosis));
        fields.add(Box.createVerticalStrut(7));
        fields.add(createFieldRow("Address / Notes:", addressScroll));

        JScrollPane formScroll = new JScrollPane(fields);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        card.add(formScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAdd = createButton("Add Patient", blue, Color.WHITE, 12);
        JButton btnUpdate = createButton("Update", new Color(40, 150, 90), Color.WHITE, 12);
        JButton btnDelete = createButton("Delete", new Color(215, 60, 60), Color.WHITE, 12);
        JButton btnClear = createButton("Clear", new Color(235, 241, 248), navy, 12);

        btnAdd.addActionListener(e -> addPatient());
        btnUpdate.addActionListener(e -> updatePatient());
        btnDelete.addActionListener(e -> deletePatient());
        btnClear.addActionListener(e -> clearForm());

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnDelete);
        actions.add(btnClear);

        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createFieldRow(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(textDark);

        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Registered Patients Directory");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel info = new JLabel("Click any row to view, edit, or delete records");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(textMuted);

        header.add(title, BorderLayout.WEST);
        header.add(info, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Full Name", "Age", "Gender", "Blood", "Phone", "Diagnosis", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(rowSorter);
        patientTable.setRowHeight(32);
        patientTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        patientTable.setGridColor(new Color(235, 240, 245));
        patientTable.setShowVerticalLines(false);
        patientTable.setSelectionBackground(new Color(220, 235, 252));
        patientTable.setSelectionForeground(navy);

        JTableHeader th = patientTable.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setBackground(new Color(245, 248, 252));
        th.setForeground(navy);
        th.setPreferredSize(new Dimension(0, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        int[] widths = {55, 130, 45, 65, 55, 100, 120, 140};
        int[] centerCols = {0, 2, 3, 4};

        for (int i = 0; i < widths.length; i++) {
            patientTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        for (int col : centerCols) {
            patientTable.getColumnModel().getColumn(col).setCellRenderer(center);
        }

        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = patientTable.getSelectedRow();
                if (row != -1) {
                    populateFormFromRow(patientTable.convertRowIndexToModel(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderLight, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void populateFormFromRow(int modelRow) {
        txtId.setText(String.valueOf(tableModel.getValueAt(modelRow, 0)));
        txtName.setText(String.valueOf(tableModel.getValueAt(modelRow, 1)));
        txtAge.setText(String.valueOf(tableModel.getValueAt(modelRow, 2)));
        comboGender.setSelectedItem(String.valueOf(tableModel.getValueAt(modelRow, 3)));
        comboBlood.setSelectedItem(String.valueOf(tableModel.getValueAt(modelRow, 4)));
        txtPhone.setText(String.valueOf(tableModel.getValueAt(modelRow, 5)));
        txtDiagnosis.setText(String.valueOf(tableModel.getValueAt(modelRow, 6)));
        txtAddress.setText(String.valueOf(tableModel.getValueAt(modelRow, 7)));
    }

    private Patient parsePatientFromForm() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String ageStr = txtAge.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name, Age, and Phone are required fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0 || age > 130) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid age between 0 and 130.",
                    "Invalid Age",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Patient(
                id,
                name,
                age,
                (String) comboGender.getSelectedItem(),
                (String) comboBlood.getSelectedItem(),
                phone,
                txtDiagnosis.getText().trim(),
                txtAddress.getText().trim()
        );
    }

    private void addPatient() {
        Patient patient = parsePatientFromForm();
        if (patient == null) {
            return;
        }

        if (patientServices.addPatient(patient)) {
            tableModel.addRow(new Object[]{
                    patient.getId(), patient.getName(), String.valueOf(patient.getAge()),
                    patient.getGender(), patient.getBloodGroup(), patient.getPhone(),
                    patient.getDiagnosis(), patient.getAddress()
            });
            JOptionPane.showMessageDialog(this,
                    "Patient " + patient.getName() + " (" + patient.getId() + ") registered successfully.",
                    "Patient Registered",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            updateCount();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save patient to database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient from the table to update.",
                    "No Patient Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient patient = parsePatientFromForm();
        if (patient == null) {
            return;
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        if (patientServices.updatePatient(patient)) {
            tableModel.setValueAt(patient.getName(), modelRow, 1);
            tableModel.setValueAt(String.valueOf(patient.getAge()), modelRow, 2);
            tableModel.setValueAt(patient.getGender(), modelRow, 3);
            tableModel.setValueAt(patient.getBloodGroup(), modelRow, 4);
            tableModel.setValueAt(patient.getPhone(), modelRow, 5);
            tableModel.setValueAt(patient.getDiagnosis(), modelRow, 6);
            tableModel.setValueAt(patient.getAddress(), modelRow, 7);

            JOptionPane.showMessageDialog(this,
                    "Patient record updated successfully.",
                    "Update Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update patient in database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient from the table to delete.",
                    "No Patient Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        String patientName = String.valueOf(tableModel.getValueAt(modelRow, 1));
        String patientId = String.valueOf(tableModel.getValueAt(modelRow, 0));

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete patient " + patientName + " (" + patientId + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (patientServices.deletePatient(patientId)) {
                tableModel.removeRow(modelRow);
                clearForm();
                updateCount();
                JOptionPane.showMessageDialog(this,
                        "Patient record deleted successfully.",
                        "Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete patient from database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtId.setText(patientServices.getNextPatientId());
        txtName.setText("");
        txtAge.setText("");
        comboGender.setSelectedIndex(0);
        comboBlood.setSelectedIndex(0);
        txtPhone.setText("");
        txtDiagnosis.setText("");
        txtAddress.setText("");
        patientTable.clearSelection();
        txtName.requestFocusInWindow();
    }

    private void updateCount() {
        countLabel.setText("Total Patients: " + tableModel.getRowCount());
    }

    private void loadPatientsFromDatabase() {
        tableModel.setRowCount(0);
        for (Patient p : patientServices.getAllPatients()) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), String.valueOf(p.getAge()),
                    p.getGender(), p.getBloodGroup(), p.getPhone(),
                    p.getDiagnosis(), p.getAddress()
            });
        }
        updateCount();
        txtId.setText(patientServices.getNextPatientId());
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
