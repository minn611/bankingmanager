package com.bankmanager;

import com.formdev.flatlaf.FlatLightLaf;
import com.bankmanager.view.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Điểm khởi động ứng dụng quản lý ngân hàng BankManager Pro.
 */
public class Main {
    public static void main(String[] args) {
        // Thiết lập giao diện hiện đại FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // Tùy biến một số thông số giao diện để trông premium và mềm mại hơn
            UIManager.put("Button.arc", 8); // Bo góc nút
            UIManager.put("Component.arc", 8); // Bo góc combobox, text field...
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("ScrollBar.width", 12);
            
            // Cài đặt font chữ hệ thống hiện đại hơn
            Font sysFont = new Font("Segoe UI", Font.PLAIN, 13);
            UIManager.put("Label.font", sysFont);
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
            UIManager.put("Table.font", sysFont);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
            UIManager.put("TextField.font", sysFont);
            UIManager.put("PasswordField.font", sysFont);
            UIManager.put("ComboBox.font", sysFont);
            
        } catch (Exception e) {
            System.err.println("Không thể khởi tạo theme FlatLaf. Sử dụng giao diện mặc định.");
        }

        // Chạy ứng dụng trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
