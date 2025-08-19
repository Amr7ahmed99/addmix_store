package com.web.service.addmix_store.utils.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.web.service.addmix_store.models.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refreshExpiration}")
    private Long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /* ===================== Extract Claims ===================== */

    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /* ===================== Token Generation ===================== */

    public String generateAccessToken(User user) {
        return buildToken(user.getEmail(), "Addmix Store", accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user.getEmail(), "Addmix Store", refreshTokenExpiration);
    }

    private String buildToken(String subject, String issuer, long validityMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validityMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public ResponseCookie buildCookieForRefreshToken(String refreshToken) {
    return ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)// JS cannot read it (protects from XSS)
            .secure(false) // use false on localhost, true only in prod (HTTPS)
            .sameSite("Strict") // required for cross-site cookies (React 3000 ↔ Spring 8080)
            .path("/api/auth/refresh")
            .maxAge(refreshTokenExpiration)
            .build();
}

    /* ===================== Validation ===================== */

    public boolean validateToken(String token, User user) {
        try {
            String email = extractUserEmail(token);
            return (email.equals(user.getEmail()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            // JwtException covers: SignatureException, MalformedJwtException, ExpiredJwtException
            return false;
        }
    }
}
