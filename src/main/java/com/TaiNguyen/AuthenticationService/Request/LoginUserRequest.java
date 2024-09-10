package com.TaiNguyen.AuthenticationService.Request;

import lombok.Data;

@Data
public class LoginUserRequest {
    private String email;
    private String password;
}
