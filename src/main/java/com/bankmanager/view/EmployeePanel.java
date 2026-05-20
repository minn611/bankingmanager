package com.bankmanager.view;

import com.bankmanager.dao.EmployeeDAO;
import com.bankmanager.model.Employee;
import com.bankmanager.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel Quản lý Nhân viên (Giao dịch viên) - Chỉ dành riêng cho Quản trị viên (ADMIN).
 * Hỗ trợ các chức năng CRUD nhân viên: Xem danh sách, Thêm mới, Cập nhật thông tin/trạng thái/quyền hạn, Xóa nhân viên,
 * tự động mã hóa BCrypt bảo mật tuyệt đối khi tạo/đổi mật khẩu.
 */
public class EmployeePanel extends JPanel {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    
    private JTable tblEmployees;
    private DefaultTableModel tableModel;
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtFullName;
    private JComboBox<String> cbRole;
    private JComboBox<String> cbStatus;
    
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JTextField txtSearch;
    private JButton btnSearch;
    
    private Employee selectedEmployee = null;

    public EmployeePanel() {
        initComponents();
        loadEmployeeData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Tiêu đề Trang (Header)
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setBackground(new Color(245, 246, 248));
        
        JLabel lblTitle = new JLabel("🔑 QUẢN TRỊ NHÂN VIÊN & GIAO DỊCH VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 34, 64));
        
        JLabel lblSub = new JLabel("Thêm mới, điều chỉnh chức vụ, khóa/mở khóa tài khoản giao dịch viên trong hệ thống.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        
        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Nội dung Trung tâm (Chia làm 2 phần: Bảng danh sách bên trái, Form nhập liệu bên phải)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 246, 248));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 15);

        // --- Bên trái: Danh sách nhân viên ---
        JPanel listPanel = new JPanel(new BorderLayout(10, 10));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Thanh Tìm kiếm nhân viên
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setBackground(Color.WHITE);
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm theo tên tài khoản hoặc họ tên...");
        txtSearch.setPreferredSize(new Dimension(0, 35));
        btnSearch = new JButton("Tìm Kiếm");
        btnSearch.setBackground(new Color(15, 34, 64));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.addActionListener(e -> performSearch());
        searchBar.add(txtSearch, BorderLayout.CENTER);
        searchBar.add(btnSearch, BorderLayout.EAST);
        listPanel.add(searchBar, BorderLayout.NORTH);

        // Bảng dữ liệu nhân viên
        String[] columns = {"ID", "Tên tài khoản", "Họ và tên", "Chức vụ", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblEmployees = new JTable(tableModel);
        tblEmployees.setRowHeight(30);
        tblEmployees.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblEmployees.getTableHeader().setBackground(new Color(230, 235, 245));
        tblEmployees.getSelectionModel().addListSelectionListener(e -> handleTableSelection());
        
        JScrollPane scrollPane = new JScrollPane(tblEmployees);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.weighty = 1.0;
        centerPanel.add(listPanel, gbc);

        // --- Bên phải: Form Nhập liệu (Thêm/Sửa) ---
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setPreferredSize(new Dimension(320, 0));

        JLabel lblFormTitle = new JLabel("THÔNG TIN NHÂN VIÊN");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(new Color(15, 34, 64));
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        formPanel.add(lblFormTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints fGbc = new GridBagConstraints();
        fGbc.fill = GridBagConstraints.HORIZONTAL;
        fGbc.insets = new Insets(6, 0, 6, 0);
        fGbc.weightx = 1.0;

        // Ô nhập Username
        fGbc.gridx = 0;
        fGbc.gridy = 0;
        fieldsPanel.add(new JLabel("Tên tài khoản:"), fGbc);
        fGbc.gridy = 1;
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(0, 33));
        fieldsPanel.add(txtUsername, fGbc);

        // Ô nhập Password
        fGbc.gridy = 2;
        fieldsPanel.add(new JLabel("Mật khẩu:"), fGbc);
        fGbc.gridy = 3;
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(0, 33));
        txtPassword.putClientProperty("JTextField.placeholderText", "Bỏ trống nếu giữ nguyên mật khẩu...");
        fieldsPanel.add(txtPassword, fGbc);

        // Ô nhập Họ Tên
        fGbc.gridy = 4;
        fieldsPanel.add(new JLabel("Họ và tên nhân viên:"), fGbc);
        fGbc.gridy = 5;
        txtFullName = new JTextField();
        txtFullName.setPreferredSize(new Dimension(0, 33));
        fieldsPanel.add(txtFullName, fGbc);

        // Vai trò
        fGbc.gridy = 6;
        fieldsPanel.add(new JLabel("Chức vụ quyền hạn:"), fGbc);
        fGbc.gridy = 7;
        cbRole = new JComboBox<>(new String[]{"TELLER", "ADMIN"});
        cbRole.setPreferredSize(new Dimension(0, 33));
        fieldsPanel.add(cbRole, fGbc);

