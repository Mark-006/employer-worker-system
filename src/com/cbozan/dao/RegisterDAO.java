package com.cbozan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RegisterDAO {

    public boolean isEmailTaken(String email) {
        return checkIfExists("email", email);
    }

    public boolean isUsernameTaken(String username) {
        return checkIfExists("username", username);
    }

    public boolean isPhoneNumberTaken(String phoneNumber) {
        return checkIfExists("phonenumber", phoneNumber);
    }

    private boolean checkIfExists(String column, String value) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        String query = "SELECT id FROM auth WHERE " + column + " = ?";

        try {
            conn = DB.getConnection();
            pst = conn.prepareStatement(query);
            pst.setString(1, value);
            rs = pst.executeQuery();

            return rs.next();

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

    public boolean registerUser(String username, String email, String phoneNumber, String password, String role) {
        if (isUsernameTaken(username) || isEmailTaken(email) || isPhoneNumberTaken(phoneNumber)) {
            return false; // Username, email, or phone number is already taken
        }

        Connection conn = null;
        PreparedStatement pst = null;

        String query = "INSERT INTO auth (uniqueid, username, email, phonenumber, password, role) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = DB.getConnection();
            pst = conn.prepareStatement(query);
            pst.setObject(1, UUID.randomUUID());
            pst.setString(2, username);
            pst.setString(3, email);
            pst.setString(4, phoneNumber);
            pst.setString(5, password);
            pst.setString(6, role);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}