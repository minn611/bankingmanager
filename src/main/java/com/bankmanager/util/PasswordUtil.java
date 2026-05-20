package com.bankmanager.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Tiện ích mã hóa và kiểm tra mật khẩu bằng thuật toán BCrypt.
 */
public class PasswordUtil {

    /**
     * Mã hóa mật khẩu dạng text thường thành chuỗi băm BCrypt.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    /**
     * Kiểm tra mật khẩu thường nhập vào có khớp với chuỗi băm BCrypt trong CSDL hay không.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra BCrypt hash: " + e.getMessage());
            return false;
        }
    }
}
