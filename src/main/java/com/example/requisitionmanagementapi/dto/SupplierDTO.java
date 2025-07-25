package com.example.requisitionmanagementapi.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupplierDTO {
    private Long id;
    private String name;
    private String contact;
}
