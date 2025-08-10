package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.Notifications.NotificationService;
import com.example.requisitionmanagementapi.dto.NotificationDTO;
import com.example.requisitionmanagementapi.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    /** Liste paginée : read=null → toutes, read=true → lues, read=false → non lues */
    @GetMapping
    public PageResponse<NotificationDTO> list(
            @RequestParam(required = false) Boolean read,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Principal principal
    ) {
        Page<NotificationDTO> p = service.list(read, pageable, principal);
        return PageResponse.from(p);
    }

    /** Compteur de notifications non lues */
    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Principal principal) {
        return Map.of("count", service.unreadCount(principal));
    }

    /** Marquer une notification comme lue (vérifie l’ownership) */
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id, Principal principal) {
        service.markRead(id, principal);
        return ResponseEntity.noContent().build(); // 204
    }

    /** Tout marquer comme lu (bulk update) */
    @PostMapping("/read-all")
    public Map<String,Integer> markAllRead(Principal principal) {
        return Map.of("updated", service.markAllRead(principal));
    }

    @PostMapping("/batch/delete")
    public Map<String,Integer> deleteBatch(@RequestBody List<Long> ids, Principal principal) {
        return Map.of("deleted", service.deleteBatchForCurrent(ids, principal));
    }


    /** Supprimer toutes les notifications de l’utilisateur courant */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(Principal principal) {
        service.deleteAll(principal);
        return ResponseEntity.noContent().build();
    }
}
