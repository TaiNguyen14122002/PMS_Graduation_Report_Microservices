package com.TaiNguyen.AuthenticationService.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String FullName;
    private String Email;
    private String Token;
    private String Message;
//    private String EmployeeCode;
//    private String Role;
}
