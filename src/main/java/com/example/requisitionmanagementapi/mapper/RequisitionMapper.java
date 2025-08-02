package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.RequisitionDTO;
import com.example.requisitionmanagementapi.dto.RequisitionResponse;
import com.example.requisitionmanagementapi.entity.Requisition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        UserMapper.class,
        RequisitionArticleMapper.class,
        DeliveryMapper.class
})
public interface RequisitionMapper {
    RequisitionDTO toDTO(Requisition entity);
    RequisitionResponse toDTOResponse(Requisition entity);
    Requisition toEntity(RequisitionDTO dto);

    List<RequisitionDTO> toDTOList(List<Requisition> entities);
    List<Requisition> toEntityList(List<RequisitionDTO> dtos);
}
