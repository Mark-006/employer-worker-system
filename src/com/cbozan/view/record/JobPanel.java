package com.cbozan.view.record;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.cbozan.dao.EmployerDAO;
import com.cbozan.dao.JobDAO;
import com.cbozan.dao.PriceDAO;
import com.cbozan.entity.Employer;
import com.cbozan.entity.Job;
import com.cbozan.entity.Price;
import com.cbozan.exception.EntityException;
import com.cbozan.view.component.RecordTextField;
import com.cbozan.view.component.TextArea;
import com.cbozan.view.helper.Observer;

public class JobPanel extends JPanel implements ActionListener, Observer {

    private static final long serialVersionUID = 1L;

    private final int LY = 230;
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
    private JLabel titleLabel, employerLabel, priceLabel, descriptionLabel;
    private RecordTextField titleTextField;
    private JComboBox<Price> priceComboBox;
    private JComboBox<Employer> employerComboBox;
    private TextArea descriptionTextArea;
    private JButton saveButton;

    public JobPanel() {
        super();
        setLayout(null);

        imageLabel = new JLabel(new ImageIcon("src\\icon\\new_job.png"));
        imageLabel.setBounds(LX + 157, 50, 128, 128);
        add(imageLabel);

        titleLabel = new JLabel("Job Title");
        titleLabel.setBounds(LX, LY, LW, LH);
        add(titleLabel);

        titleTextField = new RecordTextField(RecordTextField.REQUIRED_TEXT);
        titleTextField.setBounds(LX + titleLabel.getWidth() + LHS, titleLabel.getY(), TW, TH);
        titleTextField.setHorizontalAlignment(SwingConstants.CENTER);
        titleTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!titleTextField.getText().replaceAll("\\s+", "").equals(""))
                    employerComboBox.requestFocus();
            }
        });
        add(titleTextField);

        employerLabel = new JLabel("Employer");
        employerLabel.setBounds(LX, titleLabel.getY() + LVS, LW, LH);
        add(employerLabel);

        employerComboBox = new JComboBox<>();
        employerComboBox.setBounds(LX + employerLabel.getWidth() + LHS, employerLabel.getY(), TW, TH);

        // Populate the employer combo box initially
        populateEmployerComboBox();

        employerComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Employer selectedEmployer = (Employer) employerComboBox.getSelectedItem();
                if (selectedEmployer != null) {
                    String message = "Selected Employer: " + selectedEmployer.getName();
                    JOptionPane.showMessageDialog(JobPanel.this, message, "Selected Employer", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        add(employerComboBox);

        priceLabel = new JLabel("Price");
        priceLabel.setBounds(LX, employerLabel.getY() + LVS, LW, LH);
        add(priceLabel);

        priceComboBox = new JComboBox<>(PriceDAO.getInstance().list().toArray(new Price[0]));
        priceComboBox.setBounds(LX + priceLabel.getWidth() + LHS, priceLabel.getY(), TW, TH);
        priceComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descriptionTextArea.getViewport().getComponent(0).requestFocus();
            }
        });
        add(priceComboBox);

        descriptionLabel = new JLabel("Description");
        descriptionLabel.setBounds(LX, priceLabel.getY() + LVS, LW, LH);
        add(descriptionLabel);

        descriptionTextArea = new TextArea();
        descriptionTextArea.setBounds(descriptionLabel.getX() + LW + LHS, descriptionLabel.getY(), TW, TH * 3);
        add(descriptionTextArea);

        saveButton = new JButton("SAVE");
        saveButton.setBounds(descriptionTextArea.getX() + ((TW - BW) / 2),
                descriptionTextArea.getY() + descriptionTextArea.getHeight() + 20, BW, BH);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(this);
        add(saveButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            String title = titleTextField.getText().trim().toUpperCase();
            Employer employer = (Employer) employerComboBox.getSelectedItem();
            Price price = (Price) priceComboBox.getSelectedItem();
            String description = descriptionTextArea.getText().trim().toUpperCase();

            if (title.isEmpty() || employer == null || price == null) {
                String message = "Please fill in or select the required fields.";
                JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Job.JobBuilder builder = new Job.JobBuilder();
                builder.setId(Integer.MAX_VALUE); // Set your job ID logic here
                builder.setTitle(title);
                builder.setEmployer(employer);
                builder.setPrice(price);
                builder.setDescription(description);

                Job job;
                try {
                    job = builder.build();
                    if (JobDAO.getInstance().create(job)) {
                        JOptionPane.showMessageDialog(this, "Job registration successful");
                        clearPanel();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: Job not saved", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (EntityException ex) {
                    JOptionPane.showMessageDialog(this, "Error creating job: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void populateEmployerComboBox() {
        List<Employer> employers = EmployerDAO.getInstance().list();
        if (employers.isEmpty()) {
            System.out.println("No employers found in database.");
        } else {
            System.out.println("Employers fetched from database: " + employers.size());
        }
        employerComboBox.setModel(new DefaultComboBoxModel<>(employers.toArray(new Employer[0])));
    }

    private void clearPanel() {
        titleTextField.setText("");
        descriptionTextArea.setText("");
        titleTextField.setBorder(new LineBorder(Color.white));
    }

    @Override
    public void update() {
        populateEmployerComboBox();
    }
}
