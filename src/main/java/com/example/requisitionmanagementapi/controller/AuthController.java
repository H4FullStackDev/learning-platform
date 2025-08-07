package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.*;
import com.example.requisitionmanagementapi.service.AuthService;
import com.example.requisitionmanagementapi.service.UserService;
import lombok.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return  authService.login(request.getUsername(), request.getPassword());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/logout")
    public void  logout(@RequestBody RefreshRequest request) {
         authService.logout(request.getRefreshToken());
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.initiatePasswordReset(request.getEmail());
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());

    }

}
