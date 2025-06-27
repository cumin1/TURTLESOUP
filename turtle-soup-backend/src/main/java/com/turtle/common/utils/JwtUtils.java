package com.turtle.common.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {
    private static final String SECRET_KEY = "turtle_soup";

    public static String generateToken(Long userId, String username) {
        long now = System.currentTimeMillis();
        long expire = now + 1000 * 60 * 60 * 24; // 24小时有效
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("token", username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expire))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
