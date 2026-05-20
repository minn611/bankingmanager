package com.bankmanager.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Lớp tiện ích kiểm tra tính hợp lệ của dữ liệu đầu vào (cccd, phone, email, amount).
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$"); // SĐT Việt Nam bắt đầu bằng 0 và có 10 số
    private static final Pattern CCCD_PATTERN = Pattern.compile("^\\d{9}$|^\\d{12}$"); // CMND 9 số hoặc CCCD 12 số

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true; // Cho phép trống
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidCCCD(String cccd) {
        if (cccd == null) return false;
        return CCCD_PATTERN.matcher(cccd).matches();
    }

    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
