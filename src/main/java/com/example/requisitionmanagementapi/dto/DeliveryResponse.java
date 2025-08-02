package com.example.requisitionmanagementapi.dto;

import com.example.requisitionmanagementapi.enums.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DeliveryResponse {
    private Long id;
    private LocalDateTime deliveryDate;
    private DeliveryStatus deliveryStatus;
    private String deliveryNote;
    private UserDTO deliveredBy;
    private UserDTO recipient;
    private RequisitionResponse requisition;
}
