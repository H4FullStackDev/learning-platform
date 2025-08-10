package com.example.requisitionmanagementapi.listner;

import com.example.requisitionmanagementapi.Notifications.NotificationService;
import com.example.requisitionmanagementapi.enums.NotificationType;
import com.example.requisitionmanagementapi.event.RequisitionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RequisitionNotificationListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(
            phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
    public void on(RequisitionEvent ev) {
        if (ev.getType() != RequisitionEvent.Type.CANCELED) return;

        String title = "Réquisition";
        String reason = ev.getMetadata() != null ? (String) ev.getMetadata().get("reason") : null;
        for (Long uid : ev.getRecipients()) {
            String body = "La réquisition <strong>" + ev.getRequisitionName() + "</strong> a été annulée";
            String link = "/requisitions/";

            notificationService.createAndSendToUser(
                    uid, title, body, link, NotificationType.ALERT
            );
        }
    }
}
