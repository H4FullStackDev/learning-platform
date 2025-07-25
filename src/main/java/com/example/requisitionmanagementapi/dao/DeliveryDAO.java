package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryDAO extends JpaRepository<Delivery, Long> {}
