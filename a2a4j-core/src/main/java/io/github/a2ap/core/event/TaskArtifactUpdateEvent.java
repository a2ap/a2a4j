package io.github.a2ap.core.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.a2ap.core.model.Artifact;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Event for task artifact updates.
 */
@Data
@Builder
public class TaskArtifactUpdateEvent implements TaskUpdateEvent {

    /**
     * The ID of the task being updated.
     */
    @JsonProperty("id")
    private String id;

    /**
     * The new or updated artifact for the task.
     */
    @JsonProperty("artifact")
    private Artifact artifact;

    /**
     * Flag indicating if this is the final update for the task.
     */
    @JsonProperty("final")
    private boolean isFinal;

    /**
     * Optional metadata associated with this update event.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
   
    @Override
    public String getTaskId() {
        return id;
    }
}
