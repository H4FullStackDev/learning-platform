package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierDAO extends JpaRepository<Supplier, Long> {}
