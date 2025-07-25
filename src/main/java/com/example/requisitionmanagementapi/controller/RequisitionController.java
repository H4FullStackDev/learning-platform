package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.RequisitionDTO;
import com.example.requisitionmanagementapi.service.RequisitionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requisitions")
public class RequisitionController {
    private final RequisitionService service;

    public RequisitionController(RequisitionService service) {
        this.service = service;
    }

    @GetMapping
    public List<RequisitionDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public RequisitionDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public RequisitionDTO create(@RequestBody RequisitionDTO dto) {
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
