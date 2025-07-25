package com.example.requisitionmanagementapi.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryDTO {
    private Long id;
    private LocalDateTime deliveryDate;
    private String deliveryStatus;
    private String description;
    private RequisitionDTO requisition;
}
