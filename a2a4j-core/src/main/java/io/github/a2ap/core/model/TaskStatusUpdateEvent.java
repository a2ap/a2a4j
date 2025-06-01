package io.github.a2ap.core.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Event for task status updates.
 */
@Data
@Builder
public class TaskStatusUpdateEvent implements SendStreamingMessageResponse {

    /**
     * The ID of the task being updated.
     */
    @JsonProperty("taskId")
    private String taskId;

    /**
     * The context id the task is associated with
     */
    @JsonProperty("contextId")
    private String contextId;

    /**
     * kind type
     */
    @JsonProperty("kind")
    private String kind = "status-update";

    /**
     * The new status of the task.
     */
    @JsonProperty("status")
    private TaskStatus status;

    /**
     * Flag indicating if this is the final update for the task.
     */
    @JsonProperty("final")
    private Boolean isFinal;

    /**
     * Optional metadata associated with this update event.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
    
}
