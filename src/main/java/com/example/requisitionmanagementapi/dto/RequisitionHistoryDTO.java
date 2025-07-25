package com.example.requisitionmanagementapi.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RequisitionHistoryDTO {
    private Long id;
    private String action;
    private LocalDateTime actionDate;
    private String comment;
    private UserDTO user;
}
