package com.example.requisitionmanagementapi.entity;
import com.example.requisitionmanagementapi.enums.RequisitionType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Article {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    private RequisitionType type;
    private int stockQuantity;
    private int stockMin;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
