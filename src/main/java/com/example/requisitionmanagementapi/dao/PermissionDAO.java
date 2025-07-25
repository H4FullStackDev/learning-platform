package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionDAO extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
