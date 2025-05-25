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
    private String kind = "artifact-update";

    /**
     * The new or updated artifact for the task.
     */
    @JsonProperty("artifact")
    private Artifact artifact;

    /**
     * Flag indicating if this is the final update for the task.
     */
    @JsonProperty("final")
    private Boolean isFinal;

    /**
     * Indicates if this artifact appends to a previous one. Omitted if artifact is a complete artifact
     */
    @JsonProperty("append")
    private Boolean append;

    /**
     * Indicates if this is the last chunk of the artifact. Omitted if artifact is a complete artifact.
     */
    @JsonProperty("lastChunk")
    private Boolean lastChunk;

    /**
     * Optional metadata associated with this update event.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
   
    @Override
    public String getTaskId() {
        return taskId;
    }
}
