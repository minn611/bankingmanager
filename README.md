# 🏦 Kế Hoạch Dự Án: Ứng Dụng Quản Lý Ngân Hàng (Desktop Java)

---

## 1. Tổng Quan Dự Án

Ứng dụng **BankManager Pro** là phần mềm quản lý ngân hàng chạy trên máy tính, mô phỏng đầy đủ các nghiệp vụ của một ngân hàng thương mại. Ứng dụng xây dựng theo kiến trúc **MVC (Model - View - Controller)** với giao diện đồ họa **Java Swing**, kết nối cơ sở dữ liệu **MySQL** thông qua **JDBC**.

---

## 2. Công Nghệ Sử Dụng

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ lập trình | Java 17+ |
| IDE | NetBeans 19+ |
| Giao diện (GUI) | Java Swing + FlatLaf (theme hiện đại) |
| Cơ sở dữ liệu | MySQL 8.x |
| Kết nối DB | JDBC (MySQL Connector/J) |
| Kiến trúc | MVC (Model - View - Controller) |
| Bảo mật mật khẩu | BCrypt hashing |
| Báo cáo | JasperReports / iText PDF |
| Build tool | Maven (tích hợp trong NetBeans) |

---

## 3. Các Vai Trò Người Dùng

```
┌─────────────────────────────────────────────┐
│              HỆ THỐNG NGÂN HÀNG             │
├──────────────┬──────────────┬───────────────┤
│   ADMIN      │   NHÂN VIÊN  │   KHÁCH HÀNG  │
│              │   (Teller)   │               │
│ Quản trị hệ  │ Thực hiện    │ Xem tài khoản │
│ thống toàn   │ giao dịch,   │ & lịch sử     │
│ quyền        │ mở TK        │ giao dịch     │
└──────────────┴──────────────┴───────────────┘
```

---

## 4. Chức Năng Chi Tiết

### 4.1 Module Xác Thực & Bảo Mật
- Đăng nhập / Đăng xuất theo vai trò
- Mã hóa mật khẩu (BCrypt)
- Phân quyền truy cập theo role (Admin / Teller / Customer)
- Tự động khóa sau N lần nhập sai
- Ghi log mọi hành động đăng nhập

### 4.2 Module Quản Lý Khách Hàng
- Thêm / Sửa / Xóa / Tìm kiếm thông tin khách hàng
- Hồ sơ khách hàng: CCCD, địa chỉ, SĐT, email, ngày sinh
- Lịch sử giao dịch của từng khách hàng
- Xem tổng quan tài sản của khách hàng

### 4.3 Module Quản Lý Tài Khoản
- Mở tài khoản thanh toán (checking account)
- Mở tài khoản tiết kiệm (savings account)
- Đóng / Tạm khóa / Mở khóa tài khoản
- Xem số dư, thông tin tài khoản
- Tạo số tài khoản tự động theo quy tắc ngân hàng

### 4.4 Module Giao Dịch
- **Nạp tiền (Deposit):** Nạp tiền mặt vào tài khoản
- **Rút tiền (Withdraw):** Rút tiền mặt từ tài khoản
- **Chuyển khoản nội bộ:** Chuyển giữa các tài khoản trong hệ thống
- **Chuyển khoản liên ngân hàng:** Mô phỏng chuyển khoản ra ngoài
- **Thanh toán hóa đơn:** Điện, nước, internet (mô phỏng)
- Kiểm tra số dư trước khi giao dịch
- Mã xác nhận giao dịch (OTP mô phỏng)

### 4.5 Module Tiết Kiệm & Vay
- Gửi tiết kiệm có kỳ hạn (3, 6, 12, 24 tháng)
- Tính lãi suất tiết kiệm tự động
- Đăng ký khoản vay (loan request)
- Phê duyệt / Từ chối vay (Admin)
- Lịch trả nợ, theo dõi dư nợ
- Tính lãi suất vay tự động

### 4.6 Module Thẻ Ngân Hàng
- Phát hành thẻ ATM / thẻ tín dụng (mô phỏng)
- Khóa / Mở khóa thẻ
- Đổi PIN thẻ
- Quản lý hạn mức thẻ tín dụng

### 4.7 Module Báo Cáo & Thống Kê
- Bảng kê giao dịch theo ngày / tháng / năm
- Thống kê doanh số giao dịch
- Báo cáo số dư tài khoản
- Biểu đồ thu chi (sử dụng JFreeChart)
- Xuất báo cáo ra file **PDF / Excel**
- In sao kê tài khoản

### 4.8 Module Quản Trị Hệ Thống (Admin)
- Quản lý danh sách nhân viên
- Phân quyền người dùng
- Xem toàn bộ log hoạt động
- Cấu hình lãi suất, hạn mức
- Backup / Restore database
- Dashboard tổng quan hệ thống

