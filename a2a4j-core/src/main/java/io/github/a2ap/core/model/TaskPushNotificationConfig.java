package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Configuration for task-specific push notifications, extending the base push
 * notification config.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskPushNotificationConfig extends PushNotificationConfig {

    /**
     * The task ID to receive push notifications for.
     * Required field.
     */
    @JsonProperty("task_id")
    private String taskId;
}
