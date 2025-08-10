package com.example.requisitionmanagementapi.entity;

import com.example.requisitionmanagementapi.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Notification {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        private User user;

        @Column(nullable = false)
        private String title;

        @Column(nullable = false)
        private String body;

        private String link;

        @Enumerated(EnumType.STRING)
        private NotificationType type;

        @Column(nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "is_read",nullable = false)
        private boolean read = false;

    }

