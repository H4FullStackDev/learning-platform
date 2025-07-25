package com.example.requisitionmanagementapi.dto;

import lombok.*;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private boolean mustChangePassword;
    private Set<RoleDTO> roles;
}
