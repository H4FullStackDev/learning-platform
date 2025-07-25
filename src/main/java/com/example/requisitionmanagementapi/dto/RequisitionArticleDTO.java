package com.example.requisitionmanagementapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitionArticleDTO {
    private Long id;
    private int requestedQuantity;
    private int deliveredQuantity;
    private ArticleDTO article;
}
