package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleDAO extends JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a WHERE a.stockQuantity <= a.stockMin")
    List<Article> findLowStock();

    @Query("SELECT a FROM Article a WHERE a.stockQuantity > a.stockMin")
    List<Article> findLargeStock();

    @Query("SELECT a FROM Article a WHERE a.stockQuantity < 1")
    List<Article> findOutOfStock();


    Optional<Article> findByNameIgnoreCase(String name);
    List<Article> findBySupplierId(Long supplierId);

    List<Article> findAllByStockQuantityEquals(int stock);

    // Stock faible (0 < stock ≤ minStock)
    @Query("select a from Article a where a.stockQuantity > 0 and a.stockQuantity <= a.stockMin")
    List<Article> findAllLowStock();

}
