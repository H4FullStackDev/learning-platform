package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.*;
import com.example.requisitionmanagementapi.entity.User;
import com.example.requisitionmanagementapi.service.AuthService;
import com.example.requisitionmanagementapi.service.UserService;
import lombok.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }


}
