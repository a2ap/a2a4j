package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;

/**
 * Event for task artifact updates.
 */
public class TaskArtifactUpdateEvent implements SendStreamingMessageResponse {

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
     * Indicates if this artifact appends to a previous one. Omitted if artifact is
     * a complete artifact
     */
    @JsonProperty("append")
    private Boolean append;

    /**
     * Indicates if this is the last chunk of the artifact. Omitted if artifact is a
     * complete artifact.
     */
    @JsonProperty("lastChunk")
    private Boolean lastChunk;

    /**
     * Optional metadata associated with this update event.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * Default constructor
     */
    public TaskArtifactUpdateEvent() {
    }

    /**
     * Constructor with all properties
     * 
     * @param taskId    The task ID
     * @param contextId The context ID
     * @param kind      The kind
     * @param artifact  The artifact
     * @param isFinal   Whether this is the final update
     * @param append    Whether this appends to a previous artifact
     * @param lastChunk Whether this is the last chunk
     * @param metadata  Additional metadata
     */
    public TaskArtifactUpdateEvent(String taskId, String contextId, String kind,
            Artifact artifact, Boolean isFinal, Boolean append,
            Boolean lastChunk, Map<String, Object> metadata) {
        this.taskId = taskId;
        this.contextId = contextId;
        this.kind = kind != null ? kind : "artifact-update";
        this.artifact = artifact;
        this.isFinal = isFinal;
        this.append = append;
        this.lastChunk = lastChunk;
        this.metadata = metadata;
    }

    /**
     * Gets the task ID
     * 
     * @return The task ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Sets the task ID
     * 
     * @param taskId The task ID to set
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * Gets the context ID
     * 
     * @return The context ID
     */
    public String getContextId() {
        return contextId;
    }

    /**
     * Sets the context ID
     * 
     * @param contextId The context ID to set
     */
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    /**
     * Gets the kind
     * 
     * @return The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the kind
     * 
     * @param kind The kind to set
     */
    public void setKind(String kind) {
        this.kind = kind != null ? kind : "artifact-update";
    }

    /**
     * Gets the artifact
     * 
     * @return The artifact
     */
    public Artifact getArtifact() {
        return artifact;
    }

    /**
     * Sets the artifact
     * 
     * @param artifact The artifact to set
     */
    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    /**
     * Gets whether this is the final update
     * 
     * @return Whether this is the final update
     */
    public Boolean getIsFinal() {
        return isFinal;
    }

    /**
     * Sets whether this is the final update
     * 
     * @param isFinal Whether this is the final update
     */
    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    /**
     * Gets whether this appends to a previous artifact
     * 
     * @return Whether this appends to a previous artifact
     */
    public Boolean getAppend() {
        return append;
    }

    /**
     * Sets whether this appends to a previous artifact
     * 
     * @param append Whether this appends to a previous artifact
     */
    public void setAppend(Boolean append) {
        this.append = append;
    }

    /**
     * Gets whether this is the last chunk
     * 
     * @return Whether this is the last chunk
     */
    public Boolean getLastChunk() {
        return lastChunk;
    }

    /**
     * Sets whether this is the last chunk
     * 
     * @param lastChunk Whether this is the last chunk
     */
    public void setLastChunk(Boolean lastChunk) {
        this.lastChunk = lastChunk;
    }

    /**
     * Gets the metadata
     * 
     * @return The metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata
     * 
     * @param metadata The metadata to set
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaskArtifactUpdateEvent that = (TaskArtifactUpdateEvent) o;
        return Objects.equals(taskId, that.taskId) &&
                Objects.equals(contextId, that.contextId) &&
                Objects.equals(kind, that.kind) &&
                Objects.equals(artifact, that.artifact) &&
                Objects.equals(isFinal, that.isFinal) &&
                Objects.equals(append, that.append) &&
                Objects.equals(lastChunk, that.lastChunk) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, contextId, kind, artifact, isFinal, append, lastChunk, metadata);
    }

    @Override
    public String toString() {
        return "TaskArtifactUpdateEvent{" +
                "taskId='" + taskId + '\'' +
                ", contextId='" + contextId + '\'' +
                ", kind='" + kind + '\'' +
                ", artifact=" + artifact +
                ", isFinal=" + isFinal +
                ", append=" + append +
                ", lastChunk=" + lastChunk +
                ", metadata=" + metadata +
                '}';
    }

    /**
     * Returns a builder for TaskArtifactUpdateEvent
     * 
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for TaskArtifactUpdateEvent
     */
    public static class Builder {
        private String taskId;
        private String contextId;
        private String kind = "artifact-update";
        private Artifact artifact;
        private Boolean isFinal;
        private Boolean append;
        private Boolean lastChunk;
        private Map<String, Object> metadata;

        /**
         * Default constructor
         */
        private Builder() {
        }

        /**
         * Sets the task ID
         * 
         * @param taskId The task ID
         * @return This builder for chaining
         */
        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        /**
         * Sets the context ID
         * 
         * @param contextId The context ID
         * @return This builder for chaining
         */
        public Builder contextId(String contextId) {
            this.contextId = contextId;
            return this;
        }

        /**
         * Sets the kind
         * 
         * @param kind The kind
         * @return This builder for chaining
         */
        public Builder kind(String kind) {
            this.kind = kind != null ? kind : "artifact-update";
            return this;
        }

        /**
         * Sets the artifact
         * 
         * @param artifact The artifact
         * @return This builder for chaining
         */
        public Builder artifact(Artifact artifact) {
            this.artifact = artifact;
            return this;
        }

        /**
         * Sets whether this is the final update
         * 
         * @param isFinal Whether this is the final update
         * @return This builder for chaining
         */
        public Builder isFinal(Boolean isFinal) {
            this.isFinal = isFinal;
            return this;
        }

        /**
         * Sets whether this appends to a previous artifact
         * 
         * @param append Whether this appends to a previous artifact
         * @return This builder for chaining
         */
        public Builder append(Boolean append) {
            this.append = append;
            return this;
        }

        /**
         * Sets whether this is the last chunk
         * 
         * @param lastChunk Whether this is the last chunk
         * @return This builder for chaining
         */
        public Builder lastChunk(Boolean lastChunk) {
            this.lastChunk = lastChunk;
            return this;
        }

        /**
         * Sets the metadata
         * 
         * @param metadata The metadata
         * @return This builder for chaining
         */
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Builds a new TaskArtifactUpdateEvent instance
         * 
         * @return The built instance
         */
        public TaskArtifactUpdateEvent build() {
            return new TaskArtifactUpdateEvent(taskId, contextId, kind, artifact, isFinal, append, lastChunk, metadata);
        }
    }
}
