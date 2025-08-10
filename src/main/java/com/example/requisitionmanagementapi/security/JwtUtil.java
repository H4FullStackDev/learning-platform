package com.example.requisitionmanagementapi.security;

import com.example.requisitionmanagementapi.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    public String generateJwtToken(UserDetails userDetails) {
        SecurityUserPrincipal principal = (SecurityUserPrincipal) userDetails;
        return Jwts.builder()
                .setSubject(principal.getUsername())         // ou getUsername() si email == username
                .claim("email", principal.getEmail())
                // Ajoute d'autres claims si besoin
                .claim("role", principal.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication toAuthentication(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .setAllowedClockSkewSeconds(30) // optionnel
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String role = claims.get("role", String.class); // ex: "ADMIN", "DIRECTEUR"

            var authorities = (role == null || role.isBlank())
                    ? java.util.List.<org.springframework.security.core.GrantedAuthority>of()
                    : java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));

            return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    username, null, authorities
            );
        } catch (io.jsonwebtoken.JwtException e) {
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid JWT", e);
        }
    }

}
