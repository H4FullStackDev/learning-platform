package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.ArticleDAO;
import com.example.requisitionmanagementapi.dto.ArticleDTO;
import com.example.requisitionmanagementapi.entity.Article;
import com.example.requisitionmanagementapi.mapper.ArticleMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleDAO dao;
    private final ArticleMapper mapper;


    public List<ArticleDTO> getAll() {
        return mapper.toDTOs(dao.findAll());
    }

    public ArticleDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
    }

    public ArticleDTO save(ArticleDTO dto) {
        Article entity = mapper.toEntity(dto);
        return mapper.toDTO(dao.save(entity));
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }
}
