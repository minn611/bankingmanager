-- 🏦 SCRIPT KHỞI TẠO CƠ SỞ DỮ LIỆU BANKMANAGER PRO
CREATE DATABASE IF NOT EXISTS bankmanager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bankmanager;

-- 1. Bảng nhân viên (employees)
CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- BCrypt Hash
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'TELLER', -- ADMIN / TELLER
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / LOCKED
    login_attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Bảng khách hàng (customers)
CREATE TABLE IF NOT EXISTS customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cccd VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100),
    dob DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. Bảng tài khoản (accounts)
CREATE TABLE IF NOT EXISTS accounts (
    account_number VARCHAR(20) PRIMARY KEY,
    customer_id INT NOT NULL,
    account_type VARCHAR(20) NOT NULL DEFAULT 'CHECKING', -- CHECKING / SAVINGS
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / LOCKED / CLOSED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 4. Bảng lịch sử giao dịch (transactions)
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    from_account VARCHAR(20),
    to_account VARCHAR(20),
    transaction_type VARCHAR(20) NOT NULL, -- DEPOSIT / WITHDRAW / TRANSFER / BILL_PAYMENT
    amount DECIMAL(15, 2) NOT NULL,
    fee DECIMAL(10, 2) DEFAULT 0.00,
    description TEXT,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_account) REFERENCES accounts(account_number) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (to_account) REFERENCES accounts(account_number) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 5. Bảng tiết kiệm kỳ hạn (savings_deposits)
CREATE TABLE IF NOT EXISTS savings_deposits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    term_months INT NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    principal_amount DECIMAL(15, 2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / CLOSED (đã tất toán)
    FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 6. Bảng khoản vay (loans)
CREATE TABLE IF NOT EXISTS loans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    loan_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    term_months INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING / APPROVED / REJECTED / ACTIVE / PAID
    loan_date DATE,
    approved_by INT,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 7. Bảng lịch sử trả nợ (loan_payments)
CREATE TABLE IF NOT EXISTS loan_payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    amount_paid DECIMAL(15, 2) NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 8. Bảng thẻ ngân hàng (cards)
CREATE TABLE IF NOT EXISTS cards (
    card_number VARCHAR(20) PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    card_type VARCHAR(20) NOT NULL DEFAULT 'DEBIT', -- DEBIT / CREDIT
    pin_hash VARCHAR(255) NOT NULL,
    credit_limit DECIMAL(15, 2) DEFAULT 0.00,
    balance_due DECIMAL(15, 2) DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / LOCKED
    expiry_date DATE NOT NULL,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 9. Bảng ghi log hoạt động (system_logs)
CREATE TABLE IF NOT EXISTS system_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    action VARCHAR(100) NOT NULL,
    detail TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 10. Bảng cấu hình lãi suất (interest_rates)
CREATE TABLE IF NOT EXISTS interest_rates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rate_type VARCHAR(20) NOT NULL, -- SAVINGS / LOAN
    term_months INT NOT NULL,
    value DECIMAL(5, 2) NOT NULL, -- Lãi suất % năm
    effective_date DATE NOT NULL
) ENGINE=InnoDB;


-- 🔐 CHÈN DỮ LIỆU CẤU HÌNH & TÀI KHOẢN QUẢN TRỊ VIÊN BAN ĐẦU (INITIAL SEED DATA)

-- Mật khẩu mặc định cho admin là 'admin123' (đã mã hóa BCrypt chuẩn)
-- Hash BCrypt: $2a$10$B4K0tl4KWQSodlNqcVs2h.24XkrNHZncWS7sv5ue/whYD3RRT9qse

INSERT INTO employees (username, password, full_name, role, status) VALUES 
('admin', '$2a$10$B4K0tl4KWQSodlNqcVs2h.24XkrNHZncWS7sv5ue/whYD3RRT9qse', 'Ngô Mạnh Hiếu', 'ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE username=username;

-- Thêm cấu hình lãi suất mặc định
INSERT INTO interest_rates (rate_type, term_months, value, effective_date) VALUES 
('SAVINGS', 1, 3.00, '2026-01-01'),
('SAVINGS', 3, 3.50, '2026-01-01'),
('SAVINGS', 6, 4.50, '2026-01-01'),
('SAVINGS', 12, 5.50, '2026-01-01'),
('SAVINGS', 24, 6.00, '2026-01-01'),
('LOAN', 6, 7.50, '2026-01-01'),
('LOAN', 12, 8.50, '2026-01-01'),
('LOAN', 24, 9.50, '2026-01-01')
ON DUPLICATE KEY UPDATE value=value;

