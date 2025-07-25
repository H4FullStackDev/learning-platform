package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.RequisitionArticleDAO;
import com.example.requisitionmanagementapi.dto.RequisitionArticleDTO;
import com.example.requisitionmanagementapi.entity.RequisitionArticle;
import com.example.requisitionmanagementapi.mapper.RequisitionArticleMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RequisitionArticleService {

    private final RequisitionArticleDAO dao;
    private final RequisitionArticleMapper mapper;

    public List<RequisitionArticleDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public RequisitionArticleDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
    }

    public RequisitionArticleDTO save(RequisitionArticleDTO dto) {
        RequisitionArticle entity = mapper.toEntity(dto);
        return mapper.toDTO(dao.save(entity));
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }
}
