package com.example.requisitionmanagementapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockEntryHistory {
    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Article article;

    private int quantity;

    private double totalAmount;

    private LocalDateTime entryDate;

    @ManyToOne
    private User enteredBy;

}

