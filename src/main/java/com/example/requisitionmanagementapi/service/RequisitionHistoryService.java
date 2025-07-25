package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.RequisitionHistoryDAO;
import com.example.requisitionmanagementapi.dto.RequisitionHistoryDTO;
import com.example.requisitionmanagementapi.entity.RequisitionHistory;
import com.example.requisitionmanagementapi.mapper.RequisitionHistoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RequisitionHistoryService {

    private final RequisitionHistoryDAO dao;
    private final RequisitionHistoryMapper mapper;

    public List<RequisitionHistoryDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public RequisitionHistoryDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
    }

    public RequisitionHistoryDTO save(RequisitionHistoryDTO dto) {
        RequisitionHistory entity = mapper.toEntity(dto);
        return mapper.toDTO(dao.save(entity));
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }
}
