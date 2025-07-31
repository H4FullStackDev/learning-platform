package com.example.requisitionmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class RequisitionArticle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "requisition_id")
    private Requisition requisition;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;
}

