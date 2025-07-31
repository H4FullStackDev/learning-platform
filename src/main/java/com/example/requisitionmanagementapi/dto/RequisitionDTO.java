package com.example.requisitionmanagementapi.dto;

import com.example.requisitionmanagementapi.entity.User;
import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import com.example.requisitionmanagementapi.enums.RequisitionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequisitionDTO {
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private RequisitionType type;
    private LocalDateTime createdAt;
    private RequisitionStatus status;
    private String comment;

    private UserDTO createdBy;
    private UserDTO validatedBy;

    private LocalDateTime validationDate;

    private List<RequisitionArticleDTO> articles;
    private List<RequisitionHistoryDTO> histories;
    private List<DeliveryDTO> deliveries;
}
