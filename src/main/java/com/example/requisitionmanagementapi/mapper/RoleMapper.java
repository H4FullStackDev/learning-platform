package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.RoleDTO;
import com.example.requisitionmanagementapi.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {
    RoleDTO toDTO(Role entity);
    Role toEntity(RoleDTO dto);

    List<RoleDTO> toDTOList(List<Role> entities);
    List<Role> toEntityList(List<RoleDTO> dtos);
}
