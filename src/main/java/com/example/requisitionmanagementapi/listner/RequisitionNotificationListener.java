package com.example.requisitionmanagementapi.listner;

import com.example.requisitionmanagementapi.Notifications.NotificationService;
import com.example.requisitionmanagementapi.enums.NotificationType;
import com.example.requisitionmanagementapi.event.RequisitionEvent;
import com.example.requisitionmanagementapi.templates.RequisitionNotificationTemplates;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RequisitionNotificationListener {

    private final NotificationService notificationService;
    private final RequisitionNotificationTemplates templates;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RequisitionEvent ev) {
        // Pas de destinataires, pas de cinéma
        if (ev.getRecipients().isEmpty()) return;

        var t = templates.build(ev);

        for (Long uid : ev.getRecipients()) {
            notificationService.createAndSendToUser(
                    uid,
                    t.title(),
                    t.body(),
                    t.link(),
                    t.type()
            );
        }
    }
}

