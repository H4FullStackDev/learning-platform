package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.PermissionDAO;
import com.example.requisitionmanagementapi.dao.RoleDAO;
import com.example.requisitionmanagementapi.dto.RoleDTO;
import com.example.requisitionmanagementapi.dto.RoleRequest;
import com.example.requisitionmanagementapi.dto.SupplierDTO;
import com.example.requisitionmanagementapi.entity.Permission;
import com.example.requisitionmanagementapi.entity.Role;
import com.example.requisitionmanagementapi.entity.Supplier;
import com.example.requisitionmanagementapi.mapper.RoleMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService {

    private final PermissionDAO permissionDao;
    private final RoleDAO roleDao;
    private final RoleMapper roleMapper;


    public List<RoleDTO> getAll() {
        return roleMapper.toDTOList(roleDao.findAll());
    }

    public RoleDTO getById(Long id) {
        return roleDao.findById(id).map(roleMapper::toDTO).orElse(null);
    }

    @Transactional
    public RoleDTO createRole(RoleRequest request) {
        Set<Permission> permissions = request.getPermissionIds().stream()
                .map(id -> permissionDao.findById(id).orElseThrow())
                .collect(Collectors.toSet());

        Role role = Role.builder()
                .name(request.getName())
                .permissions(permissions)
                .build();

        roleDao.save(role);
        return roleMapper.toDTO(role);
    }

    @Transactional
    public RoleDTO updateRole(Long id, RoleRequest request) {
        Role role = roleDao.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(request.getName());

        Set<Permission> permissions = request.getPermissionIds().stream()
                .map(pid -> permissionDao.findById(pid).orElseThrow())
                .collect(Collectors.toSet());
        role.setPermissions(permissions);

        roleDao.save(role);
        return roleMapper.toDTO(role);
    }

    public void delete(Long id) {
        roleDao.deleteById(id);
    }

}
