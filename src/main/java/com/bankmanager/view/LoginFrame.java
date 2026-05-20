package com.bankmanager.view;

import com.bankmanager.model.Employee;
import com.bankmanager.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {
    private final AuthService authService = new AuthService();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkShowPassword;
    private JLabel lblError;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("🏦 BankManager Pro - Đăng Nhập Hệ Thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel chính với màu nền sáng và đệm
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. Phần Header (Thương hiệu Ngân hàng)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Tạo logo thương hiệu độc đáo kích thước 65px, chế độ sáng (isLightMode = true)
        BankLogo bankLogo = new BankLogo(65, true);
        bankLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitle = new JLabel("BANKMANAGER PRO", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 34, 64));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Hệ Thống Quản Lý Ngân Hàng", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(bankLogo);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        headerPanel.add(lblSubtitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Phần Form nhập liệu
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Nhãn Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblUsername, gbc);

        // Ô nhập Username
        gbc.gridy = 1;
        txtUsername = new JTextField(20);
        txtUsername.putClientProperty("JTextField.placeholderText", "Nhập tên tài khoản...");
        txtUsername.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtUsername, gbc);

        // Nhãn Password
        gbc.gridy = 2;
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblPassword, gbc);

        // Ô nhập Password
        gbc.gridy = 3;
        txtPassword = new JPasswordField(20);
        txtPassword.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu...");
        txtPassword.setPreferredSize(new Dimension(300, 35));
        formPanel.add(txtPassword, gbc);

        // Checkbox hiện mật khẩu
        gbc.gridy = 4;
        chkShowPassword = new JCheckBox("Hiển thị mật khẩu");
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        formPanel.add(chkShowPassword, gbc);

        // Nhãn hiển thị thông báo lỗi
        gbc.gridy = 5;
        lblError = new JLabel(" ", JLabel.CENTER);
        lblError.setForeground(new Color(220, 53, 69)); // Màu đỏ lỗi
        lblError.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        formPanel.add(lblError, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 3. Phần Nút Đăng nhập ở dưới cùng
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);

        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setPreferredSize(new Dimension(0, 45));
        btnLogin.setBackground(new Color(15, 34, 64)); // Nút màu Navy
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this::handleLogin);
        footerPanel.add(btnLogin, BorderLayout.NORTH);

        JLabel lblCopyright = new JLabel("Mô phỏng Hệ thống Ngân hàng Thương mại © 2026", JLabel.CENTER);
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(Color.GRAY);
        lblCopyright.setBorder(new EmptyBorder(15, 0, 0, 0));
        footerPanel.add(lblCopyright, BorderLayout.SOUTH);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Lắng nghe sự kiện phím Enter trên form để đăng nhập nhanh
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin(null);
                }
            }
        };
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
    }

    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!");
            return;
        }

        lblError.setText("Đang xác thực...");
        btnLogin.setEnabled(false);

        // Thực hiện đăng nhập trong luồng riêng để giao diện không bị đơ
        new Thread(() -> {
            try {
                Employee employee = authService.login(username, password);
                
                // Khởi tạo Dashboard chính ở luồng nền (bao gồm truy vấn dữ liệu thống kê ban đầu)
                // Giúp giao diện không bị đơ và bắt được mọi lỗi CSDL phát sinh lúc khởi động Dashboard
                MainDashboard dashboard = new MainDashboard();
                
                SwingUtilities.invokeLater(() -> {
                    lblError.setText(" ");
                    JOptionPane.showMessageDialog(this,
                            "Chào mừng " + employee.getFullName() + " (" + employee.getRole() + ") đăng nhập thành công!",
                            "Đăng nhập thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    dashboard.setVisible(true);
                    
                    // Đóng màn hình đăng nhập
                    this.dispose();
                });
            } catch (Throwable ex) {
                SwingUtilities.invokeLater(() -> {
                    String msg = ex.getMessage();
                    if (msg == null || msg.trim().isEmpty()) {
                        msg = ex.toString();
                    }
                    lblError.setText(msg);
                    btnLogin.setEnabled(true);
                    ex.printStackTrace();
                });
            }
        }).start();
    }
}
