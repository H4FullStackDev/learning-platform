package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.PermissionDAO;
import com.example.requisitionmanagementapi.dto.PermissionDTO;
import com.example.requisitionmanagementapi.entity.Permission;
import com.example.requisitionmanagementapi.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionDAO dao;
    private final PermissionMapper mapper;

    public List<PermissionDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public PermissionDTO getById(Long id) {
        return dao.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }

    public PermissionDTO save(PermissionDTO dto) {
        Permission entity = mapper.toEntity(dto);
        entity = dao.save(entity);
        return mapper.toDTO(entity);
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }
}

