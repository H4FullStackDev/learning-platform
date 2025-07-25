package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDAO extends JpaRepository<Article, Long> {}
