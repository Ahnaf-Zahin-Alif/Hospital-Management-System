package hospital.ui;

import hospital.model.Bill;
import hospital.services.BillServices;

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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BillingPanel extends JPanel {

    private final Color navy = new Color(15, 39, 71);
    private final Color blue = new Color(35, 112, 190);
    private final Color blueHover = new Color(28, 95, 165);
    private final Color bgLight = new Color(244, 247, 251);
    private final Color cardBg = Color.WHITE;
    private final Color borderLight = new Color(220, 230, 240);
    private final Color textDark = new Color(35, 45, 60);
    private final Color textMuted = new Color(105, 120, 135);

    private final BillServices billServices = new BillServices();

    private JTextField txtBillId;
    private JTextField txtPatient;
    private JTextField txtDoctor;
    private JTextField txtConsultationFee;
    private JTextField txtTreatmentFee;
    private JTextField txtMedicineFee;
    private JTextField txtTotalAmount;
    private JComboBox<String> comboStatus;
    private JComboBox<String> comboPaymentMethod;
    private JTextField txtBillDate;

    private JTextField txtSearch;
    private JComboBox<String> filterStatus;
    private DefaultTableModel tableModel;
    private JTable billTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel totalRevenueLabel;

    public BillingPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(bgLight);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        add(createTopBar(), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(15, 0));
        mainContent.setOpaque(false);

        JPanel formCard = createFormCard();
        formCard.setPreferredSize(new Dimension(350, 0));
        mainContent.add(formCard, BorderLayout.WEST);
        mainContent.add(createTableCard(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
        loadBillsFromDatabase();
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout(15, 0));
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 48));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("\uD83D\uDD0D");
        lblSearch.setFont(new Font("SansSerif", Font.PLAIN, 15));

        txtSearch = new JTextField(16);
        styleInputField(txtSearch, 36);
        txtSearch.setToolTipText("Search by Patient, Doctor, or Bill ID");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        String[] statuses = {"All Statuses", "Paid", "Pending", "Partial"};
        filterStatus = new JComboBox<>(statuses);
        styleComboBox(filterStatus);
        filterStatus.setPreferredSize(new Dimension(135, 36));
        filterStatus.addActionListener(e -> applyFilters());

        JButton btnFilter = createButton("Filter", blue, Color.WHITE, 12);
        btnFilter.addActionListener(e -> applyFilters());

        JButton btnReset = createButton("Reset", new Color(235, 241, 248), navy, 12);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            filterStatus.setSelectedIndex(0);
            rowSorter.setRowFilter(null);
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(filterStatus);
        searchPanel.add(btnFilter);
        searchPanel.add(btnReset);

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        badgePanel.setOpaque(false);

        totalRevenueLabel = new JLabel("Total Invoiced: $0.00");
        totalRevenueLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        totalRevenueLabel.setForeground(navy);
        totalRevenueLabel.setBackground(Color.WHITE);
        totalRevenueLabel.setOpaque(true);
        totalRevenueLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(5, 12, 5, 12)
        ));

        badgePanel.add(totalRevenueLabel);

        bar.add(searchPanel, BorderLayout.WEST);
        bar.add(badgePanel, BorderLayout.EAST);
        return bar;
    }

    private void applyFilters() {
        String query = txtSearch.getText().trim();
        String status = (String) filterStatus.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
        }
        if (status != null && !"All Statuses".equals(status)) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(status), 7));
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

        JLabel title = new JLabel("Billing & Invoicing");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel subtitle = new JLabel("Process consultations, treatments, and payments");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(textMuted);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setOpaque(false);

        txtBillId = new JTextField(billServices.getNextBillId());
        txtBillId.setEditable(false);
        txtBillId.setBackground(new Color(245, 248, 252));
        styleInputField(txtBillId, 32);

        txtPatient = new JTextField();
        styleInputField(txtPatient, 32);

        txtDoctor = new JTextField();
        styleInputField(txtDoctor, 32);

        txtConsultationFee = new JTextField("100.0");
        styleInputField(txtConsultationFee, 32);

        txtTreatmentFee = new JTextField("0.0");
        styleInputField(txtTreatmentFee, 32);

        txtMedicineFee = new JTextField("0.0");
        styleInputField(txtMedicineFee, 32);

        txtTotalAmount = new JTextField("100.0");
        txtTotalAmount.setEditable(false);
        txtTotalAmount.setBackground(new Color(245, 248, 252));
        styleInputField(txtTotalAmount, 32);

        KeyAdapter feeListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateTotal();
            }
        };
        txtConsultationFee.addKeyListener(feeListener);
        txtTreatmentFee.addKeyListener(feeListener);
        txtMedicineFee.addKeyListener(feeListener);

        comboStatus = new JComboBox<>(new String[]{"Paid", "Pending", "Partial"});
        styleComboBox(comboStatus);

        comboPaymentMethod = new JComboBox<>(new String[]{"Cash", "Card", "Insurance", "Bank Transfer"});
        styleComboBox(comboPaymentMethod);

        txtBillDate = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        styleInputField(txtBillDate, 32);

        fields.add(createFieldRow("Invoice ID:", txtBillId));
        fields.add(Box.createVerticalStrut(5));
        fields.add(createFieldRow("Patient Name *:", txtPatient));
        fields.add(Box.createVerticalStrut(5));
        fields.add(createFieldRow("Attending Doctor *:", txtDoctor));
        fields.add(Box.createVerticalStrut(5));

        JPanel feesRow = new JPanel(new GridLayout(1, 3, 5, 0));
        feesRow.setOpaque(false);
        feesRow.add(createFieldRow("Consult ($):", txtConsultationFee));
        feesRow.add(createFieldRow("Treat ($):", txtTreatmentFee));
        feesRow.add(createFieldRow("Med ($):", txtMedicineFee));
        fields.add(feesRow);
        fields.add(Box.createVerticalStrut(5));

        fields.add(createFieldRow("Total Amount ($):", txtTotalAmount));
        fields.add(Box.createVerticalStrut(5));

        JPanel statusMethodRow = new JPanel(new GridLayout(1, 2, 8, 0));
        statusMethodRow.setOpaque(false);
        statusMethodRow.add(createFieldRow("Status:", comboStatus));
        statusMethodRow.add(createFieldRow("Payment Method:", comboPaymentMethod));
        fields.add(statusMethodRow);
        fields.add(Box.createVerticalStrut(5));

        fields.add(createFieldRow("Billing Date:", txtBillDate));

        JScrollPane formScroll = new JScrollPane(fields);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        card.add(formScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnGenerate = createButton("Create Bill", blue, Color.WHITE, 12);
        JButton btnUpdate = createButton("Update", new Color(40, 150, 90), Color.WHITE, 12);
        JButton btnReceipt = createButton("Receipt Preview", new Color(100, 70, 170), Color.WHITE, 12);
        JButton btnClear = createButton("Clear", new Color(235, 241, 248), navy, 12);

        btnGenerate.addActionListener(e -> addBill());
        btnUpdate.addActionListener(e -> updateBill());
        btnReceipt.addActionListener(e -> showReceiptDialog());
        btnClear.addActionListener(e -> clearForm());

        actions.add(btnGenerate);
        actions.add(btnUpdate);
        actions.add(btnReceipt);
        actions.add(btnClear);

        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private void calculateTotal() {
        try {
            double c = parseSafe(txtConsultationFee.getText());
            double t = parseSafe(txtTreatmentFee.getText());
            double m = parseSafe(txtMedicineFee.getText());
            txtTotalAmount.setText(String.format("%.2f", c + t + m));
        } catch (Exception ignored) {
        }
    }

    private double parseSafe(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(text.replace("$", "").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
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

        JLabel title = new JLabel("Invoices & Billing History");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel info = new JLabel("Select an invoice to edit details or generate a receipt");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(textMuted);

        header.add(title, BorderLayout.WEST);
        header.add(info, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        String[] columns = {"Invoice ID", "Patient", "Doctor", "Consult ($)", "Treat ($)", "Med ($)", "Total ($)", "Status", "Method", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        billTable.setRowSorter(rowSorter);
        billTable.setRowHeight(32);
        billTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        billTable.setGridColor(new Color(235, 240, 245));
        billTable.setShowVerticalLines(false);
        billTable.setSelectionBackground(new Color(220, 235, 252));
        billTable.setSelectionForeground(navy);

        JTableHeader th = billTable.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setBackground(new Color(245, 248, 252));
        th.setForeground(navy);
        th.setPreferredSize(new Dimension(0, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        int[] widths = {75, 120, 120, 75, 70, 65, 80, 75, 75, 85};
        for (int i = 0; i < widths.length; i++) {
            billTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        billTable.getColumnModel().getColumn(0).setCellRenderer(center);
        billTable.getColumnModel().getColumn(3).setCellRenderer(center);
        billTable.getColumnModel().getColumn(4).setCellRenderer(center);
        billTable.getColumnModel().getColumn(5).setCellRenderer(center);
        billTable.getColumnModel().getColumn(6).setCellRenderer(center);
        billTable.getColumnModel().getColumn(9).setCellRenderer(center);

        billTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if (!isSelected) {
                    if ("Paid".equalsIgnoreCase(val)) {
                        label.setForeground(new Color(30, 140, 70));
                    } else if ("Pending".equalsIgnoreCase(val)) {
                        label.setForeground(new Color(210, 40, 40));
                    } else {
                        label.setForeground(new Color(210, 120, 20));
                    }
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                }
                return label;
            }
        });

        billTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = billTable.getSelectedRow();
                if (row != -1) {
                    populateFormFromRow(billTable.convertRowIndexToModel(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderLight, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void populateFormFromRow(int modelRow) {
        txtBillId.setText(String.valueOf(tableModel.getValueAt(modelRow, 0)));
        txtPatient.setText(String.valueOf(tableModel.getValueAt(modelRow, 1)));
        txtDoctor.setText(String.valueOf(tableModel.getValueAt(modelRow, 2)));
        txtConsultationFee.setText(String.valueOf(tableModel.getValueAt(modelRow, 3)));
        txtTreatmentFee.setText(String.valueOf(tableModel.getValueAt(modelRow, 4)));
        txtMedicineFee.setText(String.valueOf(tableModel.getValueAt(modelRow, 5)));
        txtTotalAmount.setText(String.valueOf(tableModel.getValueAt(modelRow, 6)));
        comboStatus.setSelectedItem(String.valueOf(tableModel.getValueAt(modelRow, 7)));
        comboPaymentMethod.setSelectedItem(String.valueOf(tableModel.getValueAt(modelRow, 8)));
        txtBillDate.setText(String.valueOf(tableModel.getValueAt(modelRow, 9)));
    }

    private Bill parseBillFromForm() {
        String id = txtBillId.getText().trim();
        String patient = txtPatient.getText().trim();
        String doctor = txtDoctor.getText().trim();
        String date = txtBillDate.getText().trim();

        if (patient.isEmpty() || doctor.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Patient, Doctor, and Billing Date are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        double consult = parseSafe(txtConsultationFee.getText());
        double treat = parseSafe(txtTreatmentFee.getText());
        double med = parseSafe(txtMedicineFee.getText());
        double total = consult + treat + med;

        return new Bill(
                id,
                patient,
                doctor,
                consult,
                treat,
                med,
                total,
                (String) comboStatus.getSelectedItem(),
                (String) comboPaymentMethod.getSelectedItem(),
                date
        );
    }

    private void addBill() {
        Bill bill = parseBillFromForm();
        if (bill == null) return;

        if (billServices.addBill(bill)) {
            tableModel.addRow(new Object[]{
                    bill.getId(), bill.getPatient(), bill.getDoctor(),
                    bill.getConsultationFee(), bill.getTreatmentFee(), bill.getMedicineFee(),
                    bill.getTotalAmount(), bill.getStatus(), bill.getPaymentMethod(), bill.getBillDate()
            });
            JOptionPane.showMessageDialog(this,
                    "Invoice " + bill.getId() + " created successfully.",
                    "Invoice Created",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            updateRevenueSummary();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save invoice to database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBill() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an invoice from the table to update.",
                    "No Invoice Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Bill bill = parseBillFromForm();
        if (bill == null) return;

        int modelRow = billTable.convertRowIndexToModel(selectedRow);
        if (billServices.updateBill(bill)) {
            tableModel.setValueAt(bill.getPatient(), modelRow, 1);
            tableModel.setValueAt(bill.getDoctor(), modelRow, 2);
            tableModel.setValueAt(bill.getConsultationFee(), modelRow, 3);
            tableModel.setValueAt(bill.getTreatmentFee(), modelRow, 4);
            tableModel.setValueAt(bill.getMedicineFee(), modelRow, 5);
            tableModel.setValueAt(bill.getTotalAmount(), modelRow, 6);
            tableModel.setValueAt(bill.getStatus(), modelRow, 7);
            tableModel.setValueAt(bill.getPaymentMethod(), modelRow, 8);
            tableModel.setValueAt(bill.getBillDate(), modelRow, 9);

            JOptionPane.showMessageDialog(this,
                    "Invoice updated successfully.",
                    "Update Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            updateRevenueSummary();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update invoice in database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReceiptDialog() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an invoice from the table to preview receipt.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = billTable.convertRowIndexToModel(selectedRow);
        String id = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String patient = String.valueOf(tableModel.getValueAt(modelRow, 1));
        String doctor = String.valueOf(tableModel.getValueAt(modelRow, 2));
        String consult = String.valueOf(tableModel.getValueAt(modelRow, 3));
        String treat = String.valueOf(tableModel.getValueAt(modelRow, 4));
        String med = String.valueOf(tableModel.getValueAt(modelRow, 5));
        String total = String.valueOf(tableModel.getValueAt(modelRow, 6));
        String status = String.valueOf(tableModel.getValueAt(modelRow, 7));
        String method = String.valueOf(tableModel.getValueAt(modelRow, 8));
        String date = String.valueOf(tableModel.getValueAt(modelRow, 9));

        JTextArea receiptArea = new JTextArea(16, 32);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setEditable(false);

        String receiptText = "========================================\n"
                + "         MEDICARE HOSPITAL INVOICE       \n"
                + "========================================\n"
                + " Invoice ID : " + id + "\n"
                + " Date       : " + date + "\n"
                + " Patient    : " + patient + "\n"
                + " Physician  : " + doctor + "\n"
                + "----------------------------------------\n"
                + " Items & Charges:\n"
                + String.format("   Consultation Fee : $ %8s\n", consult)
                + String.format("   Treatment/Ward   : $ %8s\n", treat)
                + String.format("   Medicines/Pharma : $ %8s\n", med)
                + "----------------------------------------\n"
                + String.format(" TOTAL AMOUNT       : $ %8s\n", total)
                + " Status             : " + status.toUpperCase() + "\n"
                + " Payment Method     : " + method + "\n"
                + "========================================\n"
                + "       Thank you for choosing MediCare! \n"
                + "========================================\n";

        receiptArea.setText(receiptText);

        JScrollPane scroll = new JScrollPane(receiptArea);
        scroll.setBorder(BorderFactory.createLineBorder(borderLight, 1));

        JOptionPane.showMessageDialog(this, scroll, "Official Patient Receipt: " + id, JOptionPane.PLAIN_MESSAGE);
    }

    private void clearForm() {
        txtBillId.setText(billServices.getNextBillId());
        txtPatient.setText("");
        txtDoctor.setText("");
        txtConsultationFee.setText("100.0");
        txtTreatmentFee.setText("0.0");
        txtMedicineFee.setText("0.0");
        txtTotalAmount.setText("100.0");
        comboStatus.setSelectedIndex(0);
        comboPaymentMethod.setSelectedIndex(0);
        txtBillDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        billTable.clearSelection();
        txtPatient.requestFocusInWindow();
    }

    private void updateRevenueSummary() {
        double total = 0.0;
        int paidCount = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += parseSafe(String.valueOf(tableModel.getValueAt(i, 6)));
            if ("Paid".equalsIgnoreCase(String.valueOf(tableModel.getValueAt(i, 7)))) {
                paidCount++;
            }
        }
        totalRevenueLabel.setText(String.format("Total: $%.2f | Paid Invoices: %d", total, paidCount));
    }

    private void loadBillsFromDatabase() {
        tableModel.setRowCount(0);
        for (Bill b : billServices.getAllBills()) {
            tableModel.addRow(new Object[]{
                    b.getId(), b.getPatient(), b.getDoctor(),
                    b.getConsultationFee(), b.getTreatmentFee(), b.getMedicineFee(),
                    b.getTotalAmount(), b.getStatus(), b.getPaymentMethod(), b.getBillDate()
            });
        }
        updateRevenueSummary();
        txtBillId.setText(billServices.getNextBillId());
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

