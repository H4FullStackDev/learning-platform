package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.dao.DepartmentDAO;
import com.example.requisitionmanagementapi.dao.RoleDAO;
import com.example.requisitionmanagementapi.dao.SupplierDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.DashboardStatDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DashboardStatService {

    private final UserDAO userDAO;
    private final DepartmentDAO departmentDAO;
    private final SupplierDAO supplierDAO;

    public DashboardStatDTO getDashboardStatDTO() {
        long users       = userDAO.count();
        long departments = departmentDAO.count();
        long suppliers   = supplierDAO.count();

        DashboardStatDTO dto = new DashboardStatDTO();
        dto.setCountUser(users);
        dto.setCountDepartment(departments);
        dto.setCountSupplier(suppliers);
        return dto;
    }

}
