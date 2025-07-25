package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.RequisitionDAO;
import com.example.requisitionmanagementapi.dto.RequisitionDTO;
import com.example.requisitionmanagementapi.entity.Requisition;
import com.example.requisitionmanagementapi.mapper.RequisitionMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RequisitionService {

    private final RequisitionDAO dao;
    private final RequisitionMapper mapper;


    public List<RequisitionDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public RequisitionDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
    }

    public RequisitionDTO save(RequisitionDTO dto) {
        Requisition entity = mapper.toEntity(dto);
        return mapper.toDTO(dao.save(entity));
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }
}

