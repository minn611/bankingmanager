package com.bankmanager.view;

import com.bankmanager.model.Employee;
import com.bankmanager.service.AuthService;
import com.bankmanager.util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainDashboard extends JFrame {
    private final AuthService authService = new AuthService();
    private final Employee currentUser;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Các panel chức năng
    private CustomerPanel customerPanel;
    private AccountPanel accountPanel;
    private TransactionPanel transactionPanel;
    private ReportPanel reportPanel;
    private EmployeePanel employeePanel;
    
    // Labels thống kê Home
    private JLabel lblHomeCustomers, lblHomeAccounts, lblHomeBalance;

    public MainDashboard() {
        this.currentUser = AuthService.getCurrentLoggedInEmployee();
        initComponents();
        loadHomeStats();
    }

    private void initComponents() {
        setTitle("🏦 BankManager Pro - Hệ Thống Quản Lý Ngân Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));

        // Layout tổng thể: Tây (Sidebar) và Trung Tâm (Header + Body Content)
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(245, 246, 248)); // Màu xám nhạt hiện đại cho app background

        // 1. THANH MENU SIDEBAR BÊN TRÁI
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(15, 34, 64)); // Xanh Navy đậm premium
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));

        // Nhãn logo trên cùng sidebar
        BankLogo bankLogo = new BankLogo(70, false); // isLightMode = false (nền xanh Navy đậm)
        bankLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(bankLogo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel lblAppTitle = new JLabel("BANKMANAGER PRO", JLabel.CENTER);
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAppTitle.setForeground(Color.WHITE);
        lblAppTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAppTitle.setBorder(new EmptyBorder(5, 0, 2, 0));
        sidebar.add(lblAppTitle);

        JLabel lblSubTitle = new JLabel("Enterprise Edition", JLabel.CENTER);
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblSubTitle.setForeground(new Color(200, 200, 200));
        lblSubTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubTitle.setBorder(new EmptyBorder(0, 0, 25, 0));
        sidebar.add(lblSubTitle);

        // Các nút Menu
        JButton btnHome = createSidebarButton("🏠   Trang Chủ");
        JButton btnCust = createSidebarButton("👥   Khách Hàng");
        JButton btnAcc = createSidebarButton("💳   Tài Khoản");
        JButton btnTrans = createSidebarButton("💸   Giao Dịch");
        JButton btnReport = createSidebarButton("📊   Báo Cáo - Thống Kê");
        
        JButton btnEmp = null;
        if (currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            btnEmp = createSidebarButton("🔑   Nhân Viên");
        }
        
        JButton btnLogout = createSidebarButton("🚪   Đăng Xuất");

        // Thêm các button vào sidebar
        sidebar.add(btnHome);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnCust);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnAcc);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnTrans);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnReport);
        if (btnEmp != null) {
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            sidebar.add(btnEmp);
        }
        sidebar.add(Box.createRigidArea(new Dimension(0, 40))); // Khoảng cách xa hơn cho nút đăng xuất
        sidebar.add(btnLogout);

        mainContainer.add(sidebar, BorderLayout.WEST);

        // 2. PHẦN BÊN PHẢI (HEADER + CONTENT AREA)
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(new Color(245, 246, 248));

        // A. Header Bar
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(Color.WHITE);
        headerBar.setPreferredSize(new Dimension(0, 65));
        headerBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(10, 25, 10, 25)
        ));

        JLabel lblBranchName = new JLabel("🏦 HỆ THỐNG GIAO DỊCH NGÂN HÀNG TRUNG TÂM");
        lblBranchName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBranchName.setForeground(new Color(15, 34, 64));
        headerBar.add(lblBranchName, BorderLayout.WEST);

        // Thông tin người đăng nhập bên phải header
        String empName = currentUser != null ? currentUser.getFullName() : "Khách";
        String empRole = currentUser != null ? currentUser.getRole() : "TELLER";
        JLabel lblUserSession = new JLabel("Nhân viên: " + empName + " (" + empRole + ") ");
        lblUserSession.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUserSession.setForeground(Color.DARK_GRAY);
        headerBar.add(lblUserSession, BorderLayout.EAST);

        rightContainer.add(headerBar, BorderLayout.NORTH);

        // B. Content Area sử dụng CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 246, 248));

        // Khởi tạo các panel nghiệp vụ
        customerPanel = new CustomerPanel();
        accountPanel = new AccountPanel();
        transactionPanel = new TransactionPanel();
        reportPanel = new ReportPanel();
        
        if (currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            employeePanel = new EmployeePanel();
        }

        // Đăng ký các card
        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(customerPanel, "CUSTOMER");
        contentPanel.add(accountPanel, "ACCOUNT");
        contentPanel.add(transactionPanel, "TRANSACTION");
        contentPanel.add(reportPanel, "REPORT");
        
        if (employeePanel != null) {
            contentPanel.add(employeePanel, "EMPLOYEE");
        }

        rightContainer.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(rightContainer, BorderLayout.CENTER);

        // Lắng nghe sự kiện chuyển trang
        btnHome.addActionListener(e -> {
            loadHomeStats();
            cardLayout.show(contentPanel, "HOME");
        });
        btnCust.addActionListener(e -> cardLayout.show(contentPanel, "CUSTOMER"));
        btnAcc.addActionListener(e -> cardLayout.show(contentPanel, "ACCOUNT"));
        btnTrans.addActionListener(e -> cardLayout.show(contentPanel, "TRANSACTION"));
        btnReport.addActionListener(e -> {
            // Tải lại báo cáo & biểu đồ khi click
            rightContainer.remove(contentPanel);
            reportPanel = new ReportPanel();
            contentPanel.add(reportPanel, "REPORT");
            
            // Đồng bộ lại employeePanel nếu có
            if (employeePanel != null) {
                contentPanel.add(employeePanel, "EMPLOYEE");
            }
            
            rightContainer.add(contentPanel, BorderLayout.CENTER);
            cardLayout.show(contentPanel, "REPORT");
        });
        
        if (btnEmp != null) {
            btnEmp.addActionListener(e -> cardLayout.show(contentPanel, "EMPLOYEE"));
        }
        
        btnLogout.addActionListener(e -> handleLogout());

        setContentPane(mainContainer);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(210, 40));
        btn.setPreferredSize(new Dimension(210, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(24, 48, 85)); // Màu xanh nhạt hơn sidebar tí
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hiệu ứng hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 76, 128));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(24, 48, 85));
            }
        });
        return btn;
    }

    // Tạo trang chủ Dashboard mặc định
    private JPanel createHomePanel() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBackground(new Color(245, 246, 248));
        p.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Câu chào mừng
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        welcomePanel.setBackground(new Color(245, 246, 248));
        JLabel lblWelcome = new JLabel("Chào ngày làm việc mới, " + (currentUser != null ? currentUser.getFullName() : "Nhân viên") + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(new Color(15, 34, 64));
        JLabel lblDate = new JLabel("Hệ thống quản trị hoạt động chi nhánh ngân hàng giao dịch trực tuyến.");
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDate.setForeground(Color.GRAY);
        welcomePanel.add(lblWelcome);
        welcomePanel.add(lblDate);
        p.add(welcomePanel, BorderLayout.NORTH);

        // Panel các thẻ (Cards) hiển thị số liệu nhanh
        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsGrid.setBackground(new Color(245, 246, 248));
        cardsGrid.setPreferredSize(new Dimension(0, 130));

        lblHomeCustomers = createCard(cardsGrid, "👥 KHÁCH HÀNG ĐĂNG KÝ", "0", new Color(0, 123, 255));
        lblHomeAccounts = createCard(cardsGrid, "💳 TÀI KHOẢN KÍCH HOẠT", "0", new Color(40, 167, 69));
        lblHomeBalance = createCard(cardsGrid, "💰 TỔNG TIỀN GỬI TIẾT KIỆM", "0 VND", new Color(111, 66, 193));
        p.add(cardsGrid, BorderLayout.CENTER);

        // Ảnh mô phỏng thương hiệu ngân hàng lớn hoặc câu slogan ở dưới cùng
        JPanel sloganPanel = new JPanel(new BorderLayout());
        sloganPanel.setBackground(Color.WHITE);
        sloganPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(25, 30, 25, 30)
        ));
        
        JLabel lblSlogan = new JLabel("<html><body style='text-align: center;'><h2>🏦 BANKMANAGER PRO - KIẾN TẠO NIỀM TIN, VỮNG BỀN THỊNH VƯỢNG</h2>" +
                "<p style='color: gray; font-size: 11px;'>Hệ thống giao dịch tự động hóa bảo mật tuyệt đối với mã hóa BCrypt bảo vệ tài khoản nhân viên, " +
                "cơ chế dữ liệu Transaction an toàn ngăn lỗi giao dịch tài chính.</p></body></html>", JLabel.CENTER);
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sloganPanel.add(lblSlogan, BorderLayout.CENTER);
        p.add(sloganPanel, BorderLayout.SOUTH);

        return p;
    }

    private JLabel createCard(JPanel parent, String title, String value, Color themeColor) {
        JPanel card = new JPanel(new BorderLayout(5, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, themeColor), // Vạch viền màu nhấn thương hiệu bên trái
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 225, 225)),
                        new EmptyBorder(18, 20, 18, 20)
                )
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(new Color(15, 34, 64));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        parent.add(card);
        return lblVal;
    }

    private void loadHomeStats() {
        // Tải nhanh thống kê hiển thị lên trang chủ
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 1. Số khách hàng
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM customers")) {
                if (rs.next()) {
                    lblHomeCustomers.setText(rs.getString(1));
                }
            }
            
            // 2. Số tài khoản active
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM accounts WHERE status = 'ACTIVE'")) {
                if (rs.next()) {
                    lblHomeAccounts.setText(rs.getString(1));
                }
            }

            // 3. Tổng số tiền gửi
            try (ResultSet rs = stmt.executeQuery("SELECT SUM(balance) FROM accounts")) {
                if (rs.next()) {
                    double val = rs.getDouble(1);
                    lblHomeBalance.setText(String.format("%,.2f VND", val));
                }
            }
        } catch (SQLException e) {
            System.err.println("Không thể tải số liệu thống kê trang chủ: " + e.getMessage());
        }
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống không?",
                "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            authService.logout();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }
}
