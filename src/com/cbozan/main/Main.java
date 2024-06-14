
package com.cbozan.main;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import com.cbozan.dao.DB;

public class Main {
    
    public static void main(String[] args) {
        
        Locale.setDefault(new Locale("tr", "TR"));

        // Test database connection
        try (Connection conn = DB.getConnection()) {
            if (conn != null) {
                System.out.println("Connected to the database successfully!");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
        }
        
        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                //  new Login();
                 new Register();
            }
        });
    }
}
