package hospital.ui;

import hospital.model.Medicine;
import hospital.services.MedicineServices;

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

public class PharmacyPanel extends JPanel {

    private final Color navy = new Color(15, 39, 71);
    private final Color blue = new Color(35, 112, 190);
    private final Color blueHover = new Color(28, 95, 165);
    private final Color bgLight = new Color(244, 247, 251);
    private final Color cardBg = Color.WHITE;
    private final Color borderLight = new Color(220, 230, 240);
    private final Color textDark = new Color(35, 45, 60);
    private final Color textMuted = new Color(105, 120, 135);

    private final MedicineServices medicineServices = new MedicineServices();

    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> comboCategory;
    private JTextField txtStockQty;
    private JTextField txtUnitPrice;
    private JTextField txtExpiryDate;
    private JTextField txtManufacturer;

    private JTextField txtSearch;
    private JComboBox<String> filterCategory;
    private JComboBox<String> filterStockStatus;
    private DefaultTableModel tableModel;
    private JTable medicineTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel statsLabel;

    public PharmacyPanel() {
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
        loadMedicinesFromDatabase();
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
        txtSearch.setToolTipText("Search by Name, ID, or Manufacturer");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        String[] categories = {
                "All Categories", "Antibiotics", "Analgesics / Antipyretic",
                "Cardiology", "Diabetes", "Anti-inflammatory",
                "Gastrointestinal", "Antihistamine", "Respiratory", "Vitamins & Supplements", "Other"
        };
        filterCategory = new JComboBox<>(categories);
        styleComboBox(filterCategory);
        filterCategory.setPreferredSize(new Dimension(170, 36));
        filterCategory.addActionListener(e -> applyFilters());

        String[] stockStatuses = {"All Stock", "In Stock (>=15)", "Low Stock (<15)", "Out of Stock (0)"};
        filterStockStatus = new JComboBox<>(stockStatuses);
        styleComboBox(filterStockStatus);
        filterStockStatus.setPreferredSize(new Dimension(145, 36));
        filterStockStatus.addActionListener(e -> applyFilters());

        JButton btnFilter = createButton("Filter", blue, Color.WHITE, 12);
        btnFilter.addActionListener(e -> applyFilters());

        JButton btnReset = createButton("Reset", new Color(235, 241, 248), navy, 12);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            filterCategory.setSelectedIndex(0);
            filterStockStatus.setSelectedIndex(0);
            rowSorter.setRowFilter(null);
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(filterCategory);
        searchPanel.add(filterStockStatus);
        searchPanel.add(btnFilter);
        searchPanel.add(btnReset);

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        badgePanel.setOpaque(false);

        statsLabel = new JLabel("Total Items: 0 | Low Stock: 0");
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
        String selectedCategory = (String) filterCategory.getSelectedItem();
        String selectedStock = (String) filterStockStatus.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!query.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
        }

