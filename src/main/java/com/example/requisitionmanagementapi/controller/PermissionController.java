package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.PermissionDTO;
import com.example.requisitionmanagementapi.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@PreAuthorize("hasRole('SUPERADMIN')")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public List<PermissionDTO> getAll() {
        return permissionService.getAll();
    }

    @GetMapping("/{id}")
    public PermissionDTO getById(@PathVariable Long id) {
        return permissionService.getById(id);
    }

    @PostMapping
    public PermissionDTO create(@RequestBody PermissionDTO dto) {
        return permissionService.save(dto);
    }

    @PutMapping("/{id}")
    public PermissionDTO update(@PathVariable Long id, @RequestBody PermissionDTO dto) {
        dto.setId(id);
        return permissionService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        permissionService.delete(id);
    }
}

