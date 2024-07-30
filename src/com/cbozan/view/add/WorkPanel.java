package com.cbozan.view.add;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cbozan.dao.JobDAO;
import com.cbozan.dao.WorkDAO;
import com.cbozan.dao.WorkerDAO;
import com.cbozan.dao.WorktypeDAO;
import com.cbozan.entity.Job;
import com.cbozan.entity.Work;
import com.cbozan.entity.Worker;
import com.cbozan.entity.Worktype;
import com.cbozan.view.component.SearchBox;
import com.cbozan.view.helper.Observer;

public class WorkPanel extends JPanel implements Observer, ActionListener, Subject {

    private static final long serialVersionUID = 5937069706644528838L;
    private final List<Observer> observers;

    // GUI components
    private static final int LLX = 100;
    private static final int RLX = 550;
    private static final int LLW = 200;
    private static final int RLW = 430;
    private static final int LH = 25;
    private static final int LCVS = 5;

    private JLabel imageLabel, searchWorkerImageLabel;
    private JLabel jobLabel, worktypeLabel, descriptionLabel, workerLabel; // Added workerLabel
    private JComboBox<String> jobComboBox;
    private JComboBox<Worker> workerComboBox; // Changed to JComboBox<Worker>
    private JComboBox<Worktype> worktypeComboBox;
    private SearchBox searchWorkerSearchBox;
    private JScrollPane descriptionScrollPane;
    private JTextArea descriptionTextArea;
    private JButton saveButton, removeSelectedButton, addButton;

    private DefaultListModel<Worker> selectedWorkerDefaultListModel;
    private JList<Worker> selectedWorkerList;
    private JLabel selectedWorkerListLabel, selectedInfoTextLabel, selectedInfoCountLabel;

    public WorkPanel() {
        super();
        setLayout(null);
        observers = new ArrayList<>();
        initComponents();
        subscribe(this);
        update();
    }

