package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.ArticleDTO;
import com.example.requisitionmanagementapi.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService service;

    @GetMapping
    public List<ArticleDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ArticleDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ArticleDTO create(@RequestBody ArticleDTO dto) {
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

