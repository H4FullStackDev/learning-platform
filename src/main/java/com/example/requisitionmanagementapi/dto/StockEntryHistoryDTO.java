package com.example.requisitionmanagementapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockEntryHistoryDTO {
    private Long id;
    private String articleName;
    private int quantity;
    private double totalAmount;
    private LocalDateTime entryDate;
    private String enteredBy;
}
