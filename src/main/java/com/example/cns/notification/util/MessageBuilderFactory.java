package com.example.cns.notification.util;

import com.example.cns.notification.type.NotificationType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageBuilderFactory {
    private final List<NotificationMessageBuilder> builders;
    private Map<NotificationType, NotificationMessageBuilder> messageBuilders;

    public MessageBuilderFactory(List<NotificationMessageBuilder> builders) {
        this.builders = builders;
    }

    @PostConstruct
    private void init() {
        this.messageBuilders = new EnumMap<>(NotificationType.class);
        for (NotificationMessageBuilder builder : builders) {
            messageBuilders.put(builder.getNotificationType(), builder);
        }
    }

    public NotificationMessageBuilder getBuilder(NotificationType type) {
        return messageBuilders.get(type);
    }
}
