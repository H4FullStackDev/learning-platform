package com.example.requisitionmanagementapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DepartmentDTO {
    private Long id;
    private String name;
}
