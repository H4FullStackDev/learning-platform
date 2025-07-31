package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.ArticleDTO;
import com.example.requisitionmanagementapi.dto.SupplierDTO;
import com.example.requisitionmanagementapi.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService service;

    @GetMapping
    public List<SupplierDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SupplierDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public SupplierDTO create(@RequestBody SupplierDTO dto) {
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/articles")
    public List<ArticleDTO> getArticles(@PathVariable Long id) {
        return service.getArticles(id);
    }
}
