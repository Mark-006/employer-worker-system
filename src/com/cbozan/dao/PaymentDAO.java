package com.cbozan.dao;

import com.cbozan.entity.Payment;
import com.cbozan.entity.Payment.PaymentBuilder;
import com.cbozan.entity.Worker;
import com.cbozan.exception.EntityException;

import javax.swing.*;
//import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class PaymentDAO {

    private final HashMap<Integer, Payment> cache = new HashMap<>();
    private boolean usingCache = true;

    private PaymentDAO() {
        list(); // Initialize cache on construction
    }

    // Singleton pattern
    private static class PaymentDAOHelper {
        private static final PaymentDAO instance = new PaymentDAO();
    }

    public static PaymentDAO getInstance() {
        return PaymentDAOHelper.instance;
    }

    // Read by id
    public Payment findById(int id) {
        if (!usingCache) {
            list(); // Refresh cache if not using cache
        }

        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        return null;
    }

    // Refresh cache
    public void refresh() {
        setUsingCache(false);
        list();
        setUsingCache(true);
    }

    private void setUsingCache(boolean b) {
		
		throw new UnsupportedOperationException("Unimplemented method 'setUsingCache'");
	}

	// Read all payments
    public List<Payment> list() {
        List<Payment> list = new ArrayList<>();

        if (!usingCache || cache.isEmpty()) {
            cache.clear();

            Connection conn = null;
            Statement st = null;
            ResultSet rs = null;
            String query = "SELECT * FROM payment";

            try {
                conn = DB.getConnection();
                st = conn.createStatement();
                rs = st.executeQuery(query);

                while (rs.next()) {
                    PaymentBuilder builder = new PaymentBuilder();
                    builder.setId(rs.getInt("id"));
                    builder.setWorkerId(rs.getInt("worker_id"));
                    builder.setJobId(rs.getInt("job_id"));
                    builder.setPaytypeId(rs.getInt("paytype_id"));
                    builder.setAmount(rs.getBigDecimal("amount"));
                    builder.setDate(rs.getTimestamp("date"));

                    try {
                        Payment payment = builder.build();
                        list.add(payment);
                        cache.put(payment.getId(), payment);
                    } catch (EntityException e) {
                        showEntityException(e, "Error creating payment");
                    }
                }
            } catch (SQLException e) {
                showSQLException(e);
            } finally {
                closeResources(conn, st, rs);
            }
        } else {
            // Return cached values
            list.addAll(cache.values());
        }

        return list;
    }

    // List payments by worker and date range
    public List<Payment> list(Worker worker, String dateStrings) {
        List<Payment> paymentList = new ArrayList<>();
        String query = "SELECT * FROM payment WHERE worker_id = ?";
        String guiDatePattern = "dd/MM/yyyy";
        String dbDatePattern = "yyyy-MM-dd";

        StringTokenizer tokenizer = new StringTokenizer(dateStrings, "-");

        if (tokenizer.countTokens() == 1) {
            try {
                Date d1 = new SimpleDateFormat(guiDatePattern).parse(tokenizer.nextToken());
                String date1 = new SimpleDateFormat(dbDatePattern).format(d1);
                d1.setTime(d1.getTime() + 86400000L);
                String date2 = new SimpleDateFormat(dbDatePattern).format(d1);
                query += " AND date >= ? AND date <= ?";

                paymentList = executePaymentQuery(query, worker.getId(), date1, date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (tokenizer.countTokens() == 2) {
            try {
                Date d1 = new SimpleDateFormat(guiDatePattern).parse(tokenizer.nextToken());
                String date1 = new SimpleDateFormat(dbDatePattern).format(d1);
                d1 = new SimpleDateFormat(guiDatePattern).parse(tokenizer.nextToken());
                d1.setTime(d1.getTime() + 86400000L);
                String date2 = new SimpleDateFormat(dbDatePattern).format(d1);
                query += " AND date >= ? AND date <= ?";

                paymentList = executePaymentQuery(query, worker.getId(), date1, date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return paymentList;
    }

    // Helper method to execute payment query
    private List<Payment> executePaymentQuery(String query, int workerId, String date1, String date2) {
        List<Payment> paymentList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DB.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, workerId);
            pst.setString(2, date1);
            pst.setString(3, date2);
            rs = pst.executeQuery();

            while (rs.next()) {
                Payment payment = findById(rs.getInt("id")); // Check cache first
                if (payment == null) {
                    PaymentBuilder builder = new PaymentBuilder();
                    builder.setId(rs.getInt("id"));
                    builder.setWorkerId(rs.getInt("worker_id"));
                    builder.setJobId(rs.getInt("job_id"));
                    builder.setPaytypeId(rs.getInt("paytype_id"));
                    builder.setAmount(rs.getBigDecimal("amount"));
                    builder.setDate(rs.getTimestamp("date"));

                    try {
                        payment = builder.build();
                        paymentList.add(payment);
                        cache.put(payment.getId(), payment);
                    } catch (EntityException e) {
                        showEntityException(e, "Error creating payment");
                    }
                } else {
                    paymentList.add(payment);
                }
            }
        } catch (SQLException e) {
            showSQLException(e);
        } finally {
            closeResources(conn, pst, rs);
        }

        return paymentList;
    }

    // Create a new payment
    public boolean create(Payment payment) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int result = 0;
        String query = "INSERT INTO payment (worker_id, job_id, paytype_id, amount, date) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            conn = DB.getConnection();
            pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, payment.getWorkerId());
            pst.setInt(2, payment.getJobId());
            pst.setInt(3, payment.getPaytypeId());
            pst.setBigDecimal(4, payment.getAmount());
            pst.setTimestamp(5, new Timestamp(payment.getDate().getTime()));

            result = pst.executeUpdate();

            if (result != 0) {
                rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    payment.setId(id);
                    cache.put(payment.getId(), payment);
                }
            }
        } catch (SQLException e) {
            showSQLException(e);
        } catch (EntityException e) {
			
			e.printStackTrace();
		} finally {
            closeResources(conn, pst, rs);
        }

        return result != 0;
    }

    // Update an existing payment
    public boolean update(Payment payment) {
        Connection conn = null;
        PreparedStatement pst = null;
        int result = 0;
        String query = "UPDATE payment SET worker_id=?, job_id=?, paytype_id=?, amount=?, date=? " +
                "WHERE id=?";

        try {
            conn = DB.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, payment.getWorkerId());
            pst.setInt(2, payment.getJobId());
            pst.setInt(3, payment.getPaytypeId());
            pst.setBigDecimal(4, payment.getAmount());
            pst.setTimestamp(5, new Timestamp(payment.getDate().getTime()));
            pst.setInt(6, payment.getId());

            result = pst.executeUpdate();

            if (result != 0) {
                cache.put(payment.getId(), payment);
            }
        } catch (SQLException e) {
            showSQLException(e);
        } finally {
            closeResources(conn, pst, null);
        }

        return result != 0;
    }

    // Delete a payment
    public boolean delete(Payment payment) {
        Connection conn = null;
        PreparedStatement pst = null;
        int result = 0;
        String query = "DELETE FROM payment WHERE id=?";

        try {
            conn = DB.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, payment.getId());

            result = pst.executeUpdate();

            if (result != 0) {
                cache.remove(payment.getId());
            }
        } catch (SQLException e) {
            showSQLException(e);
        } finally {
            closeResources(conn, pst, null);
        }

        return result != 0;
    }

    // Utility method to close resources
    private void closeResources(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                showSQLException(e);
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                showSQLException(e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                showSQLException(e);
            }
        }
    }

    // Utility method to show EntityException
    private void showEntityException(EntityException e, String msg) {
        String message = msg + ": " + e.getMessage();
        JOptionPane.showMessageDialog(null, message, "Entity Exception", JOptionPane.ERROR_MESSAGE);
    }

    // Utility method to show SQLException
    private void showSQLException(SQLException e) {
        String message = "SQL Exception: " + e.getMessage();
        JOptionPane.showMessageDialog(null, message, "SQL Exception", JOptionPane.ERROR_MESSAGE);
    }

	public List<Payment> list(Worker selectedWorker) {
		
		throw new UnsupportedOperationException("Unimplemented method 'list'");
	}
}
