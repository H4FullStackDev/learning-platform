package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.RequisitionHistoryDTO;
import com.example.requisitionmanagementapi.service.RequisitionHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/requisition-histories")
public class RequisitionHistoryController {

    private final RequisitionHistoryService service;

    @GetMapping
    public List<RequisitionHistoryDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public RequisitionHistoryDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public RequisitionHistoryDTO create(@RequestBody RequisitionHistoryDTO dto) {
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
