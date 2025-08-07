package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentDAO extends JpaRepository<Department, Long> {
}
