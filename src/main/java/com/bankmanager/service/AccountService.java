package com.bankmanager.service;

import com.bankmanager.dao.AccountDAO;
import com.bankmanager.model.Account;
import com.bankmanager.util.ValidationUtil;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

public class AccountService {
    private final AccountDAO accountDAO = new AccountDAO();
    private final SecureRandom random = new SecureRandom();

    /**
     * Mở tài khoản ngân hàng mới cho khách hàng.
     * @param customAccountNumber null = tự động sinh, khác null = dùng số do người dùng nhập
     */
    public Account openAccount(int customerId, String accountType, BigDecimal initialBalance, String customAccountNumber) throws Exception {
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Số dư ban đầu không được âm!");
        }

        String accountNumber;
        if (customAccountNumber != null && !customAccountNumber.isBlank()) {
            // Kiểm tra số TK do người dùng nhập có bị trùng không
            if (accountDAO.findByAccountNumber(customAccountNumber) != null) {
                throw new Exception("Số tài khoản \"" + customAccountNumber + "\" đã tồn tại trên hệ thống! Vui lòng chọn số khác.");
            }
            accountNumber = customAccountNumber;
        } else {
            // Tự động sinh số tài khoản duy nhất
            accountNumber = generateUniqueAccountNumber(accountType);
        }

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setCustomerId(customerId);
        account.setAccountType(accountType);
        account.setBalance(initialBalance);
        account.setStatus("ACTIVE");

        boolean success = accountDAO.insert(account);
        if (success) {
            Integer empId = AuthService.getCurrentLoggedInEmployee() != null ? AuthService.getCurrentLoggedInEmployee().getId() : null;
            LogService.log(empId, "OPEN_ACCOUNT", "Mở tài khoản thành công: " + accountNumber + " cho KH ID: " + customerId);
            return account;
        } else {
            throw new Exception("Không thể mở tài khoản mới. Vui lòng kiểm tra lại kết nối CSDL!");
        }
    }

    /**
     * Overload giữ lại tương thích ngược (tự sinh số TK).
     */
    public Account openAccount(int customerId, String accountType, BigDecimal initialBalance) throws Exception {
        return openAccount(customerId, accountType, initialBalance, null);
    }

    /**
     * Đổi trạng thái tài khoản ngân hàng (ACTIVE, LOCKED, CLOSED).
     */
    public boolean changeAccountStatus(String accountNumber, String newStatus) throws Exception {
        Account account = accountDAO.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new Exception("Tài khoản không tồn tại trên hệ thống!");
        }

        account.setStatus(newStatus);
        boolean success = accountDAO.update(account);
        if (success) {
            Integer empId = AuthService.getCurrentLoggedInEmployee() != null ? AuthService.getCurrentLoggedInEmployee().getId() : null;
            LogService.log(empId, "CHANGE_ACCOUNT_STATUS", "Đổi trạng thái tài khoản " + accountNumber + " thành " + newStatus);
            return true;
        }
        return false;
    }

    /**
     * Sinh số tài khoản ngân hàng duy nhất.
     * CHECKING bắt đầu bằng 101, SAVINGS bắt đầu bằng 202, cộng thêm 8 số ngẫu nhiên.
     */
    private String generateUniqueAccountNumber(String accountType) {
        String prefix = "SAVINGS".equalsIgnoreCase(accountType) ? "202" : "101";
        String accountNumber;
        do {
            // Sinh ngẫu nhiên 8 số
            int num = random.nextInt(90000000) + 10000000;
            accountNumber = prefix + num;
        } while (accountDAO.findByAccountNumber(accountNumber) != null); // Kiểm tra trùng trong DB

        return accountNumber;
    }

    public Account getAccount(String accountNumber) {
        return accountDAO.findByAccountNumber(accountNumber);
    }

    public List<Account> getAccountsByCustomerId(int customerId) {
        return accountDAO.findByCustomerId(customerId);
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAll();
    }
}
