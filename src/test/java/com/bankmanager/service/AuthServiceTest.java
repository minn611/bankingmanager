package com.bankmanager.service;

import com.bankmanager.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Lớp kiểm thử đơn vị (Unit Test) cho các chức năng bảo mật hệ thống.
 */
public class AuthServiceTest {

    @Test
    public void testPasswordHashing() {
        String plainPassword = "mySecurePassword123";
        
        // 1. Thực hiện băm mật khẩu
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$")); // BCrypt hash prefix

        // 2. Kiểm tra mật khẩu khớp
        assertTrue(PasswordUtil.checkPassword(plainPassword, hashedPassword));
        
        // 3. Kiểm tra mật khẩu sai
        assertFalse(PasswordUtil.checkPassword("wrongPassword", hashedPassword));
    }

    @Test
    public void testDatabaseConnection() {
        try {
            System.out.println("\n=============================================");
            System.out.println("🔍 ĐANG THỬ NGHIỆM KẾT NỐI CƠ SỞ DỮ LIỆU MySQL...");
            System.out.println("=============================================");
            
            java.sql.Connection conn = com.bankmanager.util.DBConnection.getConnection();
            assertNotNull(conn, "Kết nối CSDL trả về null!");
            System.out.println("✅ KẾT NỐI CSDL THÀNH CÔNG!");
            
            // Kiểm tra số lượng nhân viên
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM employees");
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("👥 SỐ LƯỢNG NHÂN VIÊN TRONG BẢNG: " + rs.getInt(1));
            }
            
            // Liệt kê tài khoản nhân viên và kiểm tra mật khẩu admin123
            java.sql.PreparedStatement ps2 = conn.prepareStatement("SELECT username, password, role, status FROM employees");
            java.sql.ResultSet rs2 = ps2.executeQuery();
            System.out.println("📋 DANH SÁCH NHÂN VIÊN HIỆN CÓ:");
            while (rs2.next()) {
                String user = rs2.getString("username");
                String hash = rs2.getString("password");
                boolean matches = false;
                try {
                    matches = com.bankmanager.util.PasswordUtil.checkPassword("admin123", hash);
                } catch (Exception ex) {
                    System.err.println("Lỗi kiểm tra BCrypt cho " + user + ": " + ex.getMessage());
                }
                System.out.println("   - Tài khoản: " + user + 
                                   " | Quyền: " + rs2.getString("role") + 
                                   " | Trạng thái: " + rs2.getString("status") +
                                   " | Khớp mật khẩu 'admin123': " + (matches ? "ĐÚNG ✅" : "SAI ❌"));
            }
            
            conn.close();
            System.out.println("=============================================\n");
        } catch (Exception e) {
            System.err.println("\n❌ KẾT NỐI THẤT BẠI!");
            System.err.println("Lỗi chi tiết: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=============================================\n");
            fail("Lỗi kết nối CSDL: " + e.getMessage());
        }
    }
}
