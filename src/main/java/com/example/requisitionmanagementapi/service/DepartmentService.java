package com.example.requisitionmanagementapi.service;


import com.example.requisitionmanagementapi.dao.DepartmentDAO;
import com.example.requisitionmanagementapi.dto.DepartmentDTO;
import com.example.requisitionmanagementapi.entity.Department;
import com.example.requisitionmanagementapi.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DepartmentService {

    private final DepartmentDAO dao;
    private final DepartmentMapper mapper;

    public List<DepartmentDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public DepartmentDTO getById(Long id) {
        return dao.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }

    public DepartmentDTO save(DepartmentDTO dto) {
        Department entity = mapper.toEntity(dto);
        entity = dao.save(entity);
        return mapper.toDTO(entity);
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }
}

