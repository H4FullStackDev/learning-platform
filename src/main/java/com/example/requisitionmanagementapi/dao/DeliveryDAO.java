package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Delivery;
import com.example.requisitionmanagementapi.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeliveryDAO extends JpaRepository<Delivery, Long> {
    List<Delivery> findByRequisitionId(Long requisitionId);
    List<Delivery> findAllByOrderByDeliveryDateDesc();
    long countByDeliveryStatus(DeliveryStatus deliveryStatus);
}
