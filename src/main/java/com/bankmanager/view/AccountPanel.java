package com.bankmanager.view;

import com.bankmanager.dao.CustomerDAO;
import com.bankmanager.model.Account;
import com.bankmanager.model.Customer;
import com.bankmanager.service.AccountService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class AccountPanel extends JPanel {
    private final AccountService accountService = new AccountService();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private JTable tblAccounts;
    private DefaultTableModel tableModel;
    private JComboBox<Customer> cbCustomers;
    private JComboBox<String> cbAccountType, cbStatusFilter;
    private JTextField txtInitialBalance, txtSearch, txtCustomAccountNumber;
    private JCheckBox chkAutoGenerate;
    private JButton btnOpenAccount, btnLock, btnUnlock, btnCloseAccount;
    private JLabel lblStatus;
    private String selectedAccountNumber = null;

    public AccountPanel() {
        initComponents();
        loadAccounts();
        loadCustomers();

        // 🔑 Tự động tải lại danh sách khách hàng mỗi khi panel được hiển thị
        // (Đảm bảo khách hàng vừa thêm ở tab Khách Hàng sẽ xuất hiện ngay)
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadCustomers();
            }
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Tiêu đề panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("💳 Quản Lý Tài Khoản Ngân Hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 34, 64));
        titlePanel.add(lblTitle, BorderLayout.WEST);

        // Tìm kiếm & Lọc
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(Color.WHITE);
        txtSearch = new JTextField(15);
        txtSearch.putClientProperty("JTextField.placeholderText", "Số tài khoản...");
        txtSearch.setPreferredSize(new Dimension(0, 30));
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> handleSearch());

        cbStatusFilter = new JComboBox<>(new String[]{"-- Tất cả trạng thái --", "ACTIVE", "LOCKED", "CLOSED"});
        cbStatusFilter.addActionListener(e -> filterAccounts());

        filterPanel.add(txtSearch);
        filterPanel.add(btnSearch);
        filterPanel.add(cbStatusFilter);
        titlePanel.add(filterPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        // 2. Bảng tài khoản (Bên Trái)
        String[] columns = {"Số Tài Khoản", "Mã KH", "Tên Khách Hàng", "Loại Tài Khoản", "Số Dư (VND)", "Trạng Thái", "Ngày Tạo"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAccounts = new JTable(tableModel);
        tblAccounts.setRowHeight(30);
        tblAccounts.getTableHeader().setReorderingAllowed(false);
        tblAccounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAccounts.getSelectionModel().addListSelectionListener(e -> handleTableSelection());

        JScrollPane scrollPane = new JScrollPane(tblAccounts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Form mở tài khoản mới & Đổi trạng thái (Bên Phải)
        JPanel rightPanel = new JPanel(new BorderLayout(12, 12));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(320, 0));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Form mở tài khoản
        JPanel openAccountPanel = new JPanel(new BorderLayout(8, 8));
        openAccountPanel.setBackground(Color.WHITE);
        JLabel lblFormTitle = new JLabel("Mở Tài Khoản Mới");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFormTitle.setForeground(new Color(15, 34, 64));
        openAccountPanel.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;

        // Chọn khách hàng
        gbc.gridx = 0; gbc.gridy = 0;
        JPanel custLabelRow = new JPanel(new BorderLayout(5, 0));
        custLabelRow.setBackground(Color.WHITE);
        custLabelRow.add(new JLabel("Chọn khách hàng chủ tài khoản:"), BorderLayout.WEST);
        JButton btnRefreshCust = new JButton("↻ Tải lại");
        btnRefreshCust.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnRefreshCust.setMargin(new Insets(1, 4, 1, 4));
        btnRefreshCust.setToolTipText("Tải lại danh sách khách hàng mới nhất");
        btnRefreshCust.addActionListener(e -> loadCustomers());
        custLabelRow.add(btnRefreshCust, BorderLayout.EAST);
        formGrid.add(custLabelRow, gbc);
        gbc.gridy = 1;
        cbCustomers = new JComboBox<>();
        cbCustomers.setPreferredSize(new Dimension(0, 30));
        formGrid.add(cbCustomers, gbc);

        // Loại tài khoản
        gbc.gridy = 2;
        formGrid.add(new JLabel("Loại tài khoản:"), gbc);
        gbc.gridy = 3;
        cbAccountType = new JComboBox<>(new String[]{"CHECKING (Thanh toán)", "SAVINGS (Tiết kiệm)"});
        cbAccountType.setPreferredSize(new Dimension(0, 30));
        formGrid.add(cbAccountType, gbc);

        // Số tài khoản
        gbc.gridy = 4;
        JPanel accNumLabelRow = new JPanel(new BorderLayout(5, 0));
        accNumLabelRow.setBackground(Color.WHITE);
        accNumLabelRow.add(new JLabel("Số tài khoản:"), BorderLayout.WEST);
        chkAutoGenerate = new JCheckBox("Tự động");
        chkAutoGenerate.setSelected(true);
        chkAutoGenerate.setBackground(Color.WHITE);
        chkAutoGenerate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkAutoGenerate.setToolTipText("Tích để hệ thống tự sinh số tài khoản, bỏ tích để tự điền");
        chkAutoGenerate.addActionListener(e -> {
            txtCustomAccountNumber.setEnabled(!chkAutoGenerate.isSelected());
            if (chkAutoGenerate.isSelected()) {
                txtCustomAccountNumber.setText("");
                txtCustomAccountNumber.putClientProperty("JTextField.placeholderText", "Hệ thống tự sinh...");
            } else {
                txtCustomAccountNumber.requestFocus();
            }
        });
        accNumLabelRow.add(chkAutoGenerate, BorderLayout.EAST);
        formGrid.add(accNumLabelRow, gbc);

        gbc.gridy = 5;
        txtCustomAccountNumber = new JTextField();
        txtCustomAccountNumber.setPreferredSize(new Dimension(0, 30));
        txtCustomAccountNumber.setEnabled(false); // Mặc định: tự động
        txtCustomAccountNumber.putClientProperty("JTextField.placeholderText", "Hệ thống tự sinh...");
        txtCustomAccountNumber.setBackground(new Color(245, 245, 245));
        txtCustomAccountNumber.setFont(new Font("Courier New", Font.BOLD, 13)); // Font monospace cho số TK
        formGrid.add(txtCustomAccountNumber, gbc);

        // Số dư ban đầu
        gbc.gridy = 6;
        formGrid.add(new JLabel("Số dư gửi ban đầu (VND):"), gbc);
        gbc.gridy = 7;
        txtInitialBalance = new JTextField("0");
        txtInitialBalance.setPreferredSize(new Dimension(0, 30));
        formGrid.add(txtInitialBalance, gbc);

        openAccountPanel.add(formGrid, BorderLayout.CENTER);

        btnOpenAccount = new JButton("🚀 Mở Tài Khoản Mới");
        btnOpenAccount.setBackground(new Color(40, 167, 69)); // màu xanh lá
        btnOpenAccount.setForeground(Color.WHITE);
        btnOpenAccount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOpenAccount.setPreferredSize(new Dimension(0, 40));
        btnOpenAccount.addActionListener(e -> handleOpenAccount());
        openAccountPanel.add(btnOpenAccount, BorderLayout.SOUTH);

        rightPanel.add(openAccountPanel, BorderLayout.NORTH);

        // Panel quản lý trạng thái tài khoản đang chọn
        JPanel managePanel = new JPanel(new BorderLayout(8, 8));
        managePanel.setBackground(Color.WHITE);
        managePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JLabel lblManageTitle = new JLabel("Thao Tác Tài Khoản Đang Chọn");
        lblManageTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblManageTitle.setForeground(new Color(15, 34, 64));
        lblManageTitle.setBorder(new EmptyBorder(10, 0, 5, 0));
        managePanel.add(lblManageTitle, BorderLayout.NORTH);

        JPanel actionBtnPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        actionBtnPanel.setBackground(Color.WHITE);

        btnUnlock = new JButton("🔓 Kích hoạt / Mở khóa");
        btnUnlock.setBackground(new Color(0, 123, 255));
        btnUnlock.setForeground(Color.WHITE);
        btnUnlock.setEnabled(false);
        btnUnlock.addActionListener(e -> changeStatus("ACTIVE"));

        btnLock = new JButton("🔒 Khóa tài khoản tạm thời");
        btnLock.setBackground(new Color(255, 193, 7)); // màu vàng
        btnLock.setForeground(Color.BLACK);
        btnLock.setEnabled(false);
        btnLock.addActionListener(e -> changeStatus("LOCKED"));

        btnCloseAccount = new JButton("🛑 Đóng tài khoản vĩnh viễn");
        btnCloseAccount.setBackground(new Color(220, 53, 69)); // màu đỏ
        btnCloseAccount.setForeground(Color.WHITE);
        btnCloseAccount.setEnabled(false);
        btnCloseAccount.addActionListener(e -> changeStatus("CLOSED"));

        actionBtnPanel.add(btnUnlock);
        actionBtnPanel.add(btnLock);
        actionBtnPanel.add(btnCloseAccount);
        managePanel.add(actionBtnPanel, BorderLayout.CENTER);

        rightPanel.add(managePanel, BorderLayout.CENTER);

        // Trạng thái ở chân form
        lblStatus = new JLabel("Hệ thống hoạt động bình thường.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(Color.GRAY);
        rightPanel.add(lblStatus, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);
    }

    private void loadAccounts() {
        tableModel.setRowCount(0);
        List<Account> accounts = accountService.getAllAccounts();
        for (Account a : accounts) {
            Customer customer = customerDAO.findById(a.getCustomerId());
            String custName = customer != null ? customer.getFullName() : "Không xác định";
            tableModel.addRow(new Object[]{
                    a.getAccountNumber(),
                    a.getCustomerId(),
                    custName,
                    a.getAccountType(),
                    String.format("%,.2f", a.getBalance()),
                    a.getStatus(),
                    a.getCreatedAt() != null ? a.getCreatedAt().toString() : "Không rõ" // null-safe
            });
        }
        lblStatus.setText("Đã tải " + accounts.size() + " tài khoản ngân hàng.");
    }

    private void loadCustomers() {
        cbCustomers.removeAllItems();
        List<Customer> list = customerDAO.getAll();
        for (Customer c : list) {
            cbCustomers.addItem(c);
        }
    }

    private void handleSearch() {
        String num = txtSearch.getText().trim();
        if (num.isEmpty()) {
            loadAccounts();
            return;
        }

        Account a = accountService.getAccount(num);
        tableModel.setRowCount(0);
        if (a != null) {
            Customer customer = customerDAO.findById(a.getCustomerId());
            String custName = customer != null ? customer.getFullName() : "Không xác định";
            tableModel.addRow(new Object[]{
                    a.getAccountNumber(),
                    a.getCustomerId(),
                    custName,
                    a.getAccountType(),
                    String.format("%,.2f", a.getBalance()),
                    a.getStatus(),
                    a.getCreatedAt().toString()
            });
            lblStatus.setText("Tìm thấy tài khoản " + num);
        } else {
            lblStatus.setText("Không tìm thấy tài khoản: " + num);
        }
    }

    private void filterAccounts() {
        int index = cbStatusFilter.getSelectedIndex();
        if (index <= 0) {
            loadAccounts();
            return;
        }

        String filterStatus = cbStatusFilter.getSelectedItem().toString();
        tableModel.setRowCount(0);
        List<Account> accounts = accountService.getAllAccounts();
        int count = 0;
        for (Account a : accounts) {
            if (filterStatus.equalsIgnoreCase(a.getStatus())) {
                Customer customer = customerDAO.findById(a.getCustomerId());
                String custName = customer != null ? customer.getFullName() : "Không xác định";
                tableModel.addRow(new Object[]{
                        a.getAccountNumber(),
                        a.getCustomerId(),
                        custName,
                        a.getAccountType(),
                        String.format("%,.2f", a.getBalance()),
                        a.getStatus(),
                        a.getCreatedAt().toString()
                });
                count++;
            }
        }
        lblStatus.setText("Lọc: " + filterStatus + ". Tìm thấy " + count + " tài khoản.");
    }

    private void handleTableSelection() {
        int selectedRow = tblAccounts.getSelectedRow();
        if (selectedRow != -1) {
            selectedAccountNumber = (String) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 5);

            lblStatus.setText("Đang chọn tài khoản: " + selectedAccountNumber + " (" + status + ")");

            if ("CLOSED".equalsIgnoreCase(status)) {
                // Tài khoản đã đóng vĩnh viễn không cho sửa đổi trạng thái nữa
                btnUnlock.setEnabled(false);
                btnLock.setEnabled(false);
                btnCloseAccount.setEnabled(false);
            } else if ("LOCKED".equalsIgnoreCase(status)) {
                btnUnlock.setEnabled(true);
                btnLock.setEnabled(false);
                btnCloseAccount.setEnabled(true);
            } else if ("ACTIVE".equalsIgnoreCase(status)) {
                btnUnlock.setEnabled(false);
                btnLock.setEnabled(true);
                btnCloseAccount.setEnabled(true);
            }
        } else {
            selectedAccountNumber = null;
            btnUnlock.setEnabled(false);
            btnLock.setEnabled(false);
            btnCloseAccount.setEnabled(false);
        }
    }

    private void handleOpenAccount() {
        Customer c = (Customer) cbCustomers.getSelectedItem();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Không có khách hàng nào được chọn! Hãy tạo khách hàng trước.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String balStr = txtInitialBalance.getText().trim();
        BigDecimal balance;
        try {
            balance = new BigDecimal(balStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số dư gửi ban đầu không hợp lệ (phải là số)!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String typeItem = cbAccountType.getSelectedItem().toString();
        String accountType = typeItem.contains("CHECKING") ? "CHECKING" : "SAVINGS";

        // Xác định số tài khoản: tự nhập hoặc tự động sinh
        String customAccNum = null;
        if (!chkAutoGenerate.isSelected()) {
            customAccNum = txtCustomAccountNumber.getText().trim();
            if (customAccNum.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tài khoản hoặc chọn 'Ự Tự động'!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Kiểm tra định dạng: chỉ chứa chữ số, độ dài 6–20 ký tự
            if (!customAccNum.matches("\\d{6,20}")) {
                JOptionPane.showMessageDialog(this, "Số tài khoản phải gồm từ 6 đến 20 chữ số!", "Không hợp lệ", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            Account account = accountService.openAccount(c.getId(), accountType, balance, customAccNum);
            JOptionPane.showMessageDialog(this,
                    "✅ Mở tài khoản thành công!\nSố tài khoản: " + account.getAccountNumber() +
                            "\nChủ tài khoản: " + c.getFullName() +
                            "\nSố dư ban đầu: " + String.format("%,.0f VND", balance),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            txtInitialBalance.setText("0");
            txtCustomAccountNumber.setText("");
            loadAccounts();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeStatus(String newStatus) {
        if (selectedAccountNumber == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đổi trạng thái tài khoản " + selectedAccountNumber + " sang " + newStatus + "?",
                "Xác nhận thay đổi trạng thái", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (choice == ChoiceActive()) { // JOptionPane.YES_OPTION
            try {
                accountService.changeAccountStatus(selectedAccountNumber, newStatus);
                JOptionPane.showMessageDialog(this, "Thay đổi trạng thái tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAccounts();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int ChoiceActive() {
        return JOptionPane.YES_OPTION;
    }
}
