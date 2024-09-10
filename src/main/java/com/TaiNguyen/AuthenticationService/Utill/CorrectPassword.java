package com.TaiNguyen.AuthenticationService.Utill;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CorrectPassword {
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean verityPassword(String password, String correctPassword) {
        return passwordEncoder.matches(password, correctPassword);
    }
}
