package com.example.requisitionmanagementapi.mapper;

import com.example.requisitionmanagementapi.dto.NotificationDTO;
import com.example.requisitionmanagementapi.entity.Notification;
import com.example.requisitionmanagementapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {


        @Mapping(target = "userId", source = "user.id")
        NotificationDTO toDto(Notification entity);

        @Mapping(target = "user", ignore = true)
        @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
        @Mapping(target = "read", constant = "false")
        Notification toEntity(NotificationDTO dto);

        // helper pour fixer l’utilisateur
        default Notification toEntity(NotificationDTO dto, User user) {
            Notification n = toEntity(dto);
            n.setUser(user);
            return n;
        }

    List<NotificationDTO> toDtos(List<Notification> entities);
}
