package com.bankmanager.service;

import com.bankmanager.dao.AccountDAO;
import com.bankmanager.dao.TransactionDAO;
import com.bankmanager.model.Account;
import com.bankmanager.model.Transaction;
import com.bankmanager.util.DBConnection;
import com.bankmanager.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    /**
     * Nạp tiền mặt vào tài khoản ngân hàng.
     */
    public boolean deposit(String accountNumber, BigDecimal amount, String description) throws Exception {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new Exception("Số tiền nạp vào phải lớn hơn 0!");
        }

        Account account = accountDAO.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new Exception("Tài khoản nhận không tồn tại!");
        }
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new Exception("Tài khoản nhận đang bị khóa hoặc đã đóng!");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Cập nhật số dư tài khoản
            accountDAO.updateBalance(accountNumber, amount, conn);

            // 2. Ghi lịch sử giao dịch
            Transaction tx = new Transaction();
            tx.setFromAccount(null); // Giao dịch nạp tiền mặt tại quầy
            tx.setToAccount(accountNumber);
            tx.setTransactionType("DEPOSIT");
            tx.setAmount(amount);
            tx.setFee(BigDecimal.ZERO);
            tx.setDescription(description != null ? description : "Nộp tiền mặt vào tài khoản");

            transactionDAO.insert(tx, conn);

            conn.commit(); // Ghi nhận Transaction thành công
            
            Integer empId = AuthService.getCurrentLoggedInEmployee() != null ? AuthService.getCurrentLoggedInEmployee().getId() : null;
            LogService.log(empId, "DEPOSIT", "Nạp " + amount + " VND vào TK: " + accountNumber);
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi xảy ra
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Giao dịch thất bại: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Rút tiền mặt từ tài khoản ngân hàng.
     */
    public boolean withdraw(String accountNumber, BigDecimal amount, String description) throws Exception {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new Exception("Số tiền rút phải lớn hơn 0!");
        }

        Account account = accountDAO.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new Exception("Tài khoản không tồn tại!");
        }
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new Exception("Tài khoản đang bị khóa hoặc đã đóng!");
        }

        // Kiểm tra số dư
        BigDecimal fee = new BigDecimal("1100"); // Phí rút tiền 1,100 VND mặc định
        BigDecimal totalRequired = amount.add(fee);
        if (account.getBalance().compareTo(totalRequired) < 0) {
            throw new Exception("Số dư tài khoản không đủ để thực hiện giao dịch (Số tiền rút + phí 1,100đ = " + totalRequired + "đ)!");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Trừ số dư tài khoản
            accountDAO.updateBalance(accountNumber, totalRequired.negate(), conn);

            // 2. Ghi lịch sử giao dịch
            Transaction tx = new Transaction();
            tx.setFromAccount(accountNumber);
            tx.setToAccount(null); // Rút tiền mặt
            tx.setTransactionType("WITHDRAW");
            tx.setAmount(amount);
            tx.setFee(fee);
            tx.setDescription(description != null ? description : "Rút tiền mặt tại quầy/ATM");

            transactionDAO.insert(tx, conn);

            conn.commit(); // Ghi nhận Transaction thành công
            
            Integer empId = AuthService.getCurrentLoggedInEmployee() != null ? AuthService.getCurrentLoggedInEmployee().getId() : null;
            LogService.log(empId, "WITHDRAW", "Rút " + amount + " VND (phí " + fee + ") từ TK: " + accountNumber);
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Giao dịch thất bại: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Chuyển khoản nội bộ giữa hai tài khoản trong hệ thống.
     */
    public boolean transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount, String description) throws Exception {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new Exception("Tài khoản gửi và nhận không được trùng nhau!");
        }
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new Exception("Số tiền chuyển phải lớn hơn 0!");
        }

        Account fromAcc = accountDAO.findByAccountNumber(fromAccountNumber);
        Account toAcc = accountDAO.findByAccountNumber(toAccountNumber);

        if (fromAcc == null) {
            throw new Exception("Tài khoản chuyển không tồn tại!");
        }
        if (!"ACTIVE".equalsIgnoreCase(fromAcc.getStatus())) {
            throw new Exception("Tài khoản chuyển đang bị khóa hoặc đã đóng!");
        }

        if (toAcc == null) {
            throw new Exception("Tài khoản nhận không tồn tại trên hệ thống!");
        }
        if (!"ACTIVE".equalsIgnoreCase(toAcc.getStatus())) {
            throw new Exception("Tài khoản nhận đang bị khóa hoặc đã đóng!");
        }

        // Kiểm tra số dư tài khoản gửi
        if (fromAcc.getBalance().compareTo(amount) < 0) {
            throw new Exception("Số dư tài khoản chuyển không đủ để chuyển khoản " + amount + " VND!");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Trừ tiền tài khoản chuyển
            accountDAO.updateBalance(fromAccountNumber, amount.negate(), conn);

            // 2. Cộng tiền tài khoản nhận
            accountDAO.updateBalance(toAccountNumber, amount, conn);

            // 3. Ghi lịch sử giao dịch
            Transaction tx = new Transaction();
            tx.setFromAccount(fromAccountNumber);
            tx.setToAccount(toAccountNumber);
            tx.setTransactionType("TRANSFER");
            tx.setAmount(amount);
            tx.setFee(BigDecimal.ZERO); // Chuyển khoản nội bộ miễn phí
            tx.setDescription(description != null ? description : "Chuyển khoản nội bộ");

            transactionDAO.insert(tx, conn);

            conn.commit(); // Thành công thì commit
            
            Integer empId = AuthService.getCurrentLoggedInEmployee() != null ? AuthService.getCurrentLoggedInEmployee().getId() : null;
            LogService.log(empId, "TRANSFER", "Chuyển " + amount + " VND từ TK " + fromAccountNumber + " sang TK " + toAccountNumber);
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Chuyển khoản thất bại: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Thanh toán hóa đơn (Điện, nước, internet...).
     */
    public boolean payBill(String accountNumber, String serviceType, BigDecimal amount, String providerName) throws Exception {
        if (!ValidationUtil.isValidAmount(amount)) {
            throw new Exception("Số tiền thanh toán phải lớn hơn 0!");
        }

        Account account = accountDAO.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new Exception("Tài khoản thanh toán không tồn tại!");
        }
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new Exception("Tài khoản đang bị khóa hoặc đã đóng!");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new Exception("Số dư không đủ để thanh toán hóa đơn " + amount + " VND!");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Trừ số dư tài khoản
            accountDAO.updateBalance(accountNumber, amount.negate(), conn);

            // 2. Ghi lịch sử giao dịch (toAccount = null vì không phải tài khoản nội bộ)
            Transaction tx = new Transaction();
            tx.setFromAccount(accountNumber);
            tx.setToAccount(null); // Thanh toán ra ngoài, không có tài khoản đích nội bộ
            tx.setTransactionType("BILL_PAYMENT");
            tx.setAmount(amount);
            tx.setFee(BigDecimal.ZERO);
            tx.setDescription("Thanh toán " + serviceType + " – " + providerName + " | Số tiền: " + String.format("%,.0f", amount) + " VND");

            transactionDAO.insert(tx, conn);

            conn.commit(); // Ghi nhận Transaction thành công
            
            Integer empId = AuthService.getCurrentLoggedInEmployee() != null ? AuthService.getCurrentLoggedInEmployee().getId() : null;
            LogService.log(empId, "BILL_PAYMENT", "Thanh toán hóa đơn " + serviceType + " giá " + amount + " VND từ TK: " + accountNumber);
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw new Exception("Thanh toán thất bại: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionDAO.findByAccountNumber(accountNumber);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAll();
    }
}
