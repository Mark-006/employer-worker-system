package com.cbozan.view.add;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.cbozan.dao.InvoiceDAO;
import com.cbozan.dao.JobDAO;
import com.cbozan.entity.Invoice;
import com.cbozan.entity.Job;
import com.cbozan.exception.EntityException;
import com.cbozan.view.component.SearchBox;
import com.cbozan.view.helper.Observer;

public class JobPaymentPanel extends JPanel implements Observer, FocusListener, ActionListener {

    private static final long serialVersionUID = 291336645961737012L;
    private final List<Observer> observers;

    private final int LLX = 100; // Left label x position
    private final int RLX = 480; // Right label x position
    private final int LLY = 220; // Left Label y position
    private final int RLY = 30; // Right Label y position
    private final int LLW = 200; // Left Label width
    private final int RLW = 500; // Right Label width
    private final int LH = 25; // Label height
    private final int SHS = 5; // Small height space
    private final int MHS = 15; // Mid height space

    private final String[] invoiceTableColumns = {"ID", "Amount", "Date"};

    private JLabel imageLabel, searchImageLabel;
    private JLabel amountLabel, jobTitleLabel, employerLabel;
    private JTextField jobTitleTextField, employerTextField;
    private JTextField amountTextField;
    private JButton takePaymentButton;
    private JScrollPane lastPaymentsScroll;
    private SearchBox searchJobSearchBox;
    private Job selectedJob;
    private Color defaultColor;

