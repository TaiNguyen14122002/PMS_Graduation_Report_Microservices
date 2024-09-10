package com.TaiNguyen.AuthenticationService.Controller;

import com.TaiNguyen.AuthenticationService.Config.JwtProvider;
import com.TaiNguyen.AuthenticationService.Modal.UserModal;
import com.TaiNguyen.AuthenticationService.Request.LoginUserRequest;
import com.TaiNguyen.AuthenticationService.Response.AuthResponse;
import com.TaiNguyen.AuthenticationService.Response.ErrorResponse;
import com.TaiNguyen.AuthenticationService.Response.SuccessResponse;
import com.TaiNguyen.AuthenticationService.Service.CustomerUserDatailsImpl;
import com.TaiNguyen.AuthenticationService.Service.UserService;
import com.TaiNguyen.AuthenticationService.Utill.CorrectPassword;
import com.TaiNguyen.AuthenticationService.Utill.EmailUtill;
import com.TaiNguyen.AuthenticationService.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerUserDatailsImpl customerUserDatails;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailUtill emailUtill;

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customerUserDatails.loadUserByUsername(email);
        if(userDetails == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Mật khẩu bị sai");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // Hàm kiểm tra email hợp lệ
    public boolean isValidEmail(String email) {
        // Quy tắc: chỉ cho phép ký tự chữ cái, số, dấu chấm, dấu gạch dưới và dấu gạch ngang
        String emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    // Hàm kiểm tra mật khẩu hợp lệ
    public boolean isValidPassword(String password) {
        // Quy tắc: ít nhất 8 ký tự, có ít nhất 1 chữ thường, 1 chữ hoa, 1 số, và 1 ký tự đặc biệt
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordRegex);
    }

    @Operation(
            summary = "Sign up a new user",
            description = "This endpoint allows new users to register by providing their email, password, and full name. Passwords are validated for security."
    )
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody UserModal user) {
        if(!isValidEmail(user.getEmail())) {
            ErrorResponse errorResponse = new ErrorResponse("Email không hợp lệ. Vui lòng nhập đúng định dạng email.");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        UserModal isUser = userRepository.findByEmail(user.getEmail());


        if(isUser != null) {
            ErrorResponse errorResponse = new ErrorResponse("Tài khoản đã tồn tại với email: " + user.getEmail());
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        if(!isValidPassword(user.getPassword())) {
            ErrorResponse error = new ErrorResponse("Mật khẩu không hợp lệ. Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ thường, chữ hoa, số và ký tự đặc biệt.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        UserModal CreatedUser = new UserModal();
        CreatedUser.setEmail(user.getEmail());
        CreatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        CreatedUser.setFullname(user.getFullname());

        UserModal savedUser = userRepository.save(CreatedUser);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String token = JwtProvider.generateToken(authentication);

        SuccessResponse res = new SuccessResponse();
        res.setMessage("Đăng ký tài khoản thành công");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(
            summary = "POST Login Operation for Staff Authentication",
            description = "This endpoint allows staff members to log in to the system. Users must provide their email and password. Upon successful authentication, a JWT token will be returned, which can be used for subsequent authenticated requests. If the credentials are invalid, an appropriate error message will be returned."
    )
    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginUserRequest loginUserRequest){
        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();
        UserModal user = userRepository.findByEmail(email);

        if(user == null) {
            ErrorResponse error = new ErrorResponse("Tài khoản không tồn tại");
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        if(!CorrectPassword.verityPassword(password, user.getPassword())) {
            ErrorResponse error = new ErrorResponse("Mật khẩu không chính xác");
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        AuthResponse res = new AuthResponse();
        res.setMessage("Đăng nhập thành công");
        res.setFullName(user.getFullname());
        res.setEmail(user.getEmail());
        res.setToken(token);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(
            summary = "Request password reset",
            description = "This endpoint allows users to request a password reset. A reset link will be sent to the provided email address."
    )
    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            String token = UUID.randomUUID().toString();
            userService.saveResetPasswordToken(email, token);

            String resetLink = "http://localhost:3000/auth/resetPassword?token=" + token;

            String subject = "Yêu cầu đặt lại mật khẩu";

            // Nội dung email với HTML và nút bấm
            String emailContent = "<h3>Đặt lại mật khẩu của bạn</h3>"
                    + "<p>Nhấn vào nút bên dưới để đặt lại mật khẩu của bạn:</p>"
                    + "<a href=\"" + resetLink + "\" style=\"display:inline-block;background-color:#4CAF50;color:white;padding:10px 20px;text-align:center;text-decoration:none;font-size:16px;\">Đặt lại mật khẩu</a>"
                    + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.</p>";

            emailUtill.sendEmail(email, subject, emailContent);
            return new ResponseEntity<>("Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn", HttpStatus.OK);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }


    @Operation(
            summary = "Reset user password",
            description = "This endpoint allows users to reset their password using a valid token and new password."
    )
    @PutMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) throws Exception {
        try{
            String email = userService.validateResetPasswordToken(token);
            if(email == null) {
                return new ResponseEntity<>("Token không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST);
            }

            //Đặt lại mật khẩu cho người dùng
            UserModal user = userRepository.findByEmail(email);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return new ResponseEntity<>("Đặt lại mật khẩu thành công", HttpStatus.OK);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
