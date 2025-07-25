package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.RequisitionArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionArticleDAO extends JpaRepository<RequisitionArticle, Long> {}
