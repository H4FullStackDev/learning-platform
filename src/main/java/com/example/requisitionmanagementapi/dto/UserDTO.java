package com.example.requisitionmanagementapi.dto;

import com.example.requisitionmanagementapi.entity.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private boolean mustChangePassword;
    private Role role;
    private LocalDateTime createdAt;
}
