package com.example.requisitionmanagementapi.Notifications;
import com.example.requisitionmanagementapi.dto.NotificationDTO;
import com.example.requisitionmanagementapi.entity.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationPublisher {

    public static final String QUEUE_NOTIFICATIONS = "/queue/notifications";

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /** Broadcast à tous les abonnés d’un topic public. */
    public void broadcast(String topic, Object payload) {
        // évite les doubles slash si topic commence par '/'
        String dest = topic.startsWith("/") ? "/topic" + topic : "/topic/" + topic;
        messagingTemplate.convertAndSend(dest, payload);
    }


    /** Envoi ciblé à un utilisateur (username = Principal.getName()). */
    public void sendToUser(String username, NotificationDTO payload) {
        messagingTemplate.convertAndSendToUser(username, QUEUE_NOTIFICATIONS, payload);
    }

    /** Envoi à plusieurs utilisateurs (pratique pour événements multi-destinataires). */
    public void sendToUsers(Iterable<String> usernames, NotificationDTO payload) {
        for (String u : usernames) {
            sendToUser(u, payload);
        }
    }
}

