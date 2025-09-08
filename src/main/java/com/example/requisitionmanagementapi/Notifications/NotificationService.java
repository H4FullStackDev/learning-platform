package com.example.requisitionmanagementapi.Notifications;

import com.example.requisitionmanagementapi.dao.NotificationDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.NotificationDTO;
import com.example.requisitionmanagementapi.entity.Notification;
import com.example.requisitionmanagementapi.entity.User;
import com.example.requisitionmanagementapi.enums.NotificationType;
import com.example.requisitionmanagementapi.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.util.List;


import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationDAO dao;
    private final NotificationPublisher publisher;
    private final UserDAO userDAO;
    private final NotificationMapper mapper;

    /** Utilitaire pour récupérer l'ID user courant (pattern standard chez toi) */
    private Long currentUserId(Principal principal) {
        return userDAO.findByUsername(principal.getName())
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("Utilisateur courant introuvable"));
    }

    /** Création + push ciblé (avec envoi WS après COMMIT) */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public NotificationDTO createAndSendToUser(Long userId, String title, String body, String link, NotificationType type) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + userId));

        Notification entity = new Notification();
        entity.setUser(user);
        entity.setTitle(title);
        entity.setBody(body);
        entity.setLink(link);
        entity.setType(type);
        entity.setRead(false);
        entity.setCreatedAt(java.time.LocalDateTime.now());

        entity = dao.save(entity);
        NotificationDTO dto = mapper.toDto(entity);

        // 👉 Push APRES commit pour éviter les notifs fantômes en cas de rollback
        registerSynchronization(
                new TransactionSynchronization() {
                    @Override public void afterCommit() {
                        publisher.sendToUser(user.getUsername(), dto);
                    }
                }
        );

        return dto;
    }

    /** (Optionnel) Diffusion globale : je déconseille de PERSISTER sans user.
     *  Soit tu broadcastes SANS save, soit tu utilises un "system user".
     */
    public void broadcast(String topic, String title, String body, String link, NotificationType type) {
        NotificationDTO dto = new NotificationDTO();
        dto.setTitle(title);
        dto.setBody(body);
        dto.setLink(link);
        dto.setType(type);
        dto.setCreatedAt(java.time.LocalDateTime.now());
        dto.setRead(false);
        publisher.broadcast(topic, dto); // pas de save DB ici
    }

    /** Liste paginée — read=null => tout, sinon filtre par statut */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<NotificationDTO> list(Boolean read, org.springframework.data.domain.Pageable pageable,
                                                                      Principal principal) {
        Long uid = currentUserId(principal);
        var page = (read == null)
                ? dao.findByUserIdOrderByCreatedAtDesc(uid, pageable)
                : dao.findByUserIdAndReadOrderByCreatedAtDesc(uid, read, pageable);
        return page.map(mapper::toDto);
    }

    /** Unread count */
    @Transactional(readOnly = true)
    public long unreadCount(Principal principal) {
        return dao.countByUserIdAndReadFalse(currentUserId(principal));
    }

    /** Marquer une notification comme lue (avec ownership) */
    @Transactional
    public void markRead(Long id, Principal principal) {
        Long uid = currentUserId(principal);
        Notification n = dao.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Notification introuvable"));
        if (!n.isRead()) {
            n.setRead(true);
            dao.save(n);
        }
    }

    /** Tout marquer comme lu (bulk, O(1)) */
    @Transactional
    public int markAllRead(Principal principal) {
        return dao.markAllRead(currentUserId(principal));
    }

    @Transactional
    public int deleteBatchForCurrent(List<Long> ids, Principal principal) {
        Long uid = userDAO.findByUsername(principal.getName())
                .orElseThrow().getId();
        return dao.deleteByIdInAndUserId(ids, uid);
    }

    /** Supprimer toutes les notifs de l'utilisateur courant */
    @Transactional
    public void deleteAll(Principal principal) {
        dao.deleteByUserId(currentUserId(principal));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public NotificationDTO notifyLowStock(Long userId, Long articleId, String libelle, int stock, int minStock) {
        final String safeName = HtmlUtils.htmlEscape(libelle);
        final String title = "Stock faible";
        final String body  = "L’article <strong>%s</strong> est bientôt en rupture (%d ≤ min %d)."
                .formatted(safeName, stock, minStock);
        final String link  = "/articles/%d".formatted(articleId);
        return createAndSendToUser(userId, title, body, link, NotificationType.MESSAGE);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public NotificationDTO notifyOutOfStock(Long userId, Long articleId, String libelle) {
        final String safeName = HtmlUtils.htmlEscape(libelle);
        final String title = "Rupture de stock";
        final String body  = "L’article <strong>%s</strong> est en <strong>rupture</strong> (stock = 0)."
                .formatted(safeName);
        final String link  = "/articles/%d".formatted(articleId);
        return createAndSendToUser(userId, title, body, link, NotificationType.ALERT);
    }
}