    public JobPaymentPanel() {
        super();
        setLayout(null);

        observers = new ArrayList<>();
        subscribe(this);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setIcon(new ImageIcon("src\\icon\\new_job_payment.png"));
        imageLabel.setBounds(LLX, 40, 128, 130);
        add(imageLabel);

        defaultColor = imageLabel.getForeground();
        selectedJob = null;

        jobTitleLabel = new JLabel("Job title");
        jobTitleLabel.setBounds(LLX, LLY, LLW, LH);
        add(jobTitleLabel);

        jobTitleTextField = new JTextField();
        jobTitleTextField.setBounds(jobTitleLabel.getX(), jobTitleLabel.getY() + LH + SHS, LLW, LH);
        jobTitleTextField.setEditable(true); 
        add(jobTitleTextField);

        employerLabel = new JLabel("Employer");
        employerLabel.setBounds(jobTitleTextField.getX(), jobTitleTextField.getY() + LH + MHS, LLW, LH);
        add(employerLabel);

        employerTextField = new JTextField();
        employerTextField.setBounds(employerLabel.getX(), employerLabel.getY() + LH + SHS, LLW, LH);
        employerTextField.setEditable(true); 
        add(employerTextField);

        amountLabel = new JLabel("Amount of payment");
        amountLabel.setBounds(employerTextField.getX(), employerTextField.getY() + LH + SHS + MHS + MHS, LLW, LH);
        add(amountLabel);

        amountTextField = new JTextField();
        amountTextField.setBounds(amountLabel.getX(), amountLabel.getY() + LH + SHS, LLW, LH);
        amountTextField.setHorizontalAlignment(SwingConstants.CENTER);
        amountTextField.addFocusListener(this);
        amountTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!amountTextField.getText().replaceAll("\\s+", "").equals("")
                        && decimalControl(amountTextField.getText())) {

                    takePaymentButton.doClick();

                }
            }
        });
        add(amountTextField);

        takePaymentButton = new JButton("TAKE PAYMENT (SAVE)");
        takePaymentButton.setBounds(amountTextField.getX(), amountTextField.getY() + LH + MHS + SHS, amountTextField.getWidth(), 30);
        takePaymentButton.setFocusPainted(false);
        takePaymentButton.addActionListener(this);
        add(takePaymentButton);

        searchImageLabel = new JLabel(new ImageIcon("src\\icon\\search.png"));
        searchImageLabel.setBounds(RLX - 32 + RLW / 2, RLY, 64, 64);
        add(searchImageLabel);

        searchJobSearchBox = new SearchBox(JobDAO.getInstance().list(), new Dimension(RLW, LH)) {
            private static final long serialVersionUID = 1L;

            @Override
            public void mouseAction(MouseEvent e, Object searchResultObject, int chooseIndex) {
                if (searchResultObject instanceof Job) {
                    selectedJob = (Job) searchResultObject;
                    jobTitleTextField.setText(selectedJob.toString());
                    employerTextField.setText(selectedJob.getEmployer().toString());
                    clearPanel();
                }    
                super.mouseAction(e, searchResultObject, chooseIndex);
            }
        };
        searchJobSearchBox.setBounds(RLX, searchImageLabel.getY() + 64 + MHS, RLW, LH);
        add(searchJobSearchBox);

        searchJobSearchBox.getPanel().setBounds(RLX, searchJobSearchBox.getY() + searchJobSearchBox.getHeight(), searchJobSearchBox.getWidth(), 0);
        add(searchJobSearchBox.getPanel());

        lastPaymentsScroll = new JScrollPane(new JTable(new String[][]{}, invoiceTableColumns) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }

            {
                setRowHeight(30);
                setShowVerticalLines(false);
                setShowHorizontalLines(false);
            }
        });
        lastPaymentsScroll.setBounds(RLX, jobTitleTextField.getY(), RLW, 255);
        add(lastPaymentsScroll);

        clearPanel();
    }

    private boolean decimalControl(String... args) {
        Pattern pattern = Pattern.compile("^\\d+(\\.\\d{1,2})?$"); // decimal pattern xxx.xx
        boolean result = true;

        for (String arg : args)
            result = result && pattern.matcher(arg.replaceAll("\\s+", "")).find();

        return result;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() instanceof JTextField) {

            ((JTextField) e.getSource()).setBorder(new LineBorder(Color.blue));

            if (((JTextField) e.getSource()) == amountTextField) {
                amountLabel.setForeground(Color.blue);
            }

        }

    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource() instanceof JTextField) {

            Color color = Color.white;

            if (decimalControl(((JTextField) e.getSource()).getText())) {
                color = new Color(0, 180, 0);
            } else {
                color = Color.red;
            }

            ((JTextField) e.getSource()).setBorder(new LineBorder(color));

            if (((JTextField) e.getSource()) == amountTextField) {
                amountLabel.setForeground(color);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == takePaymentButton) {

            String amount = amountTextField.getText().replaceAll("\\s+", "");

            if (!decimalControl(amount) || selectedJob == null) {

                String message = "Please select a job and enter a valid amount (max 2 decimal places)";
                JOptionPane.showMessageDialog(this, message, "ERROR", JOptionPane.ERROR_MESSAGE);

            } else {

                JTextArea jobTextArea = new JTextArea(selectedJob.toString());
                jobTextArea.setEditable(false);

                JTextArea amountTextArea = new JTextArea(amount + " ₺");
                amountTextArea.setEditable(false);

                Object[] pane = {
                        new JLabel("Job"),
                        jobTextArea,
                        new JLabel("Amount of payment"),
                        amountTextArea
                };

                int result = JOptionPane.showOptionDialog(this, pane, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        new ImageIcon("src\\icon\\accounting_icon_1_32.png"), new Object[]{"SAVE", "CANCEL"}, "CANCEL");

                if (result == JOptionPane.YES_OPTION) {
                    Invoice.InvoiceBuilder builder = new Invoice.InvoiceBuilder();
                    builder.setId(Integer.MAX_VALUE);
                    builder.setJob(selectedJob);
                    builder.setAmount(new BigDecimal(amount));

                    Invoice invoice = null;
                    try {
                        invoice = builder.build();
                    } catch (EntityException e1) {
                        System.out.println(e1.getMessage());
                    }

                    if (InvoiceDAO.getInstance().create(invoice)) {
                        JOptionPane.showMessageDialog(this, "Payment saved successfully");
                        notifyAllObservers();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to save payment", "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }

        }

    }

    private void clearPanel() {
        amountTextField.setText("");
        amountTextField.setBorder(new LineBorder(Color.white));
        amountLabel.setForeground(defaultColor);

        if (selectedJob != null) {
            List<Invoice> invoiceList = InvoiceDAO.getInstance().list("job_id", selectedJob.getId());
            String[][] tableData = new String[invoiceList.size()][invoiceTableColumns.length];
            int i = 0;
            for (Invoice invoice : invoiceList) {
                tableData[i][0] = String.valueOf(invoice.getId());
                tableData[i][1] = NumberFormat.getInstance().format(invoice.getAmount()) + " ₺";
                tableData[i][2] = new SimpleDateFormat("dd.MM.yyyy").format(invoice.getDate());
                ++i;
            }

            JTable invoiceTable = new JTable(tableData, invoiceTableColumns);
            invoiceTable.setRowHeight(30);
            invoiceTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            invoiceTable.getColumnModel().getColumn(0).setPreferredWidth(5);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            invoiceTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            invoiceTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            invoiceTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

            lastPaymentsScroll.setViewportView(invoiceTable);
        } else {
            lastPaymentsScroll.setViewportView(new JTable());
        }
    }

    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    public void notifyAllObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    @Override
    public void update() {
        JobDAO.getInstance().refresh();
        JobDAO.getInstance().list();
        jobTitleTextField.setText(""); // Reset text fields
        employerTextField.setText("");
        clearPanel();
    }

}
