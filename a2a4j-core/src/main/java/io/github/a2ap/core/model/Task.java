/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a task exchanged between agents.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task implements SendMessageResponse, SendStreamingMessageResponse {

    /**
     * The unique identifier of the task.
     * Required field.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Optional identifier for the session this task belongs to.
     */
    @JsonProperty("contextId")
    private String contextId;

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
     * An optional array of recent messages exchanged within this task,
     * ordered chronologically (oldest first).
     * This history is included if requested by the client via the `historyLength`
     * parameter
     * in `TaskSendParams` or `TaskQueryParams`.
     */
    @JsonProperty("history")
    private List<Message> history;

    /**
     * The metadata associated with the task.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    public Task() {
    }

    public Task(String id, String contextId, TaskStatus status, List<Artifact> artifacts,
            List<Message> history, Map<String, Object> metadata) {
        this.id = id;
        this.contextId = contextId;
        this.status = status;
        this.artifacts = artifacts;
        this.history = history;
        this.metadata = metadata;
    }

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public List<Message> getHistory() {
        return history;
    }

    public void setHistory(List<Message> history) {
        this.history = history;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(contextId, task.contextId) &&
                Objects.equals(status, task.status) &&
                Objects.equals(artifacts, task.artifacts) &&
                Objects.equals(history, task.history) &&
                Objects.equals(metadata, task.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contextId, status, artifacts, history, metadata);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", contextId='" + contextId + '\'' +
                ", status=" + status +
                ", artifacts=" + artifacts +
                ", history=" + history +
                ", metadata=" + metadata +
                '}';
    }

    public static class TaskBuilder {
        private String id;
        private String contextId;
        private TaskStatus status;
        private List<Artifact> artifacts;
        private List<Message> history;
        private Map<String, Object> metadata;

        TaskBuilder() {
        }

        public TaskBuilder id(String id) {
            this.id = id;
            return this;
        }

        public TaskBuilder contextId(String contextId) {
            this.contextId = contextId;
            return this;
        }

        public TaskBuilder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public TaskBuilder artifacts(List<Artifact> artifacts) {
            this.artifacts = artifacts;
            return this;
        }

        public TaskBuilder history(List<Message> history) {
            this.history = history;
            return this;
        }

        public TaskBuilder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Task build() {
            return new Task(id, contextId, status, artifacts, history, metadata);
        }

        @Override
        public String toString() {
            return "Task.TaskBuilder(id=" + this.id +
                    ", contextId=" + this.contextId +
                    ", status=" + this.status +
                    ", artifacts=" + this.artifacts +
                    ", history=" + this.history +
                    ", metadata=" + this.metadata + ")";
        }
    }
}
