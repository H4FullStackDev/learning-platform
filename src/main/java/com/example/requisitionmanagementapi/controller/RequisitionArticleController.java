package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.RequisitionArticleDTO;
import com.example.requisitionmanagementapi.service.RequisitionArticleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/requisition-articles")
public class RequisitionArticleController {

    private final RequisitionArticleService service;

    @GetMapping
    public List<RequisitionArticleDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public RequisitionArticleDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public RequisitionArticleDTO create(@RequestBody RequisitionArticleDTO dto) {
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
