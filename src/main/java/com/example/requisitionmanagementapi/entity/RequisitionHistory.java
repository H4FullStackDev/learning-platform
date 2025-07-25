package com.example.requisitionmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class RequisitionHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private LocalDateTime actionDate;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "requisition_id")
    private Requisition requisition;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
