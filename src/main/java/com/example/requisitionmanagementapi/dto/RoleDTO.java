package com.example.requisitionmanagementapi.dto;

import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private Set<PermissionDTO> permissions;
}