    private void initComponents() {
        selectedWorkerDefaultListModel = new DefaultListModel<>();

        imageLabel = new JLabel(new ImageIcon("src/icon/add_work.png"));
        imageLabel.setBounds(LLX, 30, 100, 100); // Adjusted size to fit
        add(imageLabel);

        workerLabel = new JLabel("Select worker");
        workerLabel.setBounds(LLX, 160, LLW, LH);
        add(workerLabel);

        // Fetch workers from the database
        List<Worker> workers = WorkerDAO.getInstance().list();
        workerComboBox = new JComboBox<>(workers.toArray(new Worker[0]));
        workerComboBox.setBounds(LLX, workerLabel.getY() + workerLabel.getHeight() + LCVS, LLW, LH);
        add(workerComboBox);

        jobLabel = new JLabel("Job selection");
        jobLabel.setBounds(LLX, workerComboBox.getY() + workerComboBox.getHeight() + LCVS * 3, LLW, LH);
        add(jobLabel);

        // Fetch job titles from the database
        List<String> jobTitles = JobDAO.getInstance().getAllJobTitles();
        jobComboBox = new JComboBox<>(jobTitles.toArray(new String[0]));
        jobComboBox.setBounds(LLX, jobLabel.getY() + jobLabel.getHeight() + LCVS, LLW, LH);
        add(jobComboBox);

        worktypeLabel = new JLabel("Work type");
        worktypeLabel.setBounds(LLX, jobComboBox.getY() + jobComboBox.getHeight() + LCVS * 3, LLW, LH);
        add(worktypeLabel);

        // Fetch work types from the database
        List<Worktype> worktypes = WorktypeDAO.getInstance().list();
        worktypeComboBox = new JComboBox<>(worktypes.toArray(new Worktype[0]));
        worktypeComboBox.setBounds(LLX, worktypeLabel.getY() + worktypeLabel.getHeight() + LCVS, LLW, LH);
        add(worktypeComboBox);

        descriptionLabel = new JLabel("Description");
        descriptionLabel.setBounds(LLX, worktypeComboBox.getY() + worktypeComboBox.getHeight() + LCVS * 3, LLW, LH);
        add(descriptionLabel);

        descriptionTextArea = new JTextArea();
        descriptionScrollPane = new JScrollPane(descriptionTextArea);
        descriptionScrollPane.setBounds(LLX, descriptionLabel.getY() + descriptionLabel.getHeight() + LCVS, LLW, LH * 3);
        add(descriptionScrollPane);

        addButton = new JButton("Add");
        addButton.setBounds(LLX, descriptionScrollPane.getY() + descriptionScrollPane.getHeight() + LCVS * 3, 80, LH);
        addButton.addActionListener(e -> {
            String text = descriptionTextArea.getText().trim();
            if (!text.isEmpty()) {
                selectedWorkerDefaultListModel.addElement(new Worker(text));
                descriptionTextArea.setText("");
                selectedInfoCountLabel.setText(selectedWorkerDefaultListModel.size() + " person");
            }
        });
        add(addButton);

        searchWorkerImageLabel = new JLabel(new ImageIcon("src/icon/search_worker.png"));
        searchWorkerImageLabel.setBounds(RLX - 32 + RLW / 2, 30, 64, 64); // Adjusted Y coordinate
        add(searchWorkerImageLabel);

        JLabel searchWorkerLabel = new JLabel("Search worker");
        searchWorkerLabel.setBounds(RLX, searchWorkerImageLabel.getY() + 64 + 10, RLW, LH);
        add(searchWorkerLabel);

        searchWorkerSearchBox = new SearchBox(new ArrayList<>(), new Dimension(RLW, LH)) {
            private static final long serialVersionUID = 1L;

            @Override
            public void mouseAction(MouseEvent e, Object searchResultObject, int chooseIndex) {
                if (searchResultObject instanceof Worker) {
                    Worker selectedWorker = (Worker) searchResultObject;
                    selectedWorkerDefaultListModel.addElement(selectedWorker);
                    // Remove worker from search results after selection
                    getObjectList().remove(selectedWorker);
                    this.setText("");
                    selectedInfoCountLabel.setText(selectedWorkerDefaultListModel.size() + " person");
                }
                super.mouseAction(e, searchResultObject, chooseIndex);
            }
        };
        searchWorkerSearchBox.setBounds(RLX, searchWorkerLabel.getY() + LH + LCVS, RLW, LH);
        add(searchWorkerSearchBox);

        selectedWorkerListLabel = new JLabel("Added workers");
        selectedWorkerListLabel.setBounds(RLX, 160, RLW - 70, LH);
        add(selectedWorkerListLabel);

        selectedWorkerList = new JList<>(selectedWorkerDefaultListModel);
        selectedWorkerList.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                listCellRendererComponent.setBorder(null);
                return listCellRendererComponent;
            }
        });
        selectedWorkerList.setForeground(Color.GRAY);
        selectedWorkerList.setSelectionForeground(new Color(0, 180, 0));
        selectedWorkerList.setFixedCellHeight(30);
        selectedWorkerList.setBounds(RLX, selectedWorkerListLabel.getY() + selectedWorkerListLabel.getHeight(), RLW, 185);
        add(selectedWorkerList);

        removeSelectedButton = new JButton("DELETE");
        removeSelectedButton.setFocusPainted(false);
        removeSelectedButton.setBorder(searchWorkerSearchBox.getBorder());
        removeSelectedButton.setBackground(Color.RED);
        removeSelectedButton.addActionListener(e -> {
            if (!selectedWorkerList.isSelectionEmpty()) {
                int selectedIndex = selectedWorkerList.getSelectedIndex();
                selectedWorkerDefaultListModel.remove(selectedIndex);
                selectedInfoCountLabel.setText(selectedWorkerDefaultListModel.size() + " person");
            }
        });
        removeSelectedButton.setForeground(Color.WHITE);
        removeSelectedButton.setBounds(searchWorkerSearchBox.getX() + searchWorkerSearchBox.getWidth() - 69, selectedWorkerList.getY() + selectedWorkerList.getHeight() + 5, 68, LH + 5);
        add(removeSelectedButton);

        selectedInfoTextLabel = new JLabel("Selected: ");
        selectedInfoTextLabel.setBounds(RLX, removeSelectedButton.getY(), 60, LH + 5);
        add(selectedInfoTextLabel);

        selectedInfoCountLabel = new JLabel(selectedWorkerDefaultListModel.size() + " person");
        selectedInfoCountLabel.setForeground(new Color(0, 180, 0));
        selectedInfoCountLabel.setBounds(RLX + selectedInfoTextLabel.getWidth(), selectedInfoTextLabel.getY(), RLW - 60 - 68, LH + 5);
        add(selectedInfoCountLabel);

        saveButton = new JButton("SAVE");
        saveButton.setBounds(removeSelectedButton.getX() - 100, removeSelectedButton.getY() + 80, 168, 30);
        saveButton.addActionListener(this);
        add(saveButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            String jobText = (String) jobComboBox.getSelectedItem(); // Get selected job title from JComboBox
            Worktype worktype = (Worktype) worktypeComboBox.getSelectedItem(); // Get selected worktype from JComboBox
            String description = descriptionTextArea.getText().trim().toUpperCase();
            List<Worker> workers = new ArrayList<>();
            for (int i = 0; i < selectedWorkerDefaultListModel.size(); i++) {
                workers.add(selectedWorkerDefaultListModel.get(i));
            }

            Job job = JobDAO.getInstance().findByName(jobText); // Fetch Job object by job title

            Date currentDate = new Date(); // Current date
            for (Worker worker : workers) {
                WorkDAO.getInstance().insert(new Work(-1, job, worker, worktype, currentDate, description));
            }

            JOptionPane.showMessageDialog(this, "Work details saved successfully", "Information", JOptionPane.INFORMATION_MESSAGE);
            notifyObservers();
        }
    }

    @Override
    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }

    @Override
    public void update() {
        List<Job> jobs = JobDAO.getInstance().list();
        List<Worktype> worktypes = WorktypeDAO.getInstance().list();
        List<Worker> workers = WorkerDAO.getInstance().list();

        // Populate JComboBox with job titles
        jobComboBox.removeAllItems();
        for (Job job : jobs) {
            jobComboBox.addItem(job.getName());
        }

        // Populate JComboBox with work types
        worktypeComboBox.removeAllItems();
        for (Worktype worktype : worktypes) {
            worktypeComboBox.addItem(worktype);
        }

        // Update the search box with workers from database
        searchWorkerSearchBox.setObjectList(workers);

        // Update workerComboBox with worker names
        workerComboBox.removeAllItems();
        for (Worker worker : workers) {
            workerComboBox.addItem(worker);
        }
    }
}
