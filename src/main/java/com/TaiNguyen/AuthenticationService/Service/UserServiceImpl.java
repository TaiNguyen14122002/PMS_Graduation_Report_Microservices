package com.TaiNguyen.AuthenticationService.Service;

import com.TaiNguyen.AuthenticationService.Config.JwtProvider;
import com.TaiNguyen.AuthenticationService.Modal.UserModal;
import com.TaiNguyen.AuthenticationService.Utill.EmailUtill;
import com.TaiNguyen.AuthenticationService.Utill.OTPService;
import com.TaiNguyen.AuthenticationService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private EmailUtill emailUtill;

    // Sử dụng một Map để lưu token tạm thời
    private Map<String, String> resetPasswordToken = new HashMap<>();
    private Map<String, LocalDateTime> tokenExpiryDate = new HashMap<>();


    @Override
    public UserModal findUserByEmail(String email) throws Exception {
        UserModal user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("Không tìm thấy tài khoản");
        }
        return user;
    }

    @Override
    public String forgotPassword(String email) throws Exception {
        UserModal user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("Không tìm thấy người dùng");
        }
        otpService.generateAndSendOtp(email);
        return "Mã OTP đã được gửi đến email của bạn";
    }



//    @Override
//    public boolean resetPassword(String email, String newPassword, String otp) throws Exception {
//        if(!otpService.verifyOtp(email, otp)){
//            throw new Exception("Mã OTP không hợp lệ");
//        }
//        UserModal user = userRepository.findByEmail(email);
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//        return true;
//    }

    @Override
    public boolean resetPassword(String token, String newPassword) throws Exception {

        //xác thực token
        String email = validateResetPasswordToken(token);
        if(email == null){
            throw new Exception("Token không hợp lệ hoặc đã hết hạn");
        }

        //Cập nhập mật khẩu mới
        UserModal user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("Tài khoản không tồn tại");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        //Sau khi đặt lại thành công thì xoá token
        resetPasswordToken.remove(token);
        tokenExpiryDate.remove(token);
        return true;
    }

    @Override
    public void saveResetPasswordToken(String email, String token) throws Exception {
        // Lưu token cùng với email và thời gian hết hạn (ví dụ: 5 Phút)
        resetPasswordToken.put(token, email);
        tokenExpiryDate.put(token, LocalDateTime.now().plusMinutes(5));
    }

    @Override
    public String validateResetPasswordToken(String token) throws Exception {
        String email = resetPasswordToken.get(token);
        LocalDateTime expiryDate = tokenExpiryDate.get(token);
        if(email == null || expiryDate == null || expiryDate.isBefore(LocalDateTime.now())){
            return null; // Token không hợp lệ hoặc đã hết hạn
        }
        // Trả về email nếu token hợp lệ
        return email;
    }
}
