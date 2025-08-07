package com.example.requisitionmanagementapi.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequisitionStockRecapDTO {
        private Long articleId;
        private String articleName;
        private int quantityDemanded;
        private int currentStock;
        private int remainingStock;
}
