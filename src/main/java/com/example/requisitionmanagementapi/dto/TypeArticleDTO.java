package com.example.requisitionmanagementapi.dto;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeArticleDTO {
    private Long id;
    private String label;
    private String description;
}
