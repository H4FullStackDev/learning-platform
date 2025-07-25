package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Requisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionDAO extends JpaRepository<Requisition, Long> {}