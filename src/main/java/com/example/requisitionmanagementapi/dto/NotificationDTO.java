package com.example.requisitionmanagementapi.dto;

import com.example.requisitionmanagementapi.entity.User;
import com.example.requisitionmanagementapi.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationDTO {

    private Long id;

    private Long userId;

    private String title;

    private String body;

    private String link;

    private NotificationType type;

    private LocalDateTime createdAt;

    private boolean read;
}
