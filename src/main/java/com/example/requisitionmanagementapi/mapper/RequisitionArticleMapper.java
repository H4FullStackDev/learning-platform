package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.RequisitionArticleDTO;
import com.example.requisitionmanagementapi.entity.RequisitionArticle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ArticleMapper.class})
public interface RequisitionArticleMapper {
    RequisitionArticleDTO toDTO(RequisitionArticle entity);
    RequisitionArticle toEntity(RequisitionArticleDTO dto);

    List<RequisitionArticleDTO> toDTOList(List<RequisitionArticle> entities);
    List<RequisitionArticle> toEntityList(List<RequisitionArticleDTO> dtos);
}