        if (selectedCategory != null && !"All Categories".equals(selectedCategory)) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(selectedCategory), 2));
        }

        if (selectedStock != null && !"All Stock".equals(selectedStock)) {
            if ("Out of Stock (0)".equals(selectedStock)) {
                filters.add(RowFilter.regexFilter("(?i)Out of Stock", 7));
            } else if ("Low Stock (<15)".equals(selectedStock)) {
                filters.add(RowFilter.regexFilter("(?i)Low Stock", 7));
            } else if ("In Stock (>=15)".equals(selectedStock)) {
                filters.add(RowFilter.regexFilter("(?i)In Stock", 7));
            }
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

        JLabel title = new JLabel("Medicine Inventory");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel subtitle = new JLabel("Add, track stock, and manage hospital pharmacy");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(textMuted);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setOpaque(false);

        txtId = new JTextField(medicineServices.getNextMedicineId());
        txtId.setEditable(false);
        txtId.setBackground(new Color(245, 248, 252));
        styleInputField(txtId, 32);

        txtName = new JTextField();
        styleInputField(txtName, 32);

        String[] categories = {
                "Antibiotics", "Analgesics / Antipyretic", "Cardiology",
                "Diabetes", "Anti-inflammatory", "Gastrointestinal",
                "Antihistamine", "Respiratory", "Vitamins & Supplements", "Other"
        };
        comboCategory = new JComboBox<>(categories);
        comboCategory.setEditable(true);
        styleComboBox(comboCategory);

        txtStockQty = new JTextField("100");
        styleInputField(txtStockQty, 32);

        txtUnitPrice = new JTextField("10.00");
        styleInputField(txtUnitPrice, 32);

        txtExpiryDate = new JTextField(LocalDate.now().plusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
        styleInputField(txtExpiryDate, 32);

        txtManufacturer = new JTextField();
        styleInputField(txtManufacturer, 32);

        fields.add(createFieldRow("Medicine ID:", txtId));
        fields.add(Box.createVerticalStrut(6));
        fields.add(createFieldRow("Medicine Name *:", txtName));
        fields.add(Box.createVerticalStrut(6));
        fields.add(createFieldRow("Category:", comboCategory));
        fields.add(Box.createVerticalStrut(6));

        JPanel numRow = new JPanel(new GridLayout(1, 2, 8, 0));
        numRow.setOpaque(false);
        numRow.add(createFieldRow("Stock Quantity *:", txtStockQty));
        numRow.add(createFieldRow("Unit Price ($) *:", txtUnitPrice));
        fields.add(numRow);
        fields.add(Box.createVerticalStrut(6));

        fields.add(createFieldRow("Expiry Date (YYYY-MM-DD):", txtExpiryDate));
        fields.add(Box.createVerticalStrut(6));
        fields.add(createFieldRow("Manufacturer:", txtManufacturer));

        JScrollPane formScroll = new JScrollPane(fields);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        card.add(formScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(3, 2, 6, 6));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAdd = createButton("Add Medicine", blue, Color.WHITE, 12);
        JButton btnUpdate = createButton("Update", new Color(40, 150, 90), Color.WHITE, 12);
        JButton btnAdjustStock = createButton("\u00B1 Adjust Stock", new Color(100, 70, 170), Color.WHITE, 12);
        JButton btnDelete = createButton("Delete", new Color(210, 50, 50), Color.WHITE, 12);
        JButton btnClear = createButton("Clear Form", new Color(235, 241, 248), navy, 12);
        JButton btnQuickDispense = createButton("Dispense (-1)", new Color(220, 120, 30), Color.WHITE, 12);

        btnAdd.addActionListener(e -> addMedicine());
        btnUpdate.addActionListener(e -> updateMedicine());
        btnAdjustStock.addActionListener(e -> showAdjustStockDialog());
        btnDelete.addActionListener(e -> deleteMedicine());
        btnClear.addActionListener(e -> clearForm());
        btnQuickDispense.addActionListener(e -> quickDispense());

        actions.add(btnAdd);
        actions.add(btnUpdate);
        actions.add(btnAdjustStock);
        actions.add(btnQuickDispense);
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

        JLabel title = new JLabel("Medicine Catalog & Current Stock");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(navy);

        JLabel info = new JLabel("Click any row to view, update, or adjust stock levels");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(textMuted);

        header.add(title, BorderLayout.WEST);
        header.add(info, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Medicine Name", "Category", "Stock Qty", "Unit Price ($)", "Expiry Date", "Manufacturer", "Stock Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicineTable = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        medicineTable.setRowSorter(rowSorter);
        medicineTable.setRowHeight(32);
        medicineTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        medicineTable.setGridColor(new Color(235, 240, 245));
        medicineTable.setShowVerticalLines(false);
        medicineTable.setSelectionBackground(new Color(220, 235, 252));
        medicineTable.setSelectionForeground(navy);

        JTableHeader th = medicineTable.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setBackground(new Color(245, 248, 252));
        th.setForeground(navy);
        th.setPreferredSize(new Dimension(0, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        int[] widths = {80, 170, 140, 75, 90, 95, 120, 100};
        for (int i = 0; i < widths.length; i++) {
            medicineTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        medicineTable.getColumnModel().getColumn(0).setCellRenderer(center);
        medicineTable.getColumnModel().getColumn(3).setCellRenderer(center);
        medicineTable.getColumnModel().getColumn(4).setCellRenderer(center);
        medicineTable.getColumnModel().getColumn(5).setCellRenderer(center);

        medicineTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if (!isSelected) {
                    if ("In Stock".equalsIgnoreCase(val)) {
                        label.setForeground(new Color(30, 140, 70));
                    } else if ("Low Stock".equalsIgnoreCase(val)) {
                        label.setForeground(new Color(210, 120, 20));
                    } else {
                        label.setForeground(new Color(210, 40, 40));
                    }
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                }
                return label;
            }
        });

        medicineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = medicineTable.getSelectedRow();
                if (row != -1) {
                    populateFormFromRow(medicineTable.convertRowIndexToModel(row));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderLight, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void populateFormFromRow(int modelRow) {
        txtId.setText(String.valueOf(tableModel.getValueAt(modelRow, 0)));
        txtName.setText(String.valueOf(tableModel.getValueAt(modelRow, 1)));
        comboCategory.setSelectedItem(String.valueOf(tableModel.getValueAt(modelRow, 2)));
        txtStockQty.setText(String.valueOf(tableModel.getValueAt(modelRow, 3)));
        txtUnitPrice.setText(String.valueOf(tableModel.getValueAt(modelRow, 4)));
        txtExpiryDate.setText(String.valueOf(tableModel.getValueAt(modelRow, 5)));
        txtManufacturer.setText(String.valueOf(tableModel.getValueAt(modelRow, 6)));
    }

    private Medicine parseMedicineFromForm() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String category = comboCategory.getSelectedItem() != null ? comboCategory.getSelectedItem().toString().trim() : "Other";
        String manufacturer = txtManufacturer.getText().trim();
        String expiry = txtExpiryDate.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Medicine Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int stock;
        try {
            stock = Integer.parseInt(txtStockQty.getText().trim());
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stock Quantity must be a non-negative integer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        double price;
        try {
            price = Double.parseDouble(txtUnitPrice.getText().trim().replace("$", ""));
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Unit Price must be a valid positive amount.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Medicine(id, name, category, stock, price, expiry, manufacturer);
    }

    private void addMedicine() {
        Medicine m = parseMedicineFromForm();
        if (m == null) return;

        if (medicineServices.getMedicineById(m.getId()) != null) {
            m.setId(medicineServices.getNextMedicineId());
        }

        boolean success = medicineServices.addMedicine(m);
        if (success) {
            JOptionPane.showMessageDialog(this, "Medicine " + m.getName() + " added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMedicinesFromDatabase();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add medicine. Please verify database connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMedicine() {
        int selected = medicineTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Medicine m = parseMedicineFromForm();
        if (m == null) return;

        boolean success = medicineServices.updateMedicine(m);
        if (success) {
            JOptionPane.showMessageDialog(this, "Medicine " + m.getId() + " updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMedicinesFromDatabase();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update medicine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMedicine() {
        int selected = medicineTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = medicineTable.convertRowIndexToModel(selected);
        String id = String.valueOf(tableModel.getValueAt(modelRow, 0));
        String name = String.valueOf(tableModel.getValueAt(modelRow, 1));

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete medicine: " + name + " (" + id + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = medicineServices.deleteMedicine(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "Medicine deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMedicinesFromDatabase();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete medicine.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAdjustStockDialog() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();

        if (id.isEmpty() || medicineServices.getMedicineById(id) == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid medicine first.", "Select Medicine", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(
                this,
                "Enter stock adjustment for '" + name + "' (" + id + "):\n(Use positive number to add stock, negative to deduct)",
                "Adjust Stock Quantity",
                JOptionPane.PLAIN_MESSAGE
        );

        if (input != null && !input.trim().isEmpty()) {
            try {
                int delta = Integer.parseInt(input.trim());
                boolean ok = medicineServices.adjustStock(id, delta);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Stock adjusted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMedicinesFromDatabase();
                    Medicine updated = medicineServices.getMedicineById(id);
                    if (updated != null) {
                        txtStockQty.setText(String.valueOf(updated.getStockQty()));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot adjust stock. Resulting quantity cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid integer (e.g. 20 or -5).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void quickDispense() {
        String id = txtId.getText().trim();
        if (id.isEmpty() || medicineServices.getMedicineById(id) == null) {
            JOptionPane.showMessageDialog(this, "Please select a medicine first.", "Select Medicine", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = medicineServices.adjustStock(id, -1);
        if (ok) {
            loadMedicinesFromDatabase();
            Medicine updated = medicineServices.getMedicineById(id);
            if (updated != null) {
                txtStockQty.setText(String.valueOf(updated.getStockQty()));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Item is out of stock! Cannot dispense.", "Out of Stock", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearForm() {
        txtId.setText(medicineServices.getNextMedicineId());
        txtName.setText("");
        comboCategory.setSelectedIndex(0);
        txtStockQty.setText("100");
        txtUnitPrice.setText("10.00");
        txtExpiryDate.setText(LocalDate.now().plusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
        txtManufacturer.setText("");
        medicineTable.clearSelection();
    }

    private void loadMedicinesFromDatabase() {
        tableModel.setRowCount(0);
        List<Medicine> list = medicineServices.getAllMedicines();

        int totalCount = list.size();
        int lowStockCount = 0;

        for (Medicine m : list) {
            String status;
            if (m.getStockQty() == 0) {
                status = "Out of Stock";
                lowStockCount++;
            } else if (m.getStockQty() < 15) {
                status = "Low Stock";
                lowStockCount++;
            } else {
                status = "In Stock";
            }

            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getCategory(),
                    m.getStockQty(),
                    String.format("%.2f", m.getUnitPrice()),
                    m.getExpiryDate(),
                    m.getManufacturer(),
                    status
            });
        }

        statsLabel.setText("Total Items: " + totalCount + "  |  Low Stock: " + lowStockCount);
        if (lowStockCount > 0) {
            statsLabel.setForeground(new Color(210, 100, 20));
        } else {
            statsLabel.setForeground(navy);
        }
    }

    private void styleInputField(JTextField field, int height) {
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, height));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(textDark);
        field.setCaretColor(blue);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.setBackground(Color.WHITE);
        box.setForeground(textDark);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderLight, 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
    }

    private JButton createButton(String text, Color bg, Color fg, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));
        return btn;
    }
}

