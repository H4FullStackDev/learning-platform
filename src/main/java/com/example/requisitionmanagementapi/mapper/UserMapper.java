package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.RequisitionHistoryDTO;
import com.example.requisitionmanagementapi.dto.UserDTO;
import com.example.requisitionmanagementapi.entity.RequisitionHistory;
import com.example.requisitionmanagementapi.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class, DepartmentMapper.class})
public interface UserMapper {

    UserDTO toDTO(User entity);
    User toEntity(UserDTO dto);

    List<UserDTO> toDTOList(List<User> entities);
    List<User> toEntityList(List<UserDTO> dtos);
}
