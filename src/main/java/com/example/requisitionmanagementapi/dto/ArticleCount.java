package com.example.requisitionmanagementapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCount {
    private long total;
    private long lowStock;
    private long largeStock;
    private long outOfStock;
}

