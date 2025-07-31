package com.example.requisitionmanagementapi.dto;

import com.example.requisitionmanagementapi.enums.RequisitionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDTO {

    private Long id;
    private String name;
    private int stockQuantity;
    private int stockMin;

    private RequisitionType type;
    private SupplierDTO supplier;
}