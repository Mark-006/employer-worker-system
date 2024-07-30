package com.cbozan.dao;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.cbozan.entity.Employer;
import com.cbozan.entity.Employer.EmployerBuilder;

public class EmployerDAO {

    private final Map<Integer, Employer> cache = new HashMap<>();
    private boolean usingCache = true;

    private EmployerDAO() {
        list(); // Initialize cache on DAO creation
    }

    public Employer findById(int id) {
        if (!usingCache) {
            list(); // Refresh cache if not already using it
        }
        return cache.getOrDefault(id, null);
    }


    public List<Employer> list() {
        List<Employer> list = new ArrayList<>();
        cache.clear();
    
        String query = "SELECT employer_id, name, surname, description FROM employer"; // Simplified query for debugging
    
        try (Connection conn = DB.getConnection()) {
            if (conn != null) {
                System.out.println("Connection to database established.");
    
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery(query)) {
    
                    //System.out.println("Fetching employers..."); just a test
    
                    while (rs.next()) {
                        EmployerBuilder builder = new EmployerBuilder();
                        builder.setEmployerId(rs.getInt("employer_id"));
                        builder.setName(rs.getString("name"));
                        builder.setSurname(rs.getString("surname"));
                        builder.setDescription(rs.getString("description"));
    
                        // Bypass phonenumber for now
                        builder.setPhoneNumber(null); // Set null or empty list as default
    
                        Employer employer = builder.build();
                        list.add(employer);
                        cache.put(employer.getEmployerId(), employer);
    
                        //System.out.println("Fetched employer: " + employer.getName() + " " + employer.getSurname()); another test
                    }
    
                    //System.out.println("Total employers fetched: " + list.size());
    
                } catch (SQLException e) {
                    System.err.println("Error executing query:");
                    e.printStackTrace();
                }
            } else {
                System.err.println("Failed to make database connection.");
            }
    
        } catch (SQLException e) { 
            System.err.println("Error connecting to database:");
            e.printStackTrace();
        }
    
        return list;
    }


    public boolean create(Employer employer) {
        String insertQuery = "INSERT INTO employer (name, surname, phonenumber, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, employer.getName());
            pst.setString(2, employer.getSurname());
            // Convert phone numbers list to array or handle differently based on your database schema
            pst.setArray(3, createSqlArray(conn, "VARCHAR", employer.getPhoneNumber()));
            pst.setString(4, employer.getDescription());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int employerId = generatedKeys.getInt(1);
                    employer.setEmployerId(employerId);
                    cache.put(employerId, employer); // Update cache with new employer
                }
                return true;
            }

        } catch (SQLException e) {
            showSQLException(e);
        }

        return false;
    }

    private Array createSqlArray(Connection conn, String type, List<String> elements) throws SQLException {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        return conn.createArrayOf(type, elements.toArray(new String[0]));
    }

    private void showSQLException(SQLException e) {
        String message = e.getErrorCode() + "\n" + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + e.getCause();
        JOptionPane.showMessageDialog(null, message);
    }

    public static EmployerDAO getInstance() {
        return EmployerDAOHelper.instance;
    }

    private static class EmployerDAOHelper {
        private static final EmployerDAO instance = new EmployerDAO();
    }

    // Placeholder method for future implementation
    public boolean update(Employer selectedEmployer) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