        // Trạng thái tài khoản
        fGbc.gridy = 8;
        fieldsPanel.add(new JLabel("Trạng thái hoạt động:"), fGbc);
        fGbc.gridy = 9;
        cbStatus = new JComboBox<>(new String[]{"ACTIVE", "LOCKED"});
        cbStatus.setPreferredSize(new Dimension(0, 33));
        fieldsPanel.add(cbStatus, fGbc);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);

        // Panel Các nút tác vụ CRUD
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnAdd = new JButton("THÊM MỚI");
        btnAdd.setBackground(new Color(40, 167, 69)); // Xanh lá
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAdd.addActionListener(e -> addEmployee());

        btnUpdate = new JButton("CẬP NHẬT");
        btnUpdate.setBackground(new Color(0, 123, 255)); // Xanh dương
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnUpdate.addActionListener(e -> updateEmployee());

        btnDelete = new JButton("XÓA");
        btnDelete.setBackground(new Color(220, 53, 69)); // Đỏ
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDelete.addActionListener(e -> deleteEmployee());

        btnClear = new JButton("LÀM MỚI");
        btnClear.setBackground(Color.DARK_GRAY);
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnClear.addActionListener(e -> clearForm());

        buttonsPanel.add(btnAdd);
        buttonsPanel.add(btnUpdate);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnClear);
        formPanel.add(buttonsPanel, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(formPanel, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        List<Employee> list = employeeDAO.getAll();
        for (Employee emp : list) {
            tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getUsername(),
                    emp.getFullName(),
                    emp.getRole(),
                    emp.getStatus()
            });
        }
    }

    private void handleTableSelection() {
        int row = tblEmployees.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            selectedEmployee = employeeDAO.findById(id);
            if (selectedEmployee != null) {
                txtUsername.setText(selectedEmployee.getUsername());
                txtUsername.setEditable(false); // Không cho sửa tên tài khoản khi cập nhật
                txtPassword.setText(""); // Để trống mật khẩu
                txtFullName.setText(selectedEmployee.getFullName());
                cbRole.setSelectedItem(selectedEmployee.getRole());
                cbStatus.setSelectedItem(selectedEmployee.getStatus());
            }
        }
    }

    private void clearForm() {
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtPassword.setText("");
        txtFullName.setText("");
        cbRole.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        tblEmployees.clearSelection();
        selectedEmployee = null;
    }

    private void addEmployee() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String role = (String) cbRole.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ Tên tài khoản, Mật khẩu và Họ tên!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra trùng username
        if (employeeDAO.findByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Tên tài khoản này đã tồn tại trên hệ thống!", "Trùng tài khoản", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee emp = new Employee();
        emp.setUsername(username);
        emp.setPassword(PasswordUtil.hashPassword(password)); // Mã hóa BCrypt mật khẩu tự động!
        emp.setFullName(fullName);
        emp.setRole(role);
        emp.setStatus(status);

        if (employeeDAO.insert(emp)) {
            JOptionPane.showMessageDialog(this, "Thêm mới nhân viên '" + fullName + "' thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadEmployeeData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm nhân viên mới!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên trong bảng để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Không cho phép tự khóa hoặc hạ quyền tài khoản admin chính đang chạy hệ thống để tránh lỗi bảo mật
        if ("admin".equals(selectedEmployee.getUsername()) && !"ACTIVE".equals(cbStatus.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "Không thể tự khóa tài khoản Admin tối cao đang vận hành hệ thống!", "Ràng buộc bảo mật", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fullName = txtFullName.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cbRole.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ tên nhân viên!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        selectedEmployee.setFullName(fullName);
        selectedEmployee.setRole(role);
        selectedEmployee.setStatus(status);
        
        // Nếu có nhập mật khẩu mới, băm lại và cập nhật
        if (!password.isEmpty()) {
            selectedEmployee.setPassword(PasswordUtil.hashPassword(password));
            selectedEmployee.setLoginAttempts(0); // Reset số lần nhập sai nếu đổi mật khẩu
        }

        if (employeeDAO.update(selectedEmployee)) {
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên '" + fullName + "' thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadEmployeeData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể cập nhật thông tin nhân viên!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên trong bảng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("admin".equals(selectedEmployee.getUsername())) {
            JOptionPane.showMessageDialog(this, "Không được phép xóa tài khoản Admin tối cao của hệ thống!", "Ràng buộc bảo mật", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhân viên '" + selectedEmployee.getFullName() + "' khỏi hệ thống?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            if (employeeDAO.delete(selectedEmployee.getId())) {
                JOptionPane.showMessageDialog(this, "Đã xóa thành công nhân viên!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên do có ràng buộc lịch sử nhật ký hệ thống!", "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        List<Employee> list = employeeDAO.getAll();
        
        for (Employee emp : list) {
            if (emp.getUsername().toLowerCase().contains(keyword) || 
                emp.getFullName().toLowerCase().contains(keyword)) {
                
                tableModel.addRow(new Object[]{
                        emp.getId(),
                        emp.getUsername(),
                        emp.getFullName(),
                        emp.getRole(),
                        emp.getStatus()
                });
            }
        }
    }
}
