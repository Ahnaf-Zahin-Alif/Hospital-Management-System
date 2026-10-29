package hospital.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AppointmentPanel extends JPanel {
    private JTextField patientIdField, doctorIdField, dateField;
    private JComboBox<String> statusCombo;
    private DefaultTableModel tableModel;

    public AppointmentPanel() {  
       
        setLayout(new BorderLayout(10, 10));  
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));  
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Appointment"));  
        formPanel.setPreferredSize(new Dimension(300, 0));  

        patientIdField = new JTextField(15);  
        doctorIdField = new JTextField(15);  
        dateField = new JTextField("2026-10-15", 15);  
        String[] statuses = {"Scheduled", "Completed", "Cancelled"};  
        statusCombo = new JComboBox<>(statuses);

        JButton bookBtn = new JButton("Book");  
        JButton clearBtn = new JButton("Clear");

        
        formPanel.add(new JLabel("Patient ID:"));
        formPanel.add(patientIdField);

        formPanel.add(new JLabel("Doctor ID:"));
        formPanel.add(doctorIdField);

        formPanel.add(new JLabel("Date:"));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusCombo);

        formPanel.add(clearBtn);
        formPanel.add(bookBtn);

        
        String[] columns = {"Appt ID", "Patient ID", "Doctor ID", "Date", "Status"};  
        tableModel = new DefaultTableModel(columns, 0);  
        JTable table = new JTable(tableModel);  
        JScrollPane scrollPane = new JScrollPane(table);  
        scrollPane.setBorder(BorderFactory.createTitledBorder("Scheduled Appointments"));

        
        bookBtn.addActionListener(e -> {  
            String pId = patientIdField.getText().trim();  
            String dId = doctorIdField.getText().trim();  
            String date = dateField.getText().trim();  
            String status = (String) statusCombo.getSelectedItem();

            if (pId.isEmpty() || dId.isEmpty() || date.isEmpty()) {  
                JOptionPane.showMessageDialog(this, "All fields are required!", "Warning", JOptionPane.WARNING_MESSAGE);  
                return;  
            }

            int id = tableModel.getRowCount() + 1;  
            tableModel.addRow(new Object[]{id, pId, dId, date, status});  
            patientIdField.setText("");  
            doctorIdField.setText("");  
        });

        clearBtn.addActionListener(e -> {  
            patientIdField.setText("");  
            doctorIdField.setText("");  
            dateField.setText("2026-10-15");  
            statusCombo.setSelectedIndex(0);  
        });

        
        add(formPanel, BorderLayout.WEST);  
        add(scrollPane, BorderLayout.CENTER);  
    }

    public static void main(String[] args) {  
        SwingUtilities.invokeLater(() -> {  
            JFrame frame = new JFrame("Basic GUI Preview - Appointment Panel");  
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
            frame.setSize(800, 400);  
            frame.setLocationRelativeTo(null);  
            frame.add(new AppointmentPanel());  
            frame.setVisible(true);  
        });  
    }  
}
