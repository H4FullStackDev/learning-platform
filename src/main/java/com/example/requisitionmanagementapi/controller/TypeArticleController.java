package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.TypeArticleDTO;
import com.example.requisitionmanagementapi.service.TypeArticleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/type-articles")
public class TypeArticleController {

    private final TypeArticleService service;

    @GetMapping
    public List<TypeArticleDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TypeArticleDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public TypeArticleDTO create(@RequestBody TypeArticleDTO dto) {
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}