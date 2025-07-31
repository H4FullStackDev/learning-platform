package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.ArticleDAO;
import com.example.requisitionmanagementapi.dto.ArticleCount;
import com.example.requisitionmanagementapi.dto.ArticleDTO;
import com.example.requisitionmanagementapi.entity.Article;
import com.example.requisitionmanagementapi.mapper.ArticleMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleDAO dao;
    private final ArticleMapper mapper;


    public List<ArticleDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public ArticleDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
    }

    public ArticleDTO save(ArticleDTO dto) {
        Optional<Article> existing = dao.findByNameIgnoreCase(dto.getName());
        if (existing.isPresent() && (dto.getId() == null || !existing.get().getId().equals(dto.getId()))) {
            throw new IllegalArgumentException("Un article avec ce nom existe déjà !");
        }
        Article entity = mapper.toEntity(dto);
        return mapper.toDTO(dao.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> getLowStock() {
        return mapper.toDTOList(dao.findLowStock());
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> getLargeStock() {
        return mapper.toDTOList(dao.findLargeStock());
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> getOutOfStock() {
        return mapper.toDTOList(dao.findOutOfStock());
    }

    /**
     * Faire une entrée en stock sur un article donné
     */
    public ArticleDTO addStock(Long articleId, int quantityToAdd) {
        Article article = dao.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article introuvable"));
        article.setStockQuantity(article.getStockQuantity() + quantityToAdd);
        dao.save(article);
        return mapper.toDTO(article);
    }

    public void delete(Long id) {
        dao.deleteById(id);
    }

    public ArticleCount getStockCount() {
        long total = dao.count();
        long lowStock = dao.findLowStock().size();
        long largeStock = dao.findLargeStock().size();
        long outOfStock = dao.findOutOfStock().size();
        return new ArticleCount(total, lowStock, largeStock, outOfStock);
    }

}
