package com.example.requisitionmanagementapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDTO {
    private Long id;
    private String name;
    private String description;
    private int stockQuantity;

    private TypeArticleDTO type;      // objet DTO, pas id !
    private SupplierDTO supplier;     // objet DTO, pas id !
}