package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.ArticleDTO;
import com.example.requisitionmanagementapi.entity.Article;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TypeArticleMapper.class, SupplierMapper.class})
public interface ArticleMapper {
    ArticleDTO toDTO(Article entity);
    Article toEntity(ArticleDTO dto);
    List<ArticleDTO> toDTOs(List<Article> entities);
    List<Article> toEntities(List<ArticleDTO> dtos);
}