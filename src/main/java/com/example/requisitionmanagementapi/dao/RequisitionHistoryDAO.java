package com.example.requisitionmanagementapi.dao;


import com.example.requisitionmanagementapi.entity.RequisitionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionHistoryDAO extends JpaRepository<RequisitionHistory, Long> {
    List<RequisitionHistory> findAllByOrderByActionDateDesc();
    List<RequisitionHistory> findByRequisitionIdOrderByActionDateDesc(Long id);
}
