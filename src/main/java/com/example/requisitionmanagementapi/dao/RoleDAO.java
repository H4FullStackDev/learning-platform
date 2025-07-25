package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleDAO extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
