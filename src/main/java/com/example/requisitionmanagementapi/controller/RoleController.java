package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dao.PermissionDAO;
import com.example.requisitionmanagementapi.dao.RoleDAO;
import com.example.requisitionmanagementapi.dto.PermissionDTO;
import com.example.requisitionmanagementapi.dto.PermissionIds;
import com.example.requisitionmanagementapi.dto.RoleDTO;
import com.example.requisitionmanagementapi.dto.RoleRequest;
import com.example.requisitionmanagementapi.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@PreAuthorize("hasRole('SUPERADMIN')")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleDAO roleDAO;
    private final PermissionDAO permissionDAO;

    @GetMapping
    public List<RoleDTO> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    public RoleDTO getById(@PathVariable Long id) {
        return roleService.getById(id);
    }

    @PostMapping
    public RoleDTO create(@RequestBody RoleDTO request) {
        return roleService.createRole(request);
    }

    @PutMapping("/{id}")
    public RoleDTO update(@PathVariable Long id, @RequestBody RoleRequest request) {
        return roleService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    @GetMapping("/{id}/permissions")
    public List<PermissionDTO> getPermissions(@PathVariable Long id) {
        return  roleService.getPermissionsByRoleId(id);
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<RoleDTO> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody PermissionIds request) {
        RoleDTO updatedRole = roleService.assignPermissions(roleId, request.getPermissionIds());
        return ResponseEntity.ok(updatedRole);
    }

}

