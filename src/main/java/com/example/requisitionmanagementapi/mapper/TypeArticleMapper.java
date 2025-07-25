package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.TypeArticleDTO;
import com.example.requisitionmanagementapi.entity.TypeArticle;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TypeArticleMapper {
    TypeArticleDTO toDTO(TypeArticle entity);
    TypeArticle toEntity(TypeArticleDTO dto);

    List<TypeArticleDTO> toDTOList(List<TypeArticle> entities);
    List<TypeArticle> toEntityList(List<TypeArticleDTO> dtos);
}
