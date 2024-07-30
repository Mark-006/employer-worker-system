package com.cbozan.view.add;

import com.cbozan.view.helper.Observer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WorkerPaymentPanel extends JPanel implements Observer, ActionListener {

    private static final long serialVersionUID = 1L;

    private final int LLX = 100;
    private final int RLX = 480;
    private final int LLY = 220;
    private final int LLW = 200;
    private final int RLW = 500;
    private final int LH = 25;
    private final int SHS = 1;
    private final int MHS = 10;

    private JLabel imageLabel;
    private JLabel workerLabel, jobLabel, paytypeLabel, amountLabel;
    private JTextField amountTextField;
    private JTextField workerIdTextField;
    private JTextField jobTextField;
    private JComboBox<String> paytypeComboBox;
    private JButton payButton;

    private JTable paymentTable;
    private DefaultTableModel paymentTableModel;

    private final String[] paymentTableColumns = {"Worker ID", "Job", "Payment Method", "Amount", "Date"};

    public WorkerPaymentPanel() {
        super();
        setLayout(null);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setIcon(new ImageIcon("src/icon/new_worker_payment.png"));
        imageLabel.setBounds(LLX, 40, 128, 130);
        add(imageLabel);

        workerLabel = new JLabel("Worker ID");
        workerLabel.setBounds(LLX, LLY, LLW, LH);
        add(workerLabel);

        workerIdTextField = new JTextField();
        workerIdTextField.setBounds(workerLabel.getX(), workerLabel.getY() + LH + SHS, LLW, LH);
        add(workerIdTextField);

        jobLabel = new JLabel("Job selection");
        jobLabel.setBounds(workerIdTextField.getX(), workerIdTextField.getY() + LH + MHS, LLW, LH);
        add(jobLabel);

        jobTextField = new JTextField();
        jobTextField.setBounds(jobLabel.getX(), jobLabel.getY() + LH + SHS, LLW, LH);
        add(jobTextField);

        paytypeLabel = new JLabel("Payment method");
        paytypeLabel.setBounds(jobTextField.getX(), jobTextField.getY() + LH + MHS, LLW, LH);
        add(paytypeLabel);

        String[] paymentMethods = {"Mpesa", "Paypal", "Bank"};
        paytypeComboBox = new JComboBox<>(paymentMethods);
        paytypeComboBox.setBounds(paytypeLabel.getX(), paytypeLabel.getY() + LH + SHS, LLW, LH);
        add(paytypeComboBox);

        amountLabel = new JLabel("Amount of payment");
        amountLabel.setBounds(paytypeComboBox.getX(), paytypeComboBox.getY() + LH + MHS, LLW, LH);
        add(amountLabel);

        amountTextField = new JTextField();
        amountTextField.setBounds(amountLabel.getX(), amountLabel.getY() + LH + SHS, LLW, LH);
        amountTextField.setHorizontalAlignment(SwingConstants.CENTER);
        amountTextField.addActionListener(this);
        add(amountTextField);

        payButton = new JButton("PAY (SAVE)");
        payButton.setBounds(amountTextField.getX(), amountTextField.getY() + 60, amountTextField.getWidth(), 30);
        payButton.setFocusPainted(false);
        payButton.addActionListener(this);
        add(payButton);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(RLX, workerLabel.getY(), RLW, 260);
        add(scrollPane);

        paymentTableModel = new DefaultTableModel();
        paymentTable = new JTable(paymentTableModel);
        scrollPane.setViewportView(paymentTable);

        for (String column : paymentTableColumns) {
            paymentTableModel.addColumn(column);
        }

        loadDataFromDatabase();  // Load existing payment data from database
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == payButton) {
            handlePayment();
        }
    }

    private void handlePayment() {
        String amountText = amountTextField.getText().trim();
        String workerIdText = workerIdTextField.getText().trim();

        if (workerIdText.isEmpty() || workerIdText.length() != 6 || !workerIdText.matches("\\d{6}")) {
            JOptionPane.showMessageDialog(this, "Worker ID must be a 6-digit number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!amountText.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountText);
                String selectedJobText = jobTextField.getText().trim();
                String selectedPaymentMethod = (String) paytypeComboBox.getSelectedItem();
                LocalDate currentDate = LocalDate.now(); // Get current date

                // Format current date as yyyy-MM-dd
                String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // Add row to paymentTableModel with payment details
                paymentTableModel.addRow(new Object[]{workerIdText, selectedJobText, selectedPaymentMethod, amount, formattedDate});

                // Save payment details to database
                savePaymentToDatabase(workerIdText, selectedJobText, selectedPaymentMethod, amount, currentDate);

                // Clear input fields after payment
                clearFields();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount format", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Amount cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        amountTextField.setText("");
        workerIdTextField.setText("");
        jobTextField.setText("");
        paytypeComboBox.setSelectedIndex(0);
    }

    private void savePaymentToDatabase(String workerId, String job, String paytype, double amount, LocalDate date) {
        String url = "jdbc:postgresql://localhost:5432/employer_worker_db";
        String user = "postgres";
        String password = "Mark1234";

        String sql = "INSERT INTO payment (worker_id, job, paytype, amount, date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, workerId);
            pstmt.setString(2, job);
            pstmt.setString(3, paytype);
            pstmt.setDouble(4, amount);
            pstmt.setDate(5, java.sql.Date.valueOf(date)); // Convert LocalDate to java.sql.Date

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save payment data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataFromDatabase() {
        String url = "jdbc:postgresql://localhost:5432/employer_worker_db";
        String user = "postgres";
        String password = "Mark1234";

        String sql = "SELECT worker_id, job, paytype, amount, date FROM payment";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String workerId = rs.getString("worker_id");
                String job = rs.getString("job");
                String paytype = rs.getString("paytype");
                double amount = rs.getDouble("amount");
                Date date = rs.getDate("date"); // Use java.sql.Date

                LocalDate localDate = null;
                if (date != null) {
                    localDate = date.toLocalDate(); // Convert java.sql.Date to LocalDate
                }

                paymentTableModel.addRow(new Object[]{workerId, job, paytype, amount, localDate});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load payment data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update() {
        // Implement update method if needed
    }
}
