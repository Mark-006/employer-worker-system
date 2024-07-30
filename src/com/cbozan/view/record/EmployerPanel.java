package com.cbozan.view.record;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.cbozan.dao.EmployerDAO;
import com.cbozan.entity.Employer;
import com.cbozan.exception.EntityException;
import com.cbozan.view.component.RecordTextField;
import com.cbozan.view.helper.Control;
import com.cbozan.view.helper.Observer;

public class EmployerPanel extends JPanel implements Observer, ActionListener {

    private static final long serialVersionUID = -5013773197522265980L;
    private final List<Observer> observers;

    private final int LY = 250;
    private final int LX = 330;
    private final int TW = 190;
    private final int TH = 25;
    private final int LW = 95;
    private final int LH = 25;
    private final int LVS = 40;
    private final int LHS = 30;
    private final int BW = 80;
    private final int BH = 30;

    private JLabel imageLabel;
    private JLabel employerIdLabel;
    private JLabel fnameLabel, lnameLabel, phoneNumberLabel, descriptionLabel;
    private RecordTextField fnameTextField, lnameTextField, employerIdTextField, phoneNumberTextField;
    private JScrollPane descriptionScrollPane;
    private JButton saveButton;

    public EmployerPanel() {
        super();
        setLayout(null);

        observers = new ArrayList<>();
        subscribe(this);

        initializeGUI();
    }

    private void initializeGUI() {
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setIcon(new ImageIcon("src\\icon\\new_employer.png"));
        imageLabel.setBounds(LX + 155, 40, 128, 130);
        add(imageLabel);

        fnameLabel = new JLabel("Name");
        fnameLabel.setBounds(LX, LY, LW, LH);
        add(fnameLabel);

        fnameTextField = new RecordTextField(RecordTextField.REQUIRED_TEXT);
        fnameTextField.setBounds(fnameLabel.getX() + LW + LHS, fnameLabel.getY(), TW, TH);
        fnameTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!fnameTextField.getText().replaceAll("\\s+", "").equals("")) {
                    lnameTextField.requestFocus();
                }
            }
        });
        add(fnameTextField);

        lnameLabel = new JLabel("Surname");
        lnameLabel.setBounds(fnameLabel.getX(), fnameLabel.getY() + LVS, LW, LH);
        add(lnameLabel);

        lnameTextField = new RecordTextField(RecordTextField.REQUIRED_TEXT);
        lnameTextField.setBounds(lnameLabel.getX() + LW + LHS, lnameLabel.getY(), TW, TH);
        lnameTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!lnameTextField.getText().replaceAll("\\s+", "").equals("")) {
                    phoneNumberTextField.requestFocus();
                }
            }
        });
        add(lnameTextField);

        employerIdLabel = new JLabel("Employer ID");
        employerIdLabel.setBounds(lnameLabel.getX(), lnameLabel.getY() + LVS, LW, LH);
        add(employerIdLabel);

        employerIdTextField = new RecordTextField(RecordTextField.REQUIRED_TEXT);
        employerIdTextField.setBounds(employerIdLabel.getX() + LW + LHS, employerIdLabel.getY(), TW, TH);
        employerIdTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!employerIdTextField.getText().replaceAll("\\s+", "").equals("")) {
                    phoneNumberTextField.requestFocus();
                }
            }
        });
        add(employerIdTextField);

        phoneNumberLabel = new JLabel("Phone Number");
        phoneNumberLabel.setBounds(employerIdLabel.getX(), employerIdLabel.getY() + LVS, LW, LH);
        add(phoneNumberLabel);

        phoneNumberTextField = new RecordTextField(RecordTextField.PHONE_NUMBER_TEXT + RecordTextField.REQUIRED_TEXT);
        phoneNumberTextField.setBounds(phoneNumberLabel.getX() + LW + LHS, phoneNumberLabel.getY(), TW, TH);
        phoneNumberTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Control.phoneNumberControl(phoneNumberTextField.getText())) {
                    ((JTextArea) ((JViewport) descriptionScrollPane.getComponent(0)).getComponent(0)).requestFocus();
                }
            }
        });
        add(phoneNumberTextField);

        descriptionLabel = new JLabel("Description");
        descriptionLabel.setBounds(phoneNumberLabel.getX(), phoneNumberLabel.getY() + LVS, LW, LH);
        add(descriptionLabel);

        descriptionScrollPane = new JScrollPane(new JTextArea());
        descriptionScrollPane.setBounds(descriptionLabel.getX() + LW + LHS, descriptionLabel.getY(), TW, TH * 3);
        add(descriptionScrollPane);

        saveButton = new JButton("SAVE");
        saveButton.setBounds(descriptionScrollPane.getX() + ((TW - BW) / 2), descriptionScrollPane.getY() + descriptionScrollPane.getHeight() + 20, BW, BH);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(this);
        add(saveButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            try {
                save(e);
            } catch (EntityException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void save(ActionEvent e) throws EntityException {
        String fname = fnameTextField.getText().trim();
        String lname = lnameTextField.getText().trim();
        String phoneNumber = phoneNumberTextField.getText().replaceAll("\\s+", "");
        String description = ((JTextArea) ((JViewport) descriptionScrollPane.getComponent(0)).getComponent(0)).getText().trim();

        if (fname.isEmpty() || lname.isEmpty() || !Control.phoneNumberControl(phoneNumber)) {
            String message = "Please fill in required fields or enter the Phone Number format correctly";
            JOptionPane.showMessageDialog(this, message, "ERROR", JOptionPane.ERROR_MESSAGE);
        } else {
            Employer.EmployerBuilder builder = new Employer.EmployerBuilder();
            builder.setEmployerId(Integer.parseInt(employerIdTextField.getText().trim())); // Assuming employerId is an integer
            builder.setName(fname);
            builder.setSurname(lname);
            builder.setPhoneNumber(Arrays.asList(phoneNumber));
            builder.setDescription(description);

            Employer employer = builder.build();

            if (EmployerDAO.getInstance().create(employer)) {
                JOptionPane.showMessageDialog(this, "Registration Successful");
                notifyAllObservers();
                clearPanel();
            } else {
                JOptionPane.showMessageDialog(this, "Not saved", "Database error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearPanel() {
        fnameTextField.setText("");
        lnameTextField.setText("");
        employerIdTextField.setText("");
        phoneNumberTextField.setText("");
        ((JTextArea) ((JViewport) descriptionScrollPane.getComponent(0)).getComponent(0)).setText("");

        fnameTextField.setBorder(new LineBorder(Color.white));
        lnameTextField.setBorder(new LineBorder(Color.white));
        employerIdTextField.setBorder(new LineBorder(Color.white));
        phoneNumberTextField.setBorder(new LineBorder(Color.white));
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
        clearPanel();
    }
}
