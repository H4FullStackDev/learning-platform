package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.DeliveryDTO;
import com.example.requisitionmanagementapi.entity.Delivery;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    DeliveryDTO toDTO(Delivery entity);
    Delivery toEntity(DeliveryDTO dto);

    List<DeliveryDTO> toDTOList(List<Delivery> entities);
    List<Delivery> toEntityList(List<DeliveryDTO> dtos);
}
