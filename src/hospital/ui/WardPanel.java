package hospital.ui;

import hospital.model.Bed;
import hospital.services.BedServices;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WardPanel extends JPanel {

    private final Color navy = new Color(15, 39, 71);
    private final Color blue = new Color(35, 112, 190);
    private final Color blueHover = new Color(28, 95, 165);
    private final Color bgLight = new Color(244, 247, 251);
    private final Color cardBg = Color.WHITE;
    private final Color borderLight = new Color(220, 230, 240);
    private final Color textDark = new Color(35, 45, 60);
    private final Color textMuted = new Color(105, 120, 135);

    private final BedServices bedServices = new BedServices();

    private JTextField txtBedId;
    private JComboBox<String> comboWard;
    private JTextField txtBedNumber;
    private JTextField txtPatient;
    private JComboBox<String> comboStatus;
    private JTextField txtDailyCharge;
    private JTextField txtAdmissionDate;

    private JTextField txtSearch;
    private JComboBox<String> filterWard;
    private DefaultTableModel tableModel;
    private JTable bedTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel statsLabel;

    public WardPanel() {
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
        loadBedsFromDatabase();
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout(15, 0));
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 48));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("\uD83D\uDD0D");
        lblSearch.setFont(new Font("SansSerif", Font.PLAIN, 15));

        txtSearch = new JTextField(15);
        styleInputField(txtSearch, 36);
        txtSearch.setToolTipText("Search by Bed No, Ward, or Patient");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        String[] wards = {"All Wards", "ICU", "General Ward", "Emergency", "Maternity", "Pediatric"};
        filterWard = new JComboBox<>(wards);
        styleComboBox(filterWard);
        filterWard.setPreferredSize(new Dimension(140, 36));
        filterWard.addActionListener(e -> applyFilters());

        JButton btnFilter = createButton("Filter", blue, Color.WHITE, 12);
        btnFilter.addActionListener(e -> applyFilters());

        JButton btnReset = createButton("Reset", new Color(235, 241, 248), navy, 12);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            filterWard.setSelectedIndex(0);
            rowSorter.setRowFilter(null);
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(filterWard);
        searchPanel.add(btnFilter);
        searchPanel.add(btnReset);

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        badgePanel.setOpaque(false);

        statsLabel = new JLabel("Total Beds: 0 | Available: 0");
        statsLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        statsLabel.setForeground(navy);
        statsLabel.setBackground(Color.WHITE);
        statsLabel.setOpaque(true);
        statsLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        badgePanel.add(statsLabel);

        bar.add(searchPanel, BorderLayout.WEST);
        bar.add(badgePanel, BorderLayout.EAST);
        return bar;
    }

    private void applyFilters() {
        String query = txtSearch.getText().trim();
        String ward = (String) filterWard.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
        }
        if (ward != null && !"All Wards".equals(ward)) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(ward), 1));
        }

        rowSorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
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

        JLabel title = new JLabel("Ward & Bed Allocation");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel subtitle = new JLabel("Manage hospital beds, admissions, and discharges");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(textMuted);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setOpaque(false);

        txtBedId = new JTextField(bedServices.getNextBedId());
        txtBedId.setEditable(false);
        txtBedId.setBackground(new Color(245, 248, 252));
        styleInputField(txtBedId, 32);

        String[] wardList = {"General Ward", "ICU", "Emergency", "Maternity", "Pediatric"};
        comboWard = new JComboBox<>(wardList);
        styleComboBox(comboWard);

        txtBedNumber = new JTextField();
        styleInputField(txtBedNumber, 32);
        txtBedNumber.setToolTipText("e.g. GW-05 or ICU-03");

        txtPatient = new JTextField();
        styleInputField(txtPatient, 32);
        txtPatient.setToolTipText("Patient Name or ID (Leave empty if available)");

        String[] statusList = {"Available", "Occupied", "Under Maintenance"};
        comboStatus = new JComboBox<>(statusList);
        styleComboBox(comboStatus);

        txtDailyCharge = new JTextField("75.0");
        styleInputField(txtDailyCharge, 32);

        txtAdmissionDate = new JTextField();
        styleInputField(txtAdmissionDate, 32);
        txtAdmissionDate.setToolTipText("YYYY-MM-DD");

        fields.add(createFieldRow("Bed ID:", txtBedId));
        fields.add(Box.createVerticalStrut(6));
        fields.add(createFieldRow("Ward Name:", comboWard));
        fields.add(Box.createVerticalStrut(6));
        fields.add(createFieldRow("Bed / Room Number *:", txtBedNumber));
        fields.add(Box.createVerticalStrut(6));
        fields.add(createFieldRow("Admitted Patient:", txtPatient));
        fields.add(Box.createVerticalStrut(6));

        JPanel statusChargeRow = new JPanel(new GridLayout(1, 2, 8, 0));
        statusChargeRow.setOpaque(false);
        statusChargeRow.add(createFieldRow("Status:", comboStatus));
        statusChargeRow.add(createFieldRow("Daily Rate ($):", txtDailyCharge));
        fields.add(statusChargeRow);
        fields.add(Box.createVerticalStrut(6));

        fields.add(createFieldRow("Admission Date:", txtAdmissionDate));

        JScrollPane formScroll = new JScrollPane(fields);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        card.add(formScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnSave = createButton("Save Bed", blue, Color.WHITE, 12);
        JButton btnUpdate = createButton("Update", new Color(40, 150, 90), Color.WHITE, 12);
        JButton btnDischarge = createButton("Discharge", new Color(215, 60, 60), Color.WHITE, 12);
        JButton btnClear = createButton("Clear", new Color(235, 241, 248), navy, 12);

        btnSave.addActionListener(e -> addBed());
        btnUpdate.addActionListener(e -> updateBed());
        btnDischarge.addActionListener(e -> dischargePatient());
        btnClear.addActionListener(e -> clearForm());

        actions.add(btnSave);
        actions.add(btnUpdate);
        actions.add(btnDischarge);
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

        JLabel title = new JLabel("Inpatient Wards & Occupancy");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel info = new JLabel("Click any bed row to assign, reassign, or discharge patients");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(textMuted);

        header.add(title, BorderLayout.WEST);
        header.add(info, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        String[] columns = {"Bed ID", "Ward", "Bed Number", "Patient Assigned", "Status", "Daily Rate ($)", "Admission Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bedTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        bedTable.setRowSorter(rowSorter);
        bedTable.setRowHeight(32);
        bedTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bedTable.setGridColor(new Color(235, 240, 245));
        bedTable.setShowVerticalLines(false);
        bedTable.setSelectionBackground(new Color(220, 235, 252));
        bedTable.setSelectionForeground(navy);

        JTableHeader th = bedTable.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setBackground(new Color(245, 248, 252));
        th.setForeground(navy);
        th.setPreferredSize(new Dimension(0, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        int[] widths = {70, 110, 95, 140, 100, 90, 110};
        for (int i = 0; i < widths.length; i++) {
            bedTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        bedTable.getColumnModel().getColumn(0).setCellRenderer(center);
        bedTable.getColumnModel().getColumn(2).setCellRenderer(center);
        bedTable.getColumnModel().getColumn(5).setCellRenderer(center);
        bedTable.getColumnModel().getColumn(6).setCellRenderer(center);

        bedTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if (!isSelected) {
                    if ("Available".equalsIgnoreCase(val)) {
                        label.setForeground(new Color(30, 140, 70));
                    } else if ("Occupied".equalsIgnoreCase(val)) {
                        label.setForeground(new Color(210, 40, 40));
                    } else {
                        label.setForeground(new Color(210, 120, 20));
                    }
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                }
                return label;
            }
        });

        bedTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bedTable.getSelectedRow();
                if (row != -1) {
                    populateFormFromRow(bedTable.convertRowIndexToModel(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(bedTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderLight, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void populateFormFromRow(int modelRow) {
        txtBedId.setText(String.valueOf(tableModel.getValueAt(modelRow, 0)));
        comboWard.setSelectedItem(String.valueOf(tableModel.getValueAt(modelRow, 1)));
        txtBedNumber.setText(String.valueOf(tableModel.getValueAt(modelRow, 2)));
        txtPatient.setText(String.valueOf(tableModel.getValueAt(modelRow, 3)));
        comboStatus.setSelectedItem(String.valueOf(tableModel.getValueAt(modelRow, 4)));
        txtDailyCharge.setText(String.valueOf(tableModel.getValueAt(modelRow, 5)));
        txtAdmissionDate.setText(String.valueOf(tableModel.getValueAt(modelRow, 6)));
    }

    private Bed parseBedFromForm() {
        String id = txtBedId.getText().trim();
        String ward = (String) comboWard.getSelectedItem();
        String bedNo = txtBedNumber.getText().trim();
        String patient = txtPatient.getText().trim();
        String status = (String) comboStatus.getSelectedItem();
        String chargeStr = txtDailyCharge.getText().trim();
        String date = txtAdmissionDate.getText().trim();

        if (bedNo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Bed Number is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        double charge;
        try {
            charge = Double.parseDouble(chargeStr.replace("$", "").trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric daily charge.",
                    "Invalid Rate",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (!patient.isEmpty() && "Available".equals(status)) {
            status = "Occupied";
            comboStatus.setSelectedItem("Occupied");
            if (date.isEmpty()) {
                date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
                txtAdmissionDate.setText(date);
            }
        }

        return new Bed(id, ward, bedNo, patient, status, charge, date);
    }

    private void addBed() {
        Bed bed = parseBedFromForm();
        if (bed == null) return;

        if (bedServices.addBed(bed)) {
            tableModel.addRow(new Object[]{
                    bed.getId(), bed.getWardName(), bed.getBedNumber(),
                    bed.getPatient(), bed.getStatus(), bed.getDailyCharge(), bed.getAdmissionDate()
            });
            JOptionPane.showMessageDialog(this,
                    "Bed " + bed.getBedNumber() + " added to " + bed.getWardName() + ".",
                    "Bed Registered",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            updateStats();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save bed to database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBed() {
        int selectedRow = bedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a bed row to update.",
                    "No Bed Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Bed bed = parseBedFromForm();
        if (bed == null) return;

        int modelRow = bedTable.convertRowIndexToModel(selectedRow);
        if (bedServices.updateBed(bed)) {
            tableModel.setValueAt(bed.getWardName(), modelRow, 1);
            tableModel.setValueAt(bed.getBedNumber(), modelRow, 2);
            tableModel.setValueAt(bed.getPatient(), modelRow, 3);
            tableModel.setValueAt(bed.getStatus(), modelRow, 4);
            tableModel.setValueAt(bed.getDailyCharge(), modelRow, 5);
            tableModel.setValueAt(bed.getAdmissionDate(), modelRow, 6);

            JOptionPane.showMessageDialog(this,
                    "Bed allocation updated.",
                    "Update Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            updateStats();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update bed in database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dischargePatient() {
        int selectedRow = bedTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an occupied bed to discharge the patient.",
                    "No Bed Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = bedTable.convertRowIndexToModel(selectedRow);
        String bedId = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String patient = String.valueOf(tableModel.getValueAt(modelRow, 3));
        String status = String.valueOf(tableModel.getValueAt(modelRow, 4));
        String admDate = String.valueOf(tableModel.getValueAt(modelRow, 6));
        double dailyRate = Double.parseDouble(String.valueOf(tableModel.getValueAt(modelRow, 5)));

        if (patient.trim().isEmpty() || "Available".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                    "This bed currently has no assigned patient to discharge.",
                    "Bed Already Available",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long days = 1;
        try {
            if (!admDate.trim().isEmpty()) {
                LocalDate start = LocalDate.parse(admDate);
                days = Math.max(1, ChronoUnit.DAYS.between(start, LocalDate.now()));
            }
        } catch (DateTimeParseException ignored) {
        }
        double stayTotal = days * dailyRate;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Discharge patient " + patient + " from Bed " + bedId + "?\n"
                        + "Stay Duration : " + days + " day(s)\n"
                        + "Estimated Bed Charges: $" + String.format("%.2f", stayTotal),
                "Confirm Patient Discharge",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bedServices.dischargePatient(bedId)) {
                tableModel.setValueAt("", modelRow, 3);
                tableModel.setValueAt("Available", modelRow, 4);
                tableModel.setValueAt("", modelRow, 6);
                clearForm();
                updateStats();
                JOptionPane.showMessageDialog(this,
                        "Patient " + patient + " has been discharged.\nTotal Bed Fee: $" + String.format("%.2f", stayTotal),
                        "Discharge Processed",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to discharge patient in database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtBedId.setText(bedServices.getNextBedId());
        comboWard.setSelectedIndex(0);
        txtBedNumber.setText("");
        txtPatient.setText("");
        comboStatus.setSelectedIndex(0);
        txtDailyCharge.setText("75.0");
        txtAdmissionDate.setText("");
        bedTable.clearSelection();
        txtBedNumber.requestFocusInWindow();
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        int available = 0;
        int occupied = 0;
        for (int i = 0; i < total; i++) {
            String s = String.valueOf(tableModel.getValueAt(i, 4));
            if ("Available".equalsIgnoreCase(s)) available++;
            else if ("Occupied".equalsIgnoreCase(s)) occupied++;
        }
        statsLabel.setText(String.format("Total: %d | Available: %d | Occupied: %d", total, available, occupied));
    }

    private void loadBedsFromDatabase() {
        tableModel.setRowCount(0);
        for (Bed b : bedServices.getAllBeds()) {
            tableModel.addRow(new Object[]{
                    b.getId(), b.getWardName(), b.getBedNumber(),
                    b.getPatient(), b.getStatus(), b.getDailyCharge(), b.getAdmissionDate()
            });
        }
        updateStats();
        txtBedId.setText(bedServices.getNextBedId());
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

