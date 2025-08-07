package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.DepartmentDTO;
import com.example.requisitionmanagementapi.entity.Department;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentDTO toDTO(Department entity);
    Department toEntity(DepartmentDTO dto);

    List<DepartmentDTO> toDTOList(List<Department> entities);
    List<Department> toEntityList(List<DepartmentDTO> dtos);
}
