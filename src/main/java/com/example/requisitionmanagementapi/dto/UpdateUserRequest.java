package com.example.requisitionmanagementapi.dto;

import com.example.requisitionmanagementapi.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
