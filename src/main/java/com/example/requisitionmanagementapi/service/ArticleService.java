package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.ArticleDAO;
import com.example.requisitionmanagementapi.dao.StockEntryHistoryDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.ArticleCount;
import com.example.requisitionmanagementapi.dto.ArticleDTO;
import com.example.requisitionmanagementapi.dto.StockEntryHistoryDTO;
import com.example.requisitionmanagementapi.entity.Article;
import com.example.requisitionmanagementapi.entity.StockEntryHistory;
import com.example.requisitionmanagementapi.entity.User;
import com.example.requisitionmanagementapi.mapper.ArticleMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleDAO dao;
    private final ArticleMapper mapper;
    private final UserDAO userDAO;
    private final StockEntryHistoryDAO stockEntryHistoryDAO;


    public List<ArticleDTO> getAll() {
        return mapper.toDTOList(dao.findAll());
    }

    public List<StockEntryHistoryDTO> getHistory(Long articleId) {
        Article article = dao.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article introuvable"));
        List<StockEntryHistory> entities = stockEntryHistoryDAO.findByArticleOrderByEntryDateDesc(article);
        return entities.stream().map(history -> {
            StockEntryHistoryDTO dto = new StockEntryHistoryDTO();
            dto.setId(history.getId());
            dto.setArticleName(article.getName());
            dto.setQuantity(history.getQuantity());
            dto.setTotalAmount(history.getTotalAmount());
            dto.setEntryDate(history.getEntryDate());
            dto.setEnteredBy(history.getEnteredBy() != null ? history.getEnteredBy().getUsername() : null);
            return dto;
        }).collect(Collectors.toList());
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
    public ArticleDTO addStock(Long articleId, int quantityToAdd, double totalAmount, Principal principal) {
        Article article = dao.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article introuvable"));
        article.setStockQuantity(article.getStockQuantity() + quantityToAdd);
        dao.save(article);
        User currentUser = getCurrentUser(principal);
        addStockEntryHistory(article, quantityToAdd, totalAmount, currentUser);
        return mapper.toDTO(article);
    }

    public ArticleDTO getById(Long id) {
        return dao.findById(id).map(mapper::toDTO).orElse(null);
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

    private User getCurrentUser(Principal principal) {
        return userDAO.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
    }

    public void addStockEntryHistory(Article article, int quantity, double totalAmount, User enteredBy) {
        StockEntryHistory history = new StockEntryHistory();
        history.setArticle(article);
        history.setQuantity(quantity);
        history.setTotalAmount(totalAmount);
        history.setEntryDate(LocalDateTime.now());
        history.setEnteredBy(enteredBy);
        stockEntryHistoryDAO.save(history);
    }


}
