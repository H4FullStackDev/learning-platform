package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.PermissionDTO;
import com.example.requisitionmanagementapi.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionDTO toDTO(Permission entity);
    Permission toEntity(PermissionDTO dto);

    List<PermissionDTO> toDTOList(List<Permission> entities);
    List<Permission> toEntityList(List<PermissionDTO> dtos);
}
