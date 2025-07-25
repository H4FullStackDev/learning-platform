package com.example.requisitionmanagementapi.dto;

import lombok.*;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
}