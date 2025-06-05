package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Configuration for task-specific push notifications, extending the base push
 * notification config.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskPushNotificationConfig extends PushNotificationConfig {

    /**
     * The task ID to receive push notifications for.
     * Required field.
     */
    @JsonProperty("task_id")
    private String taskId;

    public TaskPushNotificationConfig() {
        super();
    }

    public TaskPushNotificationConfig(String url, String authToken, String taskId) {
        super(url, authToken);
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        TaskPushNotificationConfig that = (TaskPushNotificationConfig) o;
        return Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), taskId);
    }

    @Override
    public String toString() {
        return "TaskPushNotificationConfig{" +
                "taskId='" + taskId + '\'' +
                ", url='" + getUrl() + '\'' +
                ", authToken='" + getAuthToken() + '\'' +
                '}';
    }

    public static TaskPushNotificationConfigBuilder taskPushBuilder() {
        return new TaskPushNotificationConfigBuilder();
    }

    public static class TaskPushNotificationConfigBuilder {
        private String url;
        private String authToken;
        private String taskId;

        TaskPushNotificationConfigBuilder() {
        }

        public TaskPushNotificationConfigBuilder url(String url) {
            this.url = url;
            return this;
        }

        public TaskPushNotificationConfigBuilder authToken(String authToken) {
            this.authToken = authToken;
            return this;
        }

        public TaskPushNotificationConfigBuilder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public TaskPushNotificationConfig build() {
            return new TaskPushNotificationConfig(url, authToken, taskId);
        }

        @Override
        public String toString() {
            return "TaskPushNotificationConfig.TaskPushNotificationConfigBuilder(url=" + this.url +
                    ", authToken=" + this.authToken +
                    ", taskId=" + this.taskId + ")";
        }
    }
}
