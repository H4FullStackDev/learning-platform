package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.RequisitionHistoryDTO;
import com.example.requisitionmanagementapi.entity.RequisitionHistory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class,RequisitionMapper.class})
public interface RequisitionHistoryMapper {
    RequisitionHistoryDTO toDTO(RequisitionHistory entity);
    RequisitionHistory toEntity(RequisitionHistoryDTO dto);

    List<RequisitionHistoryDTO> toDTOList(List<RequisitionHistory> entities);
    List<RequisitionHistory> toEntityList(List<RequisitionHistoryDTO> dtos);
}
