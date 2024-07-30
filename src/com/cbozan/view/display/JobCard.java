package com.cbozan.view.display;

//import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cbozan.dao.EmployerDAO;
import com.cbozan.dao.JobDAO;
import com.cbozan.dao.PriceDAO;
import com.cbozan.entity.Employer;
import com.cbozan.entity.Job;
import com.cbozan.entity.Price;
import com.cbozan.exception.EntityException;
import com.cbozan.view.helper.Observer;

public class JobCard extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    public static final int rowWidth = 240;
    public static final int rowHeight = 24;
    private final int BW = 40;
    private final int BS = 5;
    private final int VMS = 10;
    private final int VBS = 15;

    private JLabel titleLabel, employerLabel, priceLabel, descriptionLabel;
    private JComboBox<Job> jobTitleComboBox;
    private JComboBox<Employer> employerComboBox;
    private JComboBox<Price> priceComboBox;
    private JTextField descriptionTextField;
    private JButton updateButton;
    private Job selectedJob;
    private Observer observer;

    public JobCard() {
        super();
        setLayout(null);

        titleLabel = new JLabel("Job Title");
        titleLabel.setBounds(0, 0, rowWidth, rowHeight);
        addHeight(titleLabel.getHeight());
        this.add(titleLabel);

        jobTitleComboBox = new JComboBox<>(JobDAO.getInstance().list().toArray(new Job[0]));
        jobTitleComboBox.setBounds(titleLabel.getX(), titleLabel.getY() + titleLabel.getHeight(), rowWidth - BW - BS, rowHeight);
        jobTitleComboBox.addActionListener(e -> {
            if (jobTitleComboBox.getSelectedItem() != null) {
                selectedJob = (Job) jobTitleComboBox.getSelectedItem();
                setJobDetails(selectedJob);
                notifyObserver();
            }
        });
        addHeight(jobTitleComboBox.getHeight());
        this.add(jobTitleComboBox);

        employerLabel = new JLabel("Employer");
        employerLabel.setBounds(jobTitleComboBox.getX(), jobTitleComboBox.getY() + jobTitleComboBox.getHeight() + VMS, rowWidth, rowHeight);
        addHeight(employerLabel.getHeight() + VMS);
        this.add(employerLabel);

        employerComboBox = new JComboBox<>(EmployerDAO.getInstance().list().toArray(new Employer[0]));
        employerComboBox.setBounds(employerLabel.getX(), employerLabel.getY() + employerLabel.getHeight(), rowWidth - BW - BS, rowHeight);
        addHeight(employerComboBox.getHeight());
        this.add(employerComboBox);

        priceLabel = new JLabel("Price");
        priceLabel.setBounds(employerComboBox.getX(), employerComboBox.getY() + employerComboBox.getHeight() + VMS, rowWidth, rowHeight);
        addHeight(priceLabel.getHeight() + VMS);
        this.add(priceLabel);

        priceComboBox = new JComboBox<>(PriceDAO.getInstance().list().toArray(new Price[0]));
        priceComboBox.setBounds(priceLabel.getX(), priceLabel.getY() + priceLabel.getHeight(), rowWidth - BW - BS, rowHeight);
        addHeight(priceComboBox.getHeight());
        this.add(priceComboBox);

        descriptionLabel = new JLabel("Description");
        descriptionLabel.setBounds(priceComboBox.getX(), priceComboBox.getY() + priceComboBox.getHeight() + VMS, rowWidth, rowHeight);
        addHeight(descriptionLabel.getHeight() + VMS);
        this.add(descriptionLabel);

        descriptionTextField = new JTextField();
        descriptionTextField.setBounds(descriptionLabel.getX(), descriptionLabel.getY() + descriptionLabel.getHeight(), rowWidth - BW - BS, rowHeight * 3);
        addHeight(descriptionTextField.getHeight());
        this.add(descriptionTextField);

        updateButton = new JButton("Update");
        updateButton.setFocusable(false);
        updateButton.addActionListener(this);
        updateButton.setBounds(descriptionTextField.getX(), descriptionTextField.getY() + descriptionTextField.getHeight() + VBS, rowWidth - BW - BS, rowHeight);
        addHeight(updateButton.getHeight() + VBS);
        this.add(updateButton);
    }

    private void addHeight(int height) {
        setSize(getWidth(), getHeight() + height);
    }

    private void setJobDetails(Job job) {
        if (job != null) {
            jobTitleComboBox.setSelectedItem(job);
            employerComboBox.setSelectedItem(job.getEmployer());
            priceComboBox.setSelectedItem(job.getPrice());
            descriptionTextField.setText(job.getDescription());
        } else {
            jobTitleComboBox.setSelectedIndex(-1);
            employerComboBox.setSelectedIndex(-1);
            priceComboBox.setSelectedIndex(-1);
            descriptionTextField.setText("");
        }
    }

    public void addObserver(Observer observer) {
        this.observer = observer;
    }

    private void notifyObserver() {
        if (observer != null) {
            observer.update();
        }
    }

    public void setSelectedJob(Job selectedJob) {
        this.selectedJob = selectedJob;
        setJobDetails(selectedJob);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == updateButton && selectedJob != null) {
            try {
                selectedJob.setTitle(((Job) jobTitleComboBox.getSelectedItem()).getTitle());
                selectedJob.setEmployer((Employer) employerComboBox.getSelectedItem());
                selectedJob.setPrice((Price) priceComboBox.getSelectedItem());
                selectedJob.setDescription(descriptionTextField.getText());

                if (JobDAO.getInstance().update(selectedJob)) {
                    JOptionPane.showMessageDialog(this, "Update successful", "SUCCESSFUL", JOptionPane.INFORMATION_MESSAGE);

                    notifyObserver();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed", "UNSUCCESSFUL", JOptionPane.ERROR_MESSAGE);
                }
            } catch (EntityException e1) {
                e1.printStackTrace();
            }
        }
    }
}
