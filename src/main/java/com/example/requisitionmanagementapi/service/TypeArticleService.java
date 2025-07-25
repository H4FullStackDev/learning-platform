package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.TypeArticleDAO;
import com.example.requisitionmanagementapi.dto.TypeArticleDTO;
import com.example.requisitionmanagementapi.entity.TypeArticle;
import com.example.requisitionmanagementapi.mapper.TypeArticleMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TypeArticleService {
    private final TypeArticleDAO dao;
    private final TypeArticleMapper mapper;

    public List<TypeArticleDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public TypeArticleDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
    }

    public TypeArticleDTO save(TypeArticleDTO dto) {
        TypeArticle entity = mapper.toEntity(dto);
        return mapper.toDTO(dao.save(entity));
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }
}
