package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.ArticleDAO;
import com.example.requisitionmanagementapi.dao.SupplierDAO;
import com.example.requisitionmanagementapi.dto.ArticleDTO;
import com.example.requisitionmanagementapi.dto.SupplierDTO;
import com.example.requisitionmanagementapi.entity.Article;
import com.example.requisitionmanagementapi.entity.Supplier;
import com.example.requisitionmanagementapi.mapper.ArticleMapper;
import com.example.requisitionmanagementapi.mapper.SupplierMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class SupplierService {

    private final SupplierDAO dao;
    private final SupplierMapper mapper;
    private final ArticleDAO articleDAO;
    private final ArticleMapper articleMapper;


    public List<SupplierDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public SupplierDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
    }

    public SupplierDTO save(SupplierDTO dto) {
        Supplier entity = mapper.toEntity(dto);
        return mapper.toDTO(dao.save(entity));
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> getArticles(Long supplierId) {
        List<Article> articles = articleDAO.findBySupplierId(supplierId);
        return articleMapper.toDTOList(articles);
    }
}
