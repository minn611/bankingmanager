package com.bankmanager.service;

import com.bankmanager.dao.EmployeeDAO;
import com.bankmanager.model.Employee;
import com.bankmanager.util.PasswordUtil;

public class AuthService {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static Employee currentLoggedInEmployee = null;

    /**
     * Thực hiện đăng nhập cho nhân viên.
     * 
     * @param username tên tài khoản
     * @param password mật khẩu
     * @return Employee nếu đăng nhập thành công
     * @throws Exception thông báo lỗi chi tiết (Sai mật khẩu, tài khoản bị khóa...)
     */
    public Employee login(String username, String password) throws Exception {
        Employee emp = employeeDAO.findByUsername(username);
        if (emp == null) {
            throw new Exception("Tên đăng nhập không tồn tại trên hệ thống!");
        }

        if ("LOCKED".equalsIgnoreCase(emp.getStatus())) {
            LogService.log(emp.getId(), "LOGIN_FAIL", "Đăng nhập thất bại: Tài khoản đang bị khóa.");
            throw new Exception("Tài khoản này đã bị khóa do nhập sai mật khẩu quá nhiều lần! Vui lòng liên hệ Admin.");
        }

        if (PasswordUtil.checkPassword(password, emp.getPassword())) {
            // Đăng nhập thành công
            emp.setLoginAttempts(0);
            employeeDAO.update(emp);
            currentLoggedInEmployee = emp;
            
            LogService.log(emp.getId(), "LOGIN_SUCCESS", "Đăng nhập thành công vào hệ thống.");
            return emp;
        } else {
            // Đăng nhập sai
            int attempts = emp.getLoginAttempts() + 1;
            emp.setLoginAttempts(attempts);
            
            if (attempts >= 5) {
                emp.setStatus("LOCKED");
                employeeDAO.update(emp);
                LogService.log(emp.getId(), "ACCOUNT_LOCKED", "Tài khoản bị khóa tự động do đăng nhập sai 5 lần.");
                throw new Exception("Mật khẩu sai! Tài khoản của bạn đã bị KHÓA do nhập sai quá 5 lần.");
            } else {
                employeeDAO.update(emp);
                LogService.log(emp.getId(), "LOGIN_FAIL", "Đăng nhập thất bại: Nhập sai mật khẩu lần thứ " + attempts);
                throw new Exception("Mật khẩu không chính xác! Bạn còn " + (5 - attempts) + " lần thử.");
            }
        }
    }

    /**
     * Thực hiện đăng xuất nhân viên hiện tại.
     */
    public void logout() {
        if (currentLoggedInEmployee != null) {
            LogService.log(currentLoggedInEmployee.getId(), "LOGOUT", "Nhân viên đăng xuất khỏi hệ thống.");
            currentLoggedInEmployee = null;
        }
    }

    public static Employee getCurrentLoggedInEmployee() {
        return currentLoggedInEmployee;
    }
}
