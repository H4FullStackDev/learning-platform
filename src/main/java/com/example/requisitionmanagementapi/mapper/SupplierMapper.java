package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.SupplierDTO;
import com.example.requisitionmanagementapi.entity.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    @Mapping(target = "articles", ignore = true)
    SupplierDTO toDTO(Supplier entity);

    Supplier toEntity(SupplierDTO dto);

    List<SupplierDTO> toDTOList(List<Supplier> entities);

    List<Supplier> toEntityList(List<SupplierDTO> dtos);

}