---

## 5. Kiến Trúc Dự Án (Cấu Trúc Thư Mục)

```
BankManagerPro/
├── src/
│   └── main/java/com/bankmanager/
│       ├── model/              # Lớp dữ liệu (POJO)
│       │   ├── Customer.java
│       │   ├── Account.java
│       │   ├── Transaction.java
│       │   ├── Employee.java
│       │   ├── Loan.java
│       │   └── SavingsDeposit.java
│       │
│       ├── dao/                # Truy vấn Database (DAO Pattern)
│       │   ├── CustomerDAO.java
│       │   ├── AccountDAO.java
│       │   ├── TransactionDAO.java
│       │   └── ...
│       │
│       ├── service/            # Xử lý nghiệp vụ (Business Logic)
│       │   ├── AuthService.java
│       │   ├── AccountService.java
│       │   ├── TransactionService.java
│       │   └── ReportService.java
│       │
│       ├── view/               # Giao diện Swing (JFrame, JPanel)
│       │   ├── LoginFrame.java
│       │   ├── MainDashboard.java
│       │   ├── CustomerPanel.java
│       │   ├── TransactionPanel.java
│       │   └── ...
│       │
│       ├── controller/         # Điều phối View ↔ Service
│       │   ├── LoginController.java
│       │   ├── AccountController.java
│       │   └── ...
│       │
│       ├── util/               # Tiện ích dùng chung
│       │   ├── DBConnection.java
│       │   ├── PasswordUtil.java
│       │   ├── DateUtil.java
│       │   └── ValidationUtil.java
│       │
│       └── Main.java           # Điểm khởi chạy ứng dụng
│
├── resources/
│   ├── icons/                  # Biểu tượng giao diện
│   ├── reports/                # Template JasperReports
│   └── db/
│       └── schema.sql          # Script tạo database
│
└── pom.xml                     # Maven dependencies
```

---

## 6. Thiết Kế Cơ Sở Dữ Liệu (MySQL)

### Các bảng chính:

| Bảng | Mô tả |
|---|---|
| `employees` | Thông tin nhân viên, tài khoản đăng nhập |
| `customers` | Thông tin khách hàng |
| `accounts` | Tài khoản ngân hàng của khách hàng |
| `transactions` | Lịch sử tất cả giao dịch |
| `savings_deposits` | Các khoản tiết kiệm có kỳ hạn |
| `loans` | Thông tin khoản vay |
| `loan_payments` | Lịch sử trả nợ |
| `cards` | Thẻ ngân hàng |
| `system_logs` | Log hoạt động hệ thống |
| `interest_rates` | Cấu hình lãi suất |

---

## 7. Giao Diện Người Dùng (UI/UX)

### Màn hình chính:
- **Login Screen:** Form đăng nhập với logo ngân hàng
- **Dashboard:** Tổng quan số liệu nhanh (tổng TK, giao dịch hôm nay, v.v.)
- **Sidebar Navigation:** Menu điều hướng bên trái theo từng module
- **Content Area:** Nội dung thay đổi theo menu được chọn

### Theme giao diện:
- Sử dụng **FlatLaf** library để có giao diện hiện đại, phẳng
- Bảng màu chủ đạo: **Xanh navy + Trắng** (phong cách ngân hàng chuyên nghiệp)
- Responsive cho các kích thước màn hình khác nhau

---

## 8. Kế Hoạch Phát Triển (Timeline)

### Giai đoạn 1 — Nền tảng (Tuần 1–2)
- [ ] Thiết kế database, viết script SQL
- [ ] Cấu hình Maven, thêm dependencies
- [ ] Xây dựng DBConnection, các lớp Model
- [ ] Implement DAO layer cơ bản
- [ ] Màn hình Login + xác thực

### Giai đoạn 2 — Nghiệp vụ cốt lõi (Tuần 3–5)
- [ ] Module Quản lý Khách hàng (CRUD)
- [ ] Module Quản lý Tài khoản
- [ ] Module Giao dịch (nạp, rút, chuyển khoản)
- [ ] Phân quyền người dùng

### Giai đoạn 3 — Tính năng nâng cao (Tuần 6–8)
- [ ] Module Tiết kiệm & Vay
- [ ] Module Thẻ ngân hàng
- [ ] Báo cáo & xuất PDF/Excel
- [ ] Biểu đồ thống kê (JFreeChart)

### Giai đoạn 4 — Hoàn thiện (Tuần 9–10)
- [ ] Module quản trị hệ thống
- [ ] Ghi log toàn diện
- [ ] Kiểm thử (Unit Test với JUnit)
- [ ] Đóng gói thành file `.jar` / installer
- [ ] Viết tài liệu hướng dẫn sử dụng

---

