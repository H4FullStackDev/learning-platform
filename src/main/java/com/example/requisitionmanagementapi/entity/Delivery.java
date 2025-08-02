package com.example.requisitionmanagementapi.entity;

import com.example.requisitionmanagementapi.enums.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Delivery {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime deliveryDate;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;
    private String deliveryNote;
    @ManyToOne
    private User deliveredBy;
    @ManyToOne
    private User recipient;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "requisition_id")
    private Requisition requisition;
}
