package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the status of a task.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStatus {
    
    /**
     * Completed task status constant.
     */
    public static final TaskStatus COMPLETED = TaskStatus.builder().state(TaskState.COMPLETED).build();
    
    /**
     * Cancelled task status constant.
     */
    public static final TaskStatus CANCELLED = TaskStatus.builder().state(TaskState.CANCELED).build();

    /**
     * The state of the task.
     * Required field.
     */
    @JsonProperty("state")
    private TaskState state;

    /**
     * The message associated with this status update.
     */
    @JsonProperty("message")
    private Message message;

    /**
     * The timestamp when this status was created.
     */
    @JsonProperty("timestamp")
    private String timestamp;

    /**
     * The error message if the task failed.
     */
    @JsonProperty("error")
    private String error;
}
