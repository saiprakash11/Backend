package com.ems.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate JWT Token
     */
    public String generateToken(String employeeCode, String role) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(employeeCode)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract all claims
     */
    public Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get Employee Code
     */
    public String getEmployeeCode(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Get Role
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Check Expiration
     */
    public boolean isTokenExpired(String token) {
        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /**
     * Validate Token
     */
    public boolean isValid(String token) {

        try {

            Claims claims = getClaims(token);

            return claims.getSubject() != null
                    && !isTokenExpired(token);

        } catch (ExpiredJwtException e) {
            System.out.println("JWT Expired");
        } catch (JwtException e) {
            System.out.println("Invalid JWT: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT Token is empty");
        }

        return false;
    }
}