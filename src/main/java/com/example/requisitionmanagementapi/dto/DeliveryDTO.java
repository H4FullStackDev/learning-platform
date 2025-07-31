package com.example.requisitionmanagementapi.dto;

import com.example.requisitionmanagementapi.enums.DeliveryStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryDTO {
    private Long id;
    private LocalDateTime deliveryDate;
    private DeliveryStatus deliveryStatus;
    private String deliveryNote;

    private UserDTO deliveredBy;
    private UserDTO recipient;
}
