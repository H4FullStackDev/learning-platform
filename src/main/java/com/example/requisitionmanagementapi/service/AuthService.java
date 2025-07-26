package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.Utils.MailService;
import com.example.requisitionmanagementapi.dao.PasswordResetTokenDAO;
import com.example.requisitionmanagementapi.dao.RefreshTokenDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.AuthResponse;
import com.example.requisitionmanagementapi.dto.ChangePasswordRequest;
import com.example.requisitionmanagementapi.entity.PasswordResetToken;
import com.example.requisitionmanagementapi.entity.RefreshToken;
import com.example.requisitionmanagementapi.entity.User;
import com.example.requisitionmanagementapi.security.JwtUtil;
import com.example.requisitionmanagementapi.security.SecurityUserPrincipal;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDAO userDao;
    private final RefreshTokenDAO refreshTokenDao;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenDAO passwordResetTokenDao;
    private final MailService mailService;

    @Transactional
    public AuthResponse login(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateJwtToken(new SecurityUserPrincipal(user));
        RefreshToken refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .mustChangePassword(user.isMustChangePassword())
                .build();
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .user(user)
                .build();
        return refreshTokenDao.save(token);
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenDao.findByToken(refreshTokenStr)
                .filter(token -> !token.isRevoked() && token.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new RuntimeException("Invalid or expired refresh token"));

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateJwtToken(new SecurityUserPrincipal(user));
        // (Optionnel) Tu peux aussi régénérer un nouveau refresh token ici

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .mustChangePassword(user.isMustChangePassword())
                .build();
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        RefreshToken token = refreshTokenDao.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        token.setRevoked(true);
        refreshTokenDao.save(token);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // Récupérer l'utilisateur connecté via le contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Changer le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false); // Important : désactive le flag "changement obligatoire"
        userDao.save(user);
    }

    public void initiatePasswordReset(String email) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Générer un token unique
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .expiryDate(expiry)
                .user(user)
                .used(false)
                .build();
        passwordResetTokenDao.save(resetToken);
        // Envoi du mail
        mailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenDao.findByToken(token)
                .filter(rt -> !rt.isUsed() && rt.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false); // On considère qu'il a un mot de passe valide
        userDao.save(user);

        // Invalide le token pour qu'il ne soit utilisable qu'une seule fois
        resetToken.setUsed(true);
        passwordResetTokenDao.save(resetToken);
    }





}

