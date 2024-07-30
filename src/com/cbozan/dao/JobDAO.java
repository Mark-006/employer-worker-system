package com.cbozan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import com.cbozan.entity.Employer;
import com.cbozan.entity.Job;
import com.cbozan.entity.Job.JobBuilder;
import com.cbozan.exception.EntityException;

public class JobDAO {

    private final HashMap<Integer, Job> cache = new HashMap<>();
    private boolean usingCache = true;

    private JobDAO() {
        list();
    }

    public static JobDAO getInstance() {
        return JobDAOHelper.instance;
    }

    private static class JobDAOHelper {
        private static final JobDAO instance = new JobDAO();
    }

    // Read by id
    public Job findById(int id) {
        if (!usingCache) {
            list();
        }
        return cache.get(id);
    }

    public void refresh() {
        setUsingCache(false);
        list();
        setUsingCache(true);
    }

    // Fetch job titles from the database
    public List<String> getAllJobTitles() {
        List<String> jobTitles = new ArrayList<>();

        String query = "SELECT title FROM job;";

        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                jobTitles.add(rs.getString("title"));
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return jobTitles;
    }

    // Read all jobs for a specific employer
    public List<Job> list(Employer employer) {
        List<Job> jobList = new ArrayList<>();
        String query = "SELECT * FROM job WHERE employer_id=?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, employer.getEmployerId());
            try (ResultSet rs = pst.executeQuery()) {
                JobBuilder builder = new JobBuilder();
                while (rs.next()) {
                    Job job = findById(rs.getInt("id"));
                    if (job != null) { // cache control
                        jobList.add(job);
                    } else {
                        builder.setId(rs.getInt("id"))
                               .setEmployerId(rs.getInt("employer_id"))
                               .setPriceId(rs.getInt("price_id"))
                               .setTitle(rs.getString("title"))
                               .setDescription(rs.getString("description"))
                               .setDate(rs.getTimestamp("date"));

                        try {
                            job = builder.build();
                            jobList.add(job);
                            cache.put(job.getId(), job);
                        } catch (EntityException e) {
                            showEntityException(e, "Error while building Job object from database.");
                        }
                    }
                }
            }
        } catch (SQLException sqle) {
            showSQLException(sqle);
        }
        return jobList;
    }

    // Read All jobs
    public List<Job> list() {
        List<Job> list = new ArrayList<>();

        if (cache.size() != 0 && usingCache) {
            list.addAll(cache.values());
            return list;
        }

        cache.clear();
        String query = "SELECT * FROM job;";

        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                JobBuilder builder = new JobBuilder();
                builder.setId(rs.getInt("id"))
                       .setEmployerId(rs.getInt("employer_id"))
                       .setPriceId(rs.getInt("price_id"))
                       .setTitle(rs.getString("title"))
                       .setDescription(rs.getString("description"))
                       .setDate(rs.getTimestamp("date"));

                try {
                    Job job = builder.build();
                    list.add(job);
                    cache.put(job.getId(), job);
                } catch (EntityException e) {
                    showEntityException(e, "Error while building Job object from database.");
                }
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return list;
    }

    // Create a new job
    public boolean create(Job job) {
        if (!createControl(job)) {
            return false;
        }

        String query = "INSERT INTO job (employer_id, price_id, title, description, date) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, job.getEmployer().getEmployerId());
            pst.setInt(2, job.getPrice().getPriceId());
            pst.setString(3, job.getTitle());
            pst.setString(4, job.getDescription());
            pst.setTimestamp(5, job.getDate());

            int result = pst.executeUpdate();
            if (result != 0) {
                refresh(); // Reload cache to include the new job
                return true;
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return false;
    }

    private boolean createControl(Job job) {
        for (Job existingJob : cache.values()) {
            if (existingJob.getTitle().equals(job.getTitle())) {
                DB.ERROR_MESSAGE = existingJob.getTitle() + " registration already exists.";
                return false;
            }
        }
        return true;
    }

    // Update an existing job
    public boolean update(Job job) {
        if (!updateControl(job)) {
            return false;
        }

        String query = "UPDATE job SET employer_id=?, price_id=?, title=?, description=?, date=? WHERE id=?;";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setInt(1, job.getEmployer().getEmployerId());
            pst.setInt(2, job.getPrice().getPriceId());
            pst.setString(3, job.getTitle());
            pst.setString(4, job.getDescription());
            pst.setTimestamp(5, job.getDate());
            pst.setInt(6, job.getId());

            int result = pst.executeUpdate();
            if (result != 0) {
                cache.put(job.getId(), job);
                return true;
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return false;
    }

    private boolean updateControl(Job job) {
        for (Job existingJob : cache.values()) {
            if (existingJob.getTitle().equals(job.getTitle()) && existingJob.getId() != job.getId()) {
                DB.ERROR_MESSAGE = existingJob.getTitle() + "\r\n" +
                        "registration already exists\r\n";
                return false;
            }
        }
        return true;
    }

    // Delete a job
    public boolean delete(Job job) {
        String query = "DELETE FROM job WHERE id=?;";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, job.getId());

            int result = ps.executeUpdate();
            if (result != 0) {
                cache.remove(job.getId());
                return true;
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return false;
    }

    public boolean isUsingCache() {
        return this.usingCache;
    }

    public void setUsingCache(boolean usingCache) {
        this.usingCache = usingCache;
    }

    private void showEntityException(EntityException e, String msg) {
        String message = msg + " not added" +
                "\n" + e.getMessage() + "\n" + e.getLocalizedMessage() + e.getCause();
        JOptionPane.showMessageDialog(null, message);
    }

    private void showSQLException(SQLException e) {
        String message = e.getErrorCode() + "\n" + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + e.getCause();
        JOptionPane.showMessageDialog(null, message);
    }

    public Job findByName(String jobText) {
        throw new UnsupportedOperationException("Unimplemented method 'findByName'");
    }
}
