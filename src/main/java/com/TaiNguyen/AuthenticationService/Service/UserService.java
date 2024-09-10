package com.TaiNguyen.AuthenticationService.Service;

import com.TaiNguyen.AuthenticationService.Modal.UserModal;

public interface UserService {
    UserModal findUserByEmail(String email) throws Exception;
    String forgotPassword(String email) throws Exception;
//    boolean resetPassword(String email, String newPassword, String otp) throws Exception;

    boolean resetPassword(String token, String newPassword) throws Exception;

    // Lưu token reset mật khẩu
    void saveResetPasswordToken(String email, String token) throws Exception;

    // Kiểm tra token có hợp lệ không và trả về email nếu token hợp lệ
    String validateResetPasswordToken(String token) throws Exception;


}
