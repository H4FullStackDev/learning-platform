package com.example.requisitionmanagementapi.dao;

import com.example.requisitionmanagementapi.entity.Notification;
import com.example.requisitionmanagementapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationDAO extends JpaRepository<Notification, Long> {

    // Liste paginée (toutes)
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Liste paginée (par statut de lecture)
    Page<Notification> findByUserIdAndReadOrderByCreatedAtDesc(Long userId, boolean read, Pageable pageable);

    // Compteur non lus
    long countByUserIdAndReadFalse(Long userId);

    // Ownership check
    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    // Marquer tout comme lu (bulk, plus rapide)
    @Modifying
    @Query("update Notification n set n.read = true where n.user.id = :userId and n.read = false")
    int markAllRead(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Notification n where n.id in :ids and n.user.id = :userId")
    int deleteByIdInAndUserId(@Param("ids") List<Long> ids, @Param("userId") Long userId);


    // (Optionnel) suppression ciblée
    void deleteByUserId(Long userId);
}

