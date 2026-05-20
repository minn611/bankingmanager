package com.bankmanager.view;

import com.bankmanager.dao.CustomerDAO;
import com.bankmanager.model.Customer;
import com.bankmanager.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CustomerPanel extends JPanel {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private JTable tblCustomers;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtFullName, txtCccd, txtPhone, txtEmail, txtDob, txtAddress;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JLabel lblStatus;
    private int selectedCustomerId = -1;

    public CustomerPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Tiêu đề panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("👥 Quản Lý Khách Hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 34, 64));
        titlePanel.add(lblTitle, BorderLayout.WEST);

        // Thanh tìm kiếm nhanh bên phải tiêu đề
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        txtSearch = new JTextField(18);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm theo CCCD, Tên, SĐT...");
        txtSearch.setPreferredSize(new Dimension(0, 30));
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(e -> handleSearch());
        JButton btnReset = new JButton("Tải lại");
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);
        titlePanel.add(searchPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        // 2. Khu vực hiển thị bảng khách hàng (Bên Trái)
        String[] columns = {"ID", "CCCD", "Họ Tên", "Ngày Sinh", "Số Điện Thoại", "Email", "Địa Chỉ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };

        tblCustomers = new JTable(tableModel);
        tblCustomers.setRowHeight(30);
        tblCustomers.getTableHeader().setReorderingAllowed(false);
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCustomers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblCustomers);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Form điền thông tin (Bên Phải)
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(320, 0));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblFormTitle = new JLabel("Hồ Sơ Khách Hàng");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFormTitle.setForeground(new Color(15, 34, 64));
        rightPanel.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;

        // Họ Tên
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Họ tên khách hàng:"), gbc);
        gbc.gridy = 1;
        txtFullName = new JTextField();
        txtFullName.setPreferredSize(new Dimension(0, 30));
        formPanel.add(txtFullName, gbc);

        // CCCD
        gbc.gridy = 2;
        formPanel.add(new JLabel("Số CCCD / CMND:"), gbc);
        gbc.gridy = 3;
        txtCccd = new JTextField();
        txtCccd.setPreferredSize(new Dimension(0, 30));
        formPanel.add(txtCccd, gbc);

        // Ngày Sinh
        gbc.gridy = 4;
        formPanel.add(new JLabel("Ngày sinh (YYYY-MM-DD):"), gbc);
        gbc.gridy = 5;
        txtDob = new JTextField();
        txtDob.setPreferredSize(new Dimension(0, 30));
        formPanel.add(txtDob, gbc);

        // SĐT
        gbc.gridy = 6;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridy = 7;
        txtPhone = new JTextField();
        txtPhone.setPreferredSize(new Dimension(0, 30));
        formPanel.add(txtPhone, gbc);

        // Email
        gbc.gridy = 8;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridy = 9;
        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(0, 30));
        formPanel.add(txtEmail, gbc);

        // Địa Chỉ
        gbc.gridy = 10;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridy = 11;
        txtAddress = new JTextField();
        txtAddress.setPreferredSize(new Dimension(0, 30));
        formPanel.add(txtAddress, gbc);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Nút chức năng
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("➕ Thêm mới");
        btnAdd.setBackground(new Color(40, 167, 69)); // Màu xanh lá
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> handleAdd());

        btnUpdate = new JButton("✏️ Cập nhật");
        btnUpdate.setBackground(new Color(0, 123, 255)); // Màu xanh dương
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> handleUpdate());

        btnDelete = new JButton("❌ Xóa");
        btnDelete.setBackground(new Color(220, 53, 69)); // Màu đỏ
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> handleDelete());

        btnClear = new JButton("🧹 Làm sạch");
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        // Trạng thái / thông báo dưới form
        JPanel bottomFormPanel = new JPanel(new BorderLayout(5, 5));
        bottomFormPanel.setBackground(Color.WHITE);
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(Color.GRAY);
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        bottomFormPanel.add(lblStatus, BorderLayout.NORTH);
        bottomFormPanel.add(btnPanel, BorderLayout.SOUTH);

        rightPanel.add(bottomFormPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Customer> list = customerDAO.getAll();
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getCccd(),
                    c.getFullName(),
                    c.getDob() != null ? c.getDob().toString() : "",  // null-safe
                    c.getPhone(),
                    c.getEmail() != null ? c.getEmail() : "",
                    c.getAddress()
            });
        }
        lblStatus.setText("Tải thành công " + list.size() + " khách hàng.");
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        tableModel.setRowCount(0);
        List<Customer> list = customerDAO.search(keyword);
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getCccd(),
                    c.getFullName(),
                    c.getDob() != null ? c.getDob().toString() : "",  // null-safe
                    c.getPhone(),
                    c.getEmail() != null ? c.getEmail() : "",
                    c.getAddress()
            });
        }
        lblStatus.setText("Tìm thấy " + list.size() + " kết quả khớp.");
    }

    private void handleTableSelection() {
        int selectedRow = tblCustomers.getSelectedRow();
        if (selectedRow != -1) {
            selectedCustomerId = (int) tableModel.getValueAt(selectedRow, 0);
            txtCccd.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtFullName.setText((String) tableModel.getValueAt(selectedRow, 2));
            txtDob.setText((String) tableModel.getValueAt(selectedRow, 3));
            txtPhone.setText((String) tableModel.getValueAt(selectedRow, 4));
            txtEmail.setText((String) tableModel.getValueAt(selectedRow, 5));
            txtAddress.setText((String) tableModel.getValueAt(selectedRow, 6));

            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
            lblStatus.setText("Đang chọn khách hàng ID: " + selectedCustomerId);
        }
    }

    private void clearForm() {
        selectedCustomerId = -1;
        txtCccd.setText("");
        txtFullName.setText("");
        txtDob.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        tblCustomers.clearSelection();

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        lblStatus.setText("Nhập thông tin để thêm khách hàng mới.");
    }

    private Customer validateAndGetCustomerFromForm() {
        String fullName = txtFullName.getText().trim();
        String cccd = txtCccd.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String dobStr = txtDob.getText().trim();
        String address = txtAddress.getText().trim();

        if (fullName.isEmpty() || cccd.isEmpty() || phone.isEmpty() || dobStr.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường bắt buộc (trừ Email)!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (!ValidationUtil.isValidCCCD(cccd)) {
            JOptionPane.showMessageDialog(this, "Số CCCD không hợp lệ (phải gồm 9 hoặc 12 chữ số)!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (phải bắt đầu bằng 0 và gồm 10 chữ số)!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (!email.isEmpty() && !ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Định dạng Email không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Date dob;
        try {
            java.util.Date parsed = df.parse(dobStr);
            dob = new Date(parsed.getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng YYYY-MM-DD!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Customer c = new Customer();
        c.setId(selectedCustomerId);
        c.setFullName(fullName);
        c.setCccd(cccd);
        c.setPhone(phone);
        c.setEmail(email.isEmpty() ? null : email);
        c.setDob(dob);
        c.setAddress(address);
        return c;
    }

    private void handleAdd() {
        Customer c = validateAndGetCustomerFromForm();
        if (c == null) return;

        if (customerDAO.findByCccd(c.getCccd()) != null) {
            JOptionPane.showMessageDialog(this, "Số CCCD này đã tồn tại trên hệ thống!", "Trùng CCCD", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = customerDAO.insert(c);
        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm mới khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm khách hàng mới. Vui lòng kiểm tra lại!", "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedCustomerId == -1) return;

        Customer c = validateAndGetCustomerFromForm();
        if (c == null) return;

        // Kiểm tra trùng CCCD với người khác
        Customer existing = customerDAO.findByCccd(c.getCccd());
        if (existing != null && existing.getId() != c.getId()) {
            JOptionPane.showMessageDialog(this, "Số CCCD này đã được đăng ký bởi khách hàng khác!", "Trùng CCCD", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = customerDAO.update(c);
        if (success) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng thử lại!", "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedCustomerId == -1) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khách hàng này khỏi hệ thống? Tất cả thông tin liên quan có thể bị ảnh hưởng.",
                "Xác nhận xóa khách hàng", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = customerDAO.delete(selectedCustomerId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                // Khóa ngoại chặn xóa
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa khách hàng này vì họ vẫn đang sở hữu tài khoản ngân hàng hoặc có dư nợ chưa thanh toán!",
                        "Không cho phép xóa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
