package com.cbozan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.cbozan.entity.Work;
import com.cbozan.entity.Work.WorkBuilder;
import com.cbozan.entity.Worker;
import com.cbozan.exception.EntityException;

public class WorkDAO {

    private final HashMap<Integer, Work> cache = new HashMap<>();
    private boolean usingCache = true;

    private WorkDAO() {
        list();
    }

    private static class WorkDAOHelper {
        private static final WorkDAO INSTANCE = new WorkDAO();
    }

    public static WorkDAO getInstance() {
        return WorkDAOHelper.INSTANCE;
    }

    public Work findById(int id) {
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

    public List<Work> list(Worker worker, String dateStrings) {
        List<Work> workList = new ArrayList<>();
        String query = generateQueryForWorkerAndDate(worker.getId(), dateStrings);

        if (query.isEmpty()) {
            return workList;
        }

        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            WorkBuilder builder = new WorkBuilder();
            while (rs.next()) {
                Work work = findById(rs.getInt("id"));
                if (work != null) {
                    workList.add(work);
                } else {
                    work = buildWorkFromResultSet(rs, builder);
                    if (work != null) {
                        workList.add(work);
                        cache.put(work.getId(), work);
                    }
                }
            }
        } catch (SQLException e) {
            showSQLException(e);
        }

        return workList;
    }

    public List<Work> list(Worker worker) {
        return list(worker, "");
    }

    public List<Work> list() {
        List<Work> list = new ArrayList<>();
        if (!usingCache) {
            cache.clear();
        }

        if (!cache.isEmpty() && usingCache) {
            list.addAll(cache.values());
            return list;
        }

        String query = "SELECT * FROM work;";

        try (Connection conn = DB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            WorkBuilder builder = new WorkBuilder();
            while (rs.next()) {
                Work work = buildWorkFromResultSet(rs, builder);
                if (work != null) {
                    list.add(work);
                    cache.put(work.getId(), work);
                }
            }
        } catch (SQLException e) {
            showSQLException(e);
        }

        return list;
    }

    public boolean create(Work work) {
        String query = "INSERT INTO work (job_id, worker_id, worktype_id, workgroup_id, description) VALUES (?, ?, ?, ?, ?);";
        String query2 = "SELECT * FROM work ORDER BY id DESC LIMIT 1;";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            setWorkPreparedStatement(pst, work);

            int result = pst.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = conn.createStatement().executeQuery(query2)) {
                    if (rs.next()) {
                        Work newWork = buildWorkFromResultSet(rs, new WorkBuilder());
                        if (newWork != null) {
                            cache.put(newWork.getId(), newWork);
                        }
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return false;
    }

    public boolean update(Work work) {
        String query = "UPDATE work SET job_id=?, worker_id=?, worktype_id=?, workgroup_id=?, description=? WHERE id=?;";

        try (Connection conn = DB.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            setWorkPreparedStatement(pst, work);
            pst.setInt(6, work.getId());

            int result = pst.executeUpdate();
            if (result > 0) {
                cache.put(work.getId(), work);
                return true;
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return false;
    }

    public boolean delete(Work work) {
        String query = "DELETE FROM work WHERE id=?;";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, work.getId());

            int result = ps.executeUpdate();
            if (result > 0) {
                cache.remove(work.getId());
                return true;
            }
        } catch (SQLException e) {
            showSQLException(e);
        }
        return false;
    }

    public boolean isUsingCache() {
        return usingCache;
    }

    public void setUsingCache(boolean usingCache) {
        this.usingCache = usingCache;
    }

    private void setWorkPreparedStatement(PreparedStatement pst, Work work) throws SQLException {
        pst.setInt(1, work.getJob().getId());
        pst.setInt(2, work.getWorker().getId());
        pst.setInt(3, work.getWorktype().getId());
        // Assuming workgroup_id is part of Work class, adjust accordingly
        pst.setInt(4, work.getWorkgroup().getId());
        pst.setString(5, work.getDescription());
    }

    private Work buildWorkFromResultSet(ResultSet rs, WorkBuilder builder) {
        try {
            builder.setId(rs.getInt("id"));
            builder.setJob_id(rs.getInt("job_id"));
            builder.setWorker_id(rs.getInt("worker_id"));
            builder.setWorktype_id(rs.getInt("worktype_id"));
            // Adjust for workgroup_id and other fields as necessary
            builder.setWorkgroup_id(rs.getInt("workgroup_id"));
            builder.setDescription(rs.getString("description"));
            builder.setDate(rs.getTimestamp("date"));
            return builder.build();
        } catch (SQLException | EntityException e) {
            try {
				showEntityException(e, "ID : " + rs.getInt("id"));
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
        }
        return null;
    }

    private String generateQueryForWorkerAndDate(int workerId, String dateStrings) {
        StringBuilder query = new StringBuilder("SELECT * FROM work WHERE worker_id=" + workerId);
        StringTokenizer tokenizer = new StringTokenizer(dateStrings, "-");

        if (tokenizer.countTokens() == 1) {
            try {
                Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(tokenizer.nextToken());
                String formattedDate1 = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                date1.setTime(date1.getTime() + 86400000L);
                String formattedDate2 = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                query.append(" AND date >= '").append(formattedDate1).append("' AND date <= '").append(formattedDate2).append("';");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (tokenizer.countTokens() == 2) {
            try {
                Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(tokenizer.nextToken());
                String formattedDate1 = new SimpleDateFormat("yyyy-MM-dd").format(date1);
                Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(tokenizer.nextToken());
                date2.setTime(date2.getTime() + 86400000L);
                String formattedDate2 = new SimpleDateFormat("yyyy-MM-dd").format(date2);
                query.append(" AND date >= '").append(formattedDate1).append("' AND date <= '").append(formattedDate2).append("';");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            return "";
        }

        return query.toString();
    }

    private void showEntityException(Exception e, String msg) {
        String message = msg + " not added" + 
                         "\n" + e.getMessage() + "\n" + e.getLocalizedMessage() + e.getCause();
        JOptionPane.showMessageDialog(null, message);
    }

    private void showSQLException(SQLException e) {
        String message = e.getErrorCode() + "\n" + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + e.getCause();
        JOptionPane.showMessageDialog(null, message);
    }

	public void insert(Work work) {
		
		throw new UnsupportedOperationException("Unimplemented method 'insert'");
	}
}
