package com.example.requisitionmanagementapi.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RequisitionEvent extends ApplicationEvent {
    public enum Type { CREATED, VALIDATED, REJECTED, CANCELED,DELIVERED,  IN_PROCESS }

    private final String requisitionName;
    private final Type type;
    private final Long actorId;
    private final java.util.List<Long> recipients;
    private final java.util.Map<String, Object> metadata;

    public RequisitionEvent(Object source, String requisitionName, Type type,
                            Long actorId, java.util.List<Long> recipients,
                            java.util.Map<String, Object> metadata) {
        super(source);
        this.requisitionName = requisitionName;
        this.type = type;
        this.actorId = actorId;
        this.recipients = recipients;
        this.metadata = metadata;
    }
}
