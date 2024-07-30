package com.cbozan.dao;

import com.cbozan.entity.Worker;
import com.cbozan.exception.EntityException;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WorkerDAO {

    private final HashMap<Integer, Worker> cache = new HashMap<>();
    private boolean usingCache = true;

    private WorkerDAO() {
        list(); // Populate cache on initialization
    }

    public Worker findById(int workerId) {
        if (!usingCache) {
            list(); // Refresh cache if not using cache
        }

        return cache.get(workerId);
    }

    public void refresh() {
        setUsingCache(false);
        list();
        setUsingCache(true);
    }

 
    public List<Worker> list() {
        List<Worker> list = new ArrayList<>();
    
        if (!cache.isEmpty() && usingCache) {
            list.addAll(cache.values());
            return list;
        }
    
        cache.clear();
    
        String query = "SELECT worker_id, fname, lname, tel, iban, description, name FROM worker";
        //System.out.println("Query: " + query); // Output the query being executed
    
        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
    
            System.out.println("Connection successful.");
    
            boolean foundResults = false;
            while (rs.next()) {
                foundResults = true;
                Worker worker = extractWorkerFromResultSet(rs);
                list.add(worker);
                cache.put(worker.getId(), worker);
            }
    
            if (!foundResults) {
                System.out.println("No results found for the query."); // Print message if no results found
            } else {
                System.out.println("Retrieved " + list.size() + " workers from the database."); // Print number of workers retrieved
            }
    
        } catch (SQLException e) {
            showSQLException(e); // Show detailed SQLException information
            System.out.println("Exception occurred: " + e.getMessage()); // Print generic exception message
        }
    
        return list;
    }
    
    
    
    

    public boolean create(Worker worker) {
        if (!createControl(worker)) {
            return false;
        }

        String query = "INSERT INTO worker (fname, lname, tel, iban, description, name) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, worker.getFname());
            pstmt.setString(2, worker.getLname());
            setArrayOrNull(pstmt, 3, worker.getTel());
            pstmt.setString(4, worker.getIban());
            pstmt.setString(5, worker.getDescription());
            pstmt.setString(6, worker.getName());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int workerId = generatedKeys.getInt(1);
                        try {
                            worker.setId(workerId);
                        } catch (EntityException e) {
                            e.printStackTrace();
                        }
                        cache.put(workerId, worker);
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            showSQLException(e);
        }

        return false;
    }

    public boolean update(Worker worker) {
        if (!updateControl(worker)) {
            return false;
        }

        String query = "UPDATE worker SET fname = ?, lname = ?, tel = ?, iban = ?, description = ?, name = ? WHERE worker_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, worker.getFname());
            pstmt.setString(2, worker.getLname());
            setArrayOrNull(pstmt, 3, worker.getTel());
            pstmt.setString(4, worker.getIban());
            pstmt.setString(5, worker.getDescription());
            pstmt.setString(6, worker.getName());
            pstmt.setInt(7, worker.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                cache.put(worker.getId(), worker);
                return true;
            }

        } catch (SQLException e) {
            showSQLException(e);
        }

        return false;
    }

    public boolean delete(Worker worker) {
        String query = "DELETE FROM worker WHERE worker_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, worker.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                cache.remove(worker.getId());
                return true;
            }

        } catch (SQLException e) {
            showSQLException(e);
        }

        return false;
    }

    private Worker extractWorkerFromResultSet(ResultSet rs) throws SQLException {
        Worker.WorkerBuilder builder = new Worker.WorkerBuilder();
        builder.setId(rs.getInt("worker_id"))
                .setFname(rs.getString("fname"))
                .setLname(rs.getString("lname"))
                .setTel(getArrayOrNull(rs.getArray("tel")))
                .setIban(rs.getString("iban"))
                .setDescription(rs.getString("description"))
                .setName(rs.getString("name"))
                .setDate(rs.getTimestamp("date"));

        try {
            return builder.build();
        } catch (EntityException e) {
            showEntityException(e, rs.getString("fname") + " " + rs.getString("lname"));
            return null;
        }
    }

    private void setArrayOrNull(PreparedStatement pstmt, int index, List<String> list) throws SQLException {
        if (list == null) {
            pstmt.setNull(index, Types.ARRAY);
        } else {
            java.sql.Array array = pstmt.getConnection().createArrayOf("VARCHAR", list.toArray());
            pstmt.setArray(index, array);
        }
    }

    private List<String> getArrayOrNull(Array array) throws SQLException {
        if (array == null) {
            return null;
        } else {
            return Arrays.asList((String[]) array.getArray());
        }
    }

    private boolean createControl(Worker worker) {
        for (Worker cachedWorker : cache.values()) {
            if (cachedWorker.getFname().equals(worker.getFname()) && cachedWorker.getLname().equals(worker.getLname())) {
                DB.ERROR_MESSAGE = cachedWorker.getFname() + " " + cachedWorker.getLname() + " registration already exists.";
                return false;
            }
        }
        return true;
    }

    private boolean updateControl(Worker worker) {
        for (Worker cachedWorker : cache.values()) {
            if (cachedWorker.getFname().equals(worker.getFname())
                    && cachedWorker.getLname().equals(worker.getLname())
                    && cachedWorker.getId() != worker.getId()) {
                DB.ERROR_MESSAGE = cachedWorker.getFname() + " " + cachedWorker.getLname() + " registration already exists.";
                return false;
            }
        }
        return true;
    }

    public boolean isUsingCache() {
        return usingCache;
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

    private static class WorkerDAOHelper {
        private static final WorkerDAO instance = new WorkerDAO();
    }

    public static WorkerDAO getInstance() {
        return WorkerDAOHelper.instance;
    }
}
