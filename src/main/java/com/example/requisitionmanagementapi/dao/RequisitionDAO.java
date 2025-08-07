package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Department;
import com.example.requisitionmanagementapi.entity.Requisition;
import com.example.requisitionmanagementapi.enums.RequisitionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RequisitionDAO extends JpaRepository<Requisition, Long> {
    long countByStatus(RequisitionStatus status);
    @Query("SELECT r FROM Requisition r JOIN r.createdBy u JOIN u.departments d WHERE d IN :departments")
    List<Requisition> findByCreatedBy_DepartmentsIn(@Param("departments") Set<Department> departments);

    @Query("SELECT COUNT(DISTINCT r) FROM Requisition r JOIN r.createdBy u JOIN u.departments d WHERE d IN :departments AND r.status = :status")
    long countByDepartmentAndStatus(@Param("departments") Set<Department> departments, @Param("status") RequisitionStatus status);

}