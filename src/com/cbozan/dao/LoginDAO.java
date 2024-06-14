package com.cbozan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {
    
    public boolean verifyLogin(String username, String pass) {
        
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        String query = "SELECT * FROM auth WHERE username=? AND password=?";
        
        try {
            conn = DB.getConnection(); // Make sure DB.getConnection() returns a valid Connection
            pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, pass);
            rs = pst.executeQuery();
            
            if (rs.next())
                return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
}
