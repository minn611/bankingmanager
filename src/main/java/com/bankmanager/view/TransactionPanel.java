package com.bankmanager.view;

import com.bankmanager.model.Account;
import com.bankmanager.model.Transaction;
import com.bankmanager.service.AccountService;
import com.bankmanager.service.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

public class TransactionPanel extends JPanel {
    private final TransactionService transactionService = new TransactionService();
    private final AccountService accountService = new AccountService();
    private final SecureRandom random = new SecureRandom();

    private JTable tblHistory;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    // Nạp tiền
    private JTextField txtDepAccount, txtDepAmount, txtDepDesc;
    
    // Rút tiền
    private JTextField txtWitAccount, txtWitAmount, txtWitDesc;

    // Chuyển tiền
    private JTextField txtTrsfFrom, txtTrsfTo, txtTrsfAmount, txtTrsfDesc;

    // Thanh toán hóa đơn
    private JTextField txtBillAccount, txtBillAmount;
    private JComboBox<String> cbBillType, cbBillProvider;

    public TransactionPanel() {
        initComponents();
        loadHistory();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Tiêu đề + Tabs giao dịch gộp lại thành một vùng NORTH duy nhất
        JPanel northContainer = new JPanel(new BorderLayout(0, 8));
        northContainer.setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("💸 Thực Hiện Giao Dịch Tài Chính");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 34, 64));
        titlePanel.add(lblTitle, BorderLayout.WEST);

        lblStatus = new JLabel("Giao dịch ngân hàng bảo mật bằng mã OTP xác thực.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(Color.GRAY);
        titlePanel.add(lblStatus, BorderLayout.EAST);
        northContainer.add(titlePanel, BorderLayout.NORTH);

        // 2. Nội dung các nghiệp vụ (Sử dụng TabbedPane)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("📥 NẠP TIỀN MẶT", createDepositPanel());
        tabbedPane.addTab("📤 RÚT TIỀN MẶT", createWithdrawPanel());
        tabbedPane.addTab("🔄 CHUYỂN KHOẢN", createTransferPanel());
        tabbedPane.addTab("🧾 THANH TOÁN HÓA ĐƠN", createBillPanel());

        tabbedPane.setPreferredSize(new Dimension(0, 250));
        northContainer.add(tabbedPane, BorderLayout.CENTER);

        add(northContainer, BorderLayout.NORTH);

        // 3. Bảng lịch sử giao dịch toàn hệ thống (Phía Dưới)
        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.setBackground(Color.WHITE);
        
        JLabel lblHistoryTitle = new JLabel("📜 Lịch Sử Giao Dịch Gần Đây");
        lblHistoryTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHistoryTitle.setForeground(new Color(15, 34, 64));
        lblHistoryTitle.setBorder(new EmptyBorder(5, 0, 5, 0));
        historyPanel.add(lblHistoryTitle, BorderLayout.NORTH);

        String[] columns = {"Mã GD", "TK Nguồn", "TK Đích", "Loại Giao Dịch", "Số Tiền (VND)", "Phí", "Nội Dung", "Thời Gian"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblHistory = new JTable(tableModel);
        tblHistory.setRowHeight(28);
        tblHistory.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(tblHistory);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        add(historyPanel, BorderLayout.CENTER);
    }

    // --- TAB NẠP TIỀN ---
    private JPanel createDepositPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("Số tài khoản nhận:"), gbc);
        gbc.gridx = 1;
        txtDepAccount = new JTextField(15);
        p.add(txtDepAccount, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        p.add(new JLabel("Số tiền nạp (VND):"), gbc);
        gbc.gridx = 1;
        txtDepAmount = new JTextField(15);
        p.add(txtDepAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        p.add(new JLabel("Nội dung nộp tiền:"), gbc);
        gbc.gridx = 1;
        txtDepDesc = new JTextField(20);
        txtDepDesc.setText("Nộp tiền mặt vào tài khoản");
        p.add(txtDepDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton btnSubmit = new JButton("Nạp Tiền");
        btnSubmit.setBackground(new Color(40, 167, 69));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(150, 35));
        btnSubmit.addActionListener(e -> executeDeposit());
        p.add(btnSubmit, gbc);

        return p;
    }

    // --- TAB RÚT TIỀN ---
    private JPanel createWithdrawPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("Số tài khoản rút:"), gbc);
        gbc.gridx = 1;
        txtWitAccount = new JTextField(15);
        p.add(txtWitAccount, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        p.add(new JLabel("Số tiền rút (VND):"), gbc);
        gbc.gridx = 1;
        txtWitAmount = new JTextField(15);
        p.add(txtWitAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        p.add(new JLabel("Nội dung rút tiền:"), gbc);
        gbc.gridx = 1;
        txtWitDesc = new JTextField(20);
        txtWitDesc.setText("Rút tiền mặt tại quầy");
        p.add(txtWitDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton btnSubmit = new JButton("Rút Tiền");
        btnSubmit.setBackground(new Color(255, 193, 7));
        btnSubmit.setForeground(Color.BLACK);
        btnSubmit.setPreferredSize(new Dimension(150, 35));
        btnSubmit.addActionListener(e -> executeWithdraw());
        p.add(btnSubmit, gbc);

        return p;
    }

    // --- TAB CHUYỂN TIỀN ---
    private JPanel createTransferPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 15, 8, 15);

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("Số tài khoản nguồn:"), gbc);
        gbc.gridx = 1;
        txtTrsfFrom = new JTextField(15);
        p.add(txtTrsfFrom, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        p.add(new JLabel("Số tài khoản đích:"), gbc);
        gbc.gridx = 1;
        txtTrsfTo = new JTextField(15);
        p.add(txtTrsfTo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        p.add(new JLabel("Số tiền chuyển (VND):"), gbc);
        gbc.gridx = 1;
        txtTrsfAmount = new JTextField(15);
        p.add(txtTrsfAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        p.add(new JLabel("Nội dung chuyển khoản:"), gbc);
        gbc.gridx = 1;
        txtTrsfDesc = new JTextField(20);
        txtTrsfDesc.setText("Chuyển tiền nội bộ");
        p.add(txtTrsfDesc, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton btnSubmit = new JButton("Chuyển Khoản");
        btnSubmit.setBackground(new Color(0, 123, 255));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(150, 35));
        btnSubmit.addActionListener(e -> executeTransfer());
        p.add(btnSubmit, gbc);

        return p;
    }

    // --- TAB THANH TOÁN HÓA ĐƠN ---
    private JPanel createBillPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 15, 8, 15);

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("Số tài khoản thanh toán:"), gbc);
        gbc.gridx = 1;
        txtBillAccount = new JTextField(15);
        p.add(txtBillAccount, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        p.add(new JLabel("Loại dịch vụ:"), gbc);
        gbc.gridx = 1;
        cbBillType = new JComboBox<>(new String[]{"ĐIỆN", "NƯỚC", "INTERNET", "VIỄN THÔNG"});
        p.add(cbBillType, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        p.add(new JLabel("Nhà cung cấp dịch vụ:"), gbc);
        gbc.gridx = 1;
        cbBillProvider = new JComboBox<>(new String[]{"EVN (Điện lực)", "SAWACO (Nước sạch)", "VIETTEL", "FPT TELECOM"});
        p.add(cbBillProvider, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        p.add(new JLabel("Số tiền hóa đơn (VND):"), gbc);
        gbc.gridx = 1;
        txtBillAmount = new JTextField(15);
        p.add(txtBillAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton btnSubmit = new JButton("Thanh Toán");
        btnSubmit.setBackground(new Color(111, 66, 193)); // Màu tím thanh lịch
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(150, 35));
        btnSubmit.addActionListener(e -> executeBillPayment());
        p.add(btnSubmit, gbc);

        return p;
    }

    private void loadHistory() {
        tableModel.setRowCount(0);
        List<Transaction> list = transactionService.getAllTransactions();
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                    t.getTransactionId(),
                    t.getFromAccount() != null ? t.getFromAccount() : "Tiền mặt tại quầy",
                    t.getToAccount() != null ? t.getToAccount() : "Tiền mặt rút ra",
                    t.getTransactionType(),
                    String.format("%,.2f", t.getAmount()),
                    String.format("%,.2f", t.getFee()),
                    t.getDescription(),
                    t.getTransactionDate().toString()
            });
        }
    }

    /**
     * Mô phỏng kiểm tra mã OTP giao dịch.
     * Trả về true nếu người dùng nhập đúng OTP.
     */
    private boolean verifyOTP() {
        // Sinh mã OTP 6 số
        int otp = random.nextInt(900000) + 100000;
        String otpMessage = "🔒 BẢO MẬT GIAO DỊCH NGÂN HÀNG\n\n" +
                "Mã xác thực giao dịch OTP của bạn là: " + otp + "\n" +
                "Mã có hiệu lực trong 2 phút. Vui lòng không chia sẻ mã này!";
        
        String input = JOptionPane.showInputDialog(this, otpMessage, "XÁC THỰC MÃ OTP GIAO DỊCH", JOptionPane.QUESTION_MESSAGE);
        
        if (input == null) return false; // Hủy bỏ
        
        if (input.trim().equals(String.valueOf(otp))) {
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Mã OTP nhập vào không chính xác! Giao dịch đã bị từ chối.", "Xác thực thất bại", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void executeDeposit() {
        String acc = txtDepAccount.getText().trim();
        String amtStr = txtDepAmount.getText().trim();
        String desc = txtDepDesc.getText().trim();

        if (acc.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ số tài khoản nhận và số tiền!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amtStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Thực hiện nạp tiền (Nạp tiền mặt không cần OTP bảo mật, rút/chuyển mới cần)
        try {
            boolean success = transactionService.deposit(acc, amount, desc);
            if (success) {
                JOptionPane.showMessageDialog(this, "Nạp tiền thành công vào tài khoản " + acc + "!", "Giao dịch thành công", JOptionPane.INFORMATION_MESSAGE);
                txtDepAccount.setText("");
                txtDepAmount.setText("");
                loadHistory();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Giao dịch thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeWithdraw() {
        String acc = txtWitAccount.getText().trim();
        String amtStr = txtWitAmount.getText().trim();
        String desc = txtWitDesc.getText().trim();

        if (acc.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền số tài khoản rút và số tiền!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amtStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Yêu cầu xác thực OTP để rút tiền
        if (!verifyOTP()) return;

        try {
            boolean success = transactionService.withdraw(acc, amount, desc);
            if (success) {
                JOptionPane.showMessageDialog(this, "Rút tiền thành công từ tài khoản " + acc + "!\nSố tiền: " + String.format("%,.2f VND", amount), "Giao dịch thành công", JOptionPane.INFORMATION_MESSAGE);
                txtWitAccount.setText("");
                txtWitAmount.setText("");
                loadHistory();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Giao dịch thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeTransfer() {
        String fromAcc = txtTrsfFrom.getText().trim();
        String toAcc = txtTrsfTo.getText().trim();
        String amtStr = txtTrsfAmount.getText().trim();
        String desc = txtTrsfDesc.getText().trim();

        if (fromAcc.isEmpty() || toAcc.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các trường chuyển tiền!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amtStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền chuyển không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xác thực OTP chuyển khoản
        if (!verifyOTP()) return;

        try {
            boolean success = transactionService.transfer(fromAcc, toAcc, amount, desc);
            if (success) {
                JOptionPane.showMessageDialog(this, "Chuyển khoản thành công!\nTừ tài khoản: " + fromAcc + "\nSang tài khoản: " + toAcc + "\nSố tiền: " + String.format("%,.2f VND", amount), "Giao dịch thành công", JOptionPane.INFORMATION_MESSAGE);
                txtTrsfFrom.setText("");
                txtTrsfTo.setText("");
                txtTrsfAmount.setText("");
                loadHistory();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Giao dịch thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeBillPayment() {
        String acc = txtBillAccount.getText().trim();
        String amtStr = txtBillAmount.getText().trim();
        String type = cbBillType.getSelectedItem().toString();
        String provider = cbBillProvider.getSelectedItem().toString();

        if (acc.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền tài khoản thanh toán và số tiền hóa đơn!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amtStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xác thực OTP thanh toán hóa đơn
        if (!verifyOTP()) return;

        try {
            boolean success = transactionService.payBill(acc, type, amount, provider);
            if (success) {
                JOptionPane.showMessageDialog(this, "Thanh toán hóa đơn dịch vụ " + type + " thành công!\nTài khoản thanh toán: " + acc + "\nSố tiền: " + String.format("%,.2f VND", amount), "Giao dịch thành công", JOptionPane.INFORMATION_MESSAGE);
                txtBillAccount.setText("");
                txtBillAmount.setText("");
                loadHistory();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Thanh toán thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}
