package com.example.requisitionmanagementapi.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupplierDTO {
    private Long id;
    private String name;
    private String contact;
    private String email;
    private List<ArticleDTO>  articles = new ArrayList<>();
}
