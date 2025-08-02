package com.example.requisitionmanagementapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequisitionStatusCountDTO {
    private long draft;
    private long submitted;
    private long validated;
    private long rejected;
    private long inProcess;
    private long delivered;
    private long cancelled;
}

