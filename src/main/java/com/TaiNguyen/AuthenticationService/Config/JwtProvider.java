package com.TaiNguyen.AuthenticationService.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtProvider {
    private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(Authentication auth) {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000))
                .claim("email", auth.getName())
                .signWith(KEY)
                .compact();
    }

    public static String getEmailfromToken(String jwt) {
        if(jwt == null || jwt.length() <= 7 || !jwt.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }

        String token = jwt.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return String.valueOf(claims.get("email"));
    }

    public static SecretKey getKey(){
        return KEY;
    }
}
