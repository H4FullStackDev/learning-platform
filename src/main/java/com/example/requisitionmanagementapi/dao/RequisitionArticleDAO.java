package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Requisition;
import com.example.requisitionmanagementapi.entity.RequisitionArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionArticleDAO extends JpaRepository<RequisitionArticle, Long> {
    List<RequisitionArticle> findByRequisition(Requisition requisition);

}
