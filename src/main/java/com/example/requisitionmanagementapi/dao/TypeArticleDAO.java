package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.TypeArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeArticleDAO extends JpaRepository<TypeArticle, Long> {}
