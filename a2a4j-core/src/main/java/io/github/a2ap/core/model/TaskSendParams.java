package io.github.a2ap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSendParams {
    private String id;
    private String sessionId;
    private Message message;
    private TaskPushNotificationConfig pushNotification;
    private Integer historyLength;
    private Map<String, Object> metadata;
}
