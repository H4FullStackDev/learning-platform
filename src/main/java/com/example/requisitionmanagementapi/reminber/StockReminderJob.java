package com.example.requisitionmanagementapi.reminber;

import com.example.requisitionmanagementapi.Notifications.NotificationService;
import com.example.requisitionmanagementapi.dao.ArticleDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.entity.Article;
import com.example.requisitionmanagementapi.entity.User;
import com.example.requisitionmanagementapi.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockReminderJob {

    private final ArticleDAO articleDAO;
    private final UserDAO userDAO;
    private final NotificationService notificationService;

    // 08:00 et 21:30 (Africa/Lome)
    @Schedules({
            @Scheduled(cron = "0 16 21 * * *",  zone = "Africa/Lome"),
            @Scheduled(cron = "0 16 23 * * *", zone = "Africa/Lome")
    })
    @Transactional(readOnly = true)
    public void sendReminders() {
        // destinataires = tous les LOGISTIQUE

        List<User> logisticiens = userDAO.findAllByRole_Name("LOGISTIQUE");
        if (logisticiens.isEmpty()) return;

        // 1) Rupture (ALERTE)
        List<Article> ruptures = articleDAO.findAllByStockQuantityEquals(0);
        for (Article a : ruptures) {
            for (User u : logisticiens) {
                // si tu as déjà les helpers :
                // notificationService.notifyOutOfStock(u.getId(), a.getId(), a.getLibelle());
                sendOutOfStock(u.getId(), a.getId(), a.getName());
            }
        }

        // 2) Stock faible (MESSAGE)
        List<Article> lowStocks = articleDAO.findAllLowStock();
        for (Article a : lowStocks) {
            for (User u : logisticiens) {
                // notificationService.notifyLowStock(u.getId(), a.getId(), a.getLibelle(), a.getStock(), a.getMinStock());
                sendLowStock(u.getId(), a.getId(), a.getName(), a.getStockQuantity(), a.getStockMin());
            }
        }
    }

    // --- Helpers inline si tu n'as pas déjà notifyOutOfStock/notifyLowStock ---
    private void sendOutOfStock(Long userId, Long articleId, String libelle) {
        String title = "Rupture de stock";
        String body  = "L’article <strong>%s</strong> est en <strong>rupture</strong> (stock = 0).".formatted(html(libelle));
        String link  = "/articles/%d".formatted(articleId);
        notificationService.createAndSendToUser(userId, title, body, link, NotificationType.ALERT);
    }

    private void sendLowStock(Long userId, Long articleId, String libelle, int stock, int minStock) {
        String title = "Stock faible";
        String body  = "L’article <strong>%s</strong> est bientôt en rupture (%d ≤ min %d)."
                .formatted(html(libelle), stock, minStock);
        String link  = "/articles/%d".formatted(articleId);
        notificationService.createAndSendToUser(userId, title, body, link, NotificationType.ALERT);
    }

    private String html(String s) {
        return org.springframework.web.util.HtmlUtils.htmlEscape(s == null ? "" : s);
    }
}
