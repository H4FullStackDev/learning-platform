package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.DeliveryDTO;
import com.example.requisitionmanagementapi.service.DeliveryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService service;

    @GetMapping
    public List<DeliveryDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DeliveryDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public DeliveryDTO create(@RequestBody DeliveryDTO dto) {
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
