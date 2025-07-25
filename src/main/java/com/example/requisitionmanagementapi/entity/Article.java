package com.example.requisitionmanagementapi.entity;
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

    private String name;
    private String description;
    private int stockQuantity;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private TypeArticle type;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
