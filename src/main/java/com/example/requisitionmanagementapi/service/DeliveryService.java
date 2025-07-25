package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.DeliveryDAO;
import com.example.requisitionmanagementapi.dto.DeliveryDTO;
import com.example.requisitionmanagementapi.entity.Delivery;
import com.example.requisitionmanagementapi.mapper.DeliveryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class DeliveryService {

    private final DeliveryDAO dao;
    private final DeliveryMapper mapper;

    public List<DeliveryDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public DeliveryDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
    }

    public DeliveryDTO save(DeliveryDTO dto) {
        Delivery entity = mapper.toEntity(dto);
        return mapper.toDTO(dao.save(entity));
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }
}
