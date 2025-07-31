package com.example.requisitionmanagementapi.dto;

import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequisitionHistoryDTO {
    private Long id;
    private RequisitionStatus statusBefore;
    private RequisitionStatus statusAfter;
    private LocalDateTime actionDate;
    private String comment;
    private UserDTO user;
    private RequisitionDTO requisition;
}
