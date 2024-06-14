package com.cbozan.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB {

    public static String ERROR_MESSAGE = "";

    private DB() {}

    private static class DBHelper {
        private static final DB CONNECTION = new DB();
    }

    public static Connection getConnection() {
        return DBHelper.CONNECTION.connect();
    }

    public static void destroyConnection() {
        DBHelper.CONNECTION.disconnect();
    }

    private Connection conn = null;

    private Connection connect() {
        try {
            if (conn == null || conn.isClosed()) {
                // Database connection properties
                Properties props = new Properties();
                props.setProperty("user", "postgres");       
                props.setProperty("password", "Mark1234");   
                props.setProperty("encoding", "UTF8");

                // PostgreSQL JDBC URL
                String url = "jdbc:postgresql://localhost:5432/employer_worker_db";

                try {

                    conn = DriverManager.getConnection(url, props);
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private void disconnect() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
