package com.example.spring4shell.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class CustomJwtService {

    @Value("${app.jwt.access-secret}")
    private String accessSecret;
    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getAccessSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String extractUsernameFromAccessToken(String token) {
        return extractAccessClaims(token).getSubject();
    }

    public boolean isAccessTokenValid(String token, String username) {
        Claims claims = extractAccessClaims(token);
        return claims.getSubject().equals(username) && !isTokenExpired(claims);
    }

    private Claims extractAccessClaims(String token) {
        return Jwts.parser()
                .verifyWith(getAccessSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private SecretKey getAccessSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(accessSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}