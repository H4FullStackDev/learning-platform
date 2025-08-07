package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.DepartmentDTO;
import com.example.requisitionmanagementapi.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@PreAuthorize("hasRole('SUPERADMIN')")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;

    @GetMapping
    public List<DepartmentDTO> getAll() {
        return departmentService.getAll();
    }

    @GetMapping("/{id}")
    public DepartmentDTO getById(@PathVariable Long id) {
        return departmentService.getById(id);
    }

    @PostMapping
    public DepartmentDTO create(@RequestBody DepartmentDTO dto) {
        return departmentService.save(dto);
    }

    @PutMapping("/{id}")
    public DepartmentDTO update(@PathVariable Long id, @RequestBody DepartmentDTO dto) {
        dto.setId(id);
        return departmentService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        departmentService.delete(id);
    }
}
