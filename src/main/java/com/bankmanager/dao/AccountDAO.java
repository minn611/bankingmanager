package com.bankmanager.dao;

import com.bankmanager.model.Account;
import com.bankmanager.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm tài khoản bằng số TK: " + e.getMessage());
        }
        return null;
    }

    public List<Account> findByCustomerId(int customerId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY account_number ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAccount(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm danh sách tài khoản của khách hàng: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Account account) {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountNumber());
            ps.setInt(2, account.getCustomerId());
            ps.setString(3, account.getAccountType());
            ps.setBigDecimal(4, account.getBalance());
            ps.setString(5, account.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi mở tài khoản ngân hàng: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Account account) {
        String sql = "UPDATE accounts SET customer_id = ?, account_type = ?, balance = ?, status = ? WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, account.getCustomerId());
            ps.setString(2, account.getAccountType());
            ps.setBigDecimal(3, account.getBalance());
            ps.setString(4, account.getStatus());
            ps.setString(5, account.getAccountNumber());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật tài khoản: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật số dư tài khoản trong một Transaction (nhận Connection bên ngoài).
     */
    public boolean updateBalance(String accountNumber, BigDecimal amount, Connection conn) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ? AND status = 'ACTIVE'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setString(2, accountNumber);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Cập nhật số dư thất bại. Có thể tài khoản không tồn tại hoặc đã bị khóa/đóng.");
            }
            return true;
        }
    }

    public List<Account> getAll() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy toàn bộ tài khoản: " + e.getMessage());
        }
        return list;
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountNumber(rs.getString("account_number"));
        a.setCustomerId(rs.getInt("customer_id"));
        a.setAccountType(rs.getString("account_type"));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setStatus(rs.getString("status"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
