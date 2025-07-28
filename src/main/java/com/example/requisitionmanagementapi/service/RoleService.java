package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.PermissionDAO;
import com.example.requisitionmanagementapi.dao.RoleDAO;
import com.example.requisitionmanagementapi.dto.PermissionDTO;
import com.example.requisitionmanagementapi.dto.RoleDTO;
import com.example.requisitionmanagementapi.dto.RoleRequest;
import com.example.requisitionmanagementapi.dto.SupplierDTO;
import com.example.requisitionmanagementapi.entity.Permission;
import com.example.requisitionmanagementapi.entity.Role;
import com.example.requisitionmanagementapi.entity.Supplier;
import com.example.requisitionmanagementapi.mapper.PermissionMapper;
import com.example.requisitionmanagementapi.mapper.RoleMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService {

    private final PermissionDAO permissionDao;
    private final RoleDAO roleDao;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;


    public List<RoleDTO> getAll() {
        return roleMapper.toDTOList(roleDao.findAll());
    }

    public RoleDTO getById(Long id) {
        Role role = roleDao.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        return roleMapper.toDTO(role);
    }

    @Transactional
    public RoleDTO createRole(RoleDTO dto) {
        Role entity = roleMapper.toEntity(dto);
        entity = roleDao.save(entity);
        return roleMapper.toDTO(entity);
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

    public List<PermissionDTO> getPermissionsByRoleId(Long roleId) {
        Role role = roleDao.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        return permissionMapper.toDTOList(new ArrayList<>(role.getPermissions()));
    }


    public RoleDTO assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleDao.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        Set<Permission> permissions = new HashSet<>(permissionDao.findAllById(permissionIds));
        role.setPermissions(permissions);
        Role savedRole = roleDao.save(role);
        return roleMapper.toDTO(savedRole);
    }



}
