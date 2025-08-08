package com.example.requisitionmanagementapi.entity;

import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import com.example.requisitionmanagementapi.enums.RequisitionType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Requisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private RequisitionType type;

    @Enumerated(EnumType.STRING)
    private RequisitionStatus status;

    private String comment;

    private LocalDateTime createdAt;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    private User validatedBy;

    private LocalDateTime validationDate;

    @OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequisitionArticle> articles = new ArrayList<>();

    @OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequisitionHistory> histories = new ArrayList<>();

    @OneToOne(mappedBy = "requisition")
    private Delivery delivery;
}


