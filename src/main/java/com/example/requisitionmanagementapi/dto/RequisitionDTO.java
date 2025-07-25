package com.example.requisitionmanagementapi.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequisitionDTO {
    private Long id;
    private LocalDateTime creationDate;
    private String status;
    private String comment;
    private UserDTO createdBy;
    private List<RequisitionArticleDTO> articles;
    private List<RequisitionHistoryDTO> history;
    private DeliveryDTO delivery;
}
