package com.example.requisitionmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Delivery {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime deliveryDate;
    private String deliveryStatus;
    private String description;

    @OneToOne
    @JoinColumn(name = "requisition_id")
    private Requisition requisition;
}
