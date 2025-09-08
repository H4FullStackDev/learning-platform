package com.example.requisitionmanagementapi.templates;

import com.example.requisitionmanagementapi.enums.NotificationType;
import com.example.requisitionmanagementapi.event.RequisitionEvent;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class RequisitionNotificationTemplates {

    public record Template(String title, String body, String link, NotificationType type) {}

    public Template build(RequisitionEvent ev) {
        return switch (ev.getType()) {
            case VALIDATED -> validated(ev);
            case REJECTED -> rejected(ev);
            case CANCELED -> canceled(ev);
            case DELIVERED -> delivered(ev);
            case IN_PROCESS -> inProgress(ev);
            case CREATED -> created(ev);
        };
    }

    private Template created(RequisitionEvent ev) {
        String title = "Réquisition";
        String body = "Nouvelle réquisition <strong>%s</strong> créée.".formatted(html(ev.getRequisitionName()));
        String link = linkTo(ev);
        return new Template(title, body, link, NotificationType.MESSAGE);
    }

    private Template validated(RequisitionEvent ev) {
        String title = "Réquisition validée";
        String body = "La réquisition <strong>%s</strong> a été validée.".formatted(html(ev.getRequisitionName()));
        String link = linkTo(ev);
        return new Template(title, body, link, NotificationType.MESSAGE);
    }

    private Template rejected(RequisitionEvent ev) {
        String title = "Réquisition rejetée";
        String reason = (String) ev.getMetadata().getOrDefault("reason", "Motif non précisé");
        String body = "La réquisition <strong>%s</strong> a été rejetée. Motif: <em>%s</em>."
                .formatted(html(ev.getRequisitionName()), html(reason));
        String link = linkTo(ev);
        return new Template(title, body, link, NotificationType.MESSAGE);
    }

    private Template canceled(RequisitionEvent ev) {
        String title = "Réquisition annulée";
        String reason = (String) ev.getMetadata().getOrDefault("reason", "Motif non précisé");
        String body = "La réquisition <strong>%s</strong> a été annulée. Motif: <em>%s</em>."
                .formatted(html(ev.getRequisitionName()), html(reason));
        String link = linkTo(ev);
        return new Template(title, body, link, NotificationType.MESSAGE);
    }

    private Template delivered(RequisitionEvent ev) {
        String title = "Réquisition livrée";
        String body = "La réquisition <strong>%s</strong> a été livrée.%s"
                .formatted(html(ev.getRequisitionName()));
        String link = linkTo(ev);
        return new Template(title, body, link, NotificationType.MESSAGE);
    }

    private Template inProgress(RequisitionEvent ev) {
        String title = "Livraison planifiée";

        ZonedDateTime zdt = coerceToZoned(ev.getMetadata().get("deliveryAt"),
                (String) ev.getMetadata().getOrDefault("zone", "Africa/Lome"));

        String dateStr = zdt != null
                ? formatFr(zdt)
                : "une date à préciser";

        // Fenêtre horaire optionnelle
        String window = (String) ev.getMetadata().get("timeWindow");
        if (window == null) {
            LocalTime from = (LocalTime) ev.getMetadata().get("from");
            LocalTime to   = (LocalTime) ev.getMetadata().get("to");
            if (from != null && to != null) {
                window = "%s–%s".formatted(from, to);
            }
        }
        String windowPart = window != null ? " (créneau %s)".formatted(html(window)) : "";

        String body = "La livraison de la réquisition <strong>%s</strong> est prévue le <strong>%s</strong>%s."
                .formatted(html(ev.getRequisitionName()), html(dateStr), windowPart);

        String link = linkTo(ev);
        return new Template(title, body, link, NotificationType.MESSAGE);
    }


    private String linkTo(RequisitionEvent ev) {
        return "requisition";
    }

    private String html(String s) {
        return org.springframework.web.util.HtmlUtils.htmlEscape(s == null ? "" : s);
    }


    private ZonedDateTime coerceToZoned(Object raw, String zoneId) {
        if (raw == null) return null;
        ZoneId zone = ZoneId.of(zoneId);
        if (raw instanceof ZonedDateTime z) return z.withZoneSameInstant(zone);
        if (raw instanceof Instant i)       return i.atZone(zone);
        if (raw instanceof LocalDateTime l) return l.atZone(zone);
        if (raw instanceof LocalDate d)     return d.atStartOfDay(zone);
        if (raw instanceof String s) {
            try {
                // essaie ZonedDateTime, sinon Instant, sinon LocalDateTime, sinon LocalDate
                return ZonedDateTime.parse(s).withZoneSameInstant(zone);
            } catch (Exception ignore) { }
            try {
                return Instant.parse(s).atZone(zone);
            } catch (Exception ignore) { }
            try {
                return LocalDateTime.parse(s).atZone(zone);
            } catch (Exception ignore) { }
            try {
                return LocalDate.parse(s).atStartOfDay(zone);
            } catch (Exception ignore) { }
            return null;
        }
        return null;
    }

    private String formatFr(ZonedDateTime zdt) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("EEEE d MMMM uuuu 'à' HH:mm", Locale.FRENCH);
        return zdt.format(f);
    }

}
