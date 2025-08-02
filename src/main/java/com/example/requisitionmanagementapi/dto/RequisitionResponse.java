package com.example.requisitionmanagementapi.dto;

import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import com.example.requisitionmanagementapi.enums.RequisitionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class RequisitionResponse {
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private RequisitionType type;
    private UserDTO validatedBy;
    private LocalDateTime validationDate;
}
