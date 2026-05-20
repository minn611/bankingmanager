package com.bankmanager.service;

import com.bankmanager.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LogService {

    /**
     * Ghi log hành động vào hệ thống.
     */
    public static void log(Integer employeeId, String action, String detail) {
        String sql = "INSERT INTO system_logs (employee_id, action, detail) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (employeeId == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, employeeId);
            }
            ps.setString(2, action);
            ps.setString(3, detail);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Không thể ghi system log: " + e.getMessage());
        }
    }
}
