package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Article;
import com.example.requisitionmanagementapi.entity.StockEntryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockEntryHistoryDAO extends JpaRepository<StockEntryHistory,Long> {
    List<StockEntryHistory> findByArticleOrderByEntryDateDesc(Article article);
}
