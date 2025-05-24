package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a task exchanged between agents.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {

    /**
     * The unique identifier of the task.
     * Required field.
     */
    @JsonProperty("id")
    private String id;
    
    /**
     * Optional identifier for the session this task belongs to.
     */
    @JsonProperty("sessionId")
    private String sessionId;

    /**
     * The current status of the task.
     */
    @JsonProperty("status")
    private TaskStatus status;

    /**
     * The artifacts of the task.
     */
    @JsonProperty("artifacts")
    private List<Artifact> artifacts;

    /**
     * The metadata associated with the task.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
}
