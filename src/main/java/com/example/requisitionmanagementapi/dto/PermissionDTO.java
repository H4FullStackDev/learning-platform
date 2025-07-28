package com.example.requisitionmanagementapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {
    private Long id;
    private String name;
    private String label;
    private String category;
}
