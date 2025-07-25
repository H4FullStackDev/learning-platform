package com.example.requisitionmanagementapi.dto;

import lombok.*;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}