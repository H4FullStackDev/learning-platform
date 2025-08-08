package com.example.requisitionmanagementapi.entity;

import com.example.requisitionmanagementapi.dto.UserDTO;
import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class RequisitionHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RequisitionStatus statusBefore;

    @Enumerated(EnumType.STRING)
    private RequisitionStatus statusAfter;

    private String action;

    private LocalDateTime actionDate;

    private String comment;

    @ManyToOne
    private User actionBy;

    @ManyToOne
    @JoinColumn(name = "requisition_id")
    private Requisition requisition;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
