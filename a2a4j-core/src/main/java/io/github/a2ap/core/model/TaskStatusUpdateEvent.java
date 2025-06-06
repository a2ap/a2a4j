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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Objects;

/**
 * Event for task status updates.
 */
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

	/**
	 * Default constructor
	 */
	public TaskStatusUpdateEvent() {
	}

	/**
	 * Constructor with all properties
	 * @param taskId The task ID
	 * @param contextId The context ID
	 * @param kind The kind
	 * @param status The task status
	 * @param isFinal Whether this is the final update
	 * @param metadata Additional metadata
	 */
	public TaskStatusUpdateEvent(String taskId, String contextId, String kind, TaskStatus status, Boolean isFinal,
			Map<String, Object> metadata) {
		this.taskId = taskId;
		this.contextId = contextId;
		this.kind = kind != null ? kind : "status-update";
		this.status = status;
		this.isFinal = isFinal;
		this.metadata = metadata;
	}

	/**
	 * Gets the task ID
	 * @return The task ID
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * Sets the task ID
	 * @param taskId The task ID to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * Gets the context ID
	 * @return The context ID
	 */
	public String getContextId() {
		return contextId;
	}

	/**
	 * Sets the context ID
	 * @param contextId The context ID to set
	 */
	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	/**
	 * Gets the kind
	 * @return The kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * Sets the kind
	 * @param kind The kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind != null ? kind : "status-update";
	}

	/**
	 * Gets the task status
	 * @return The task status
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * Sets the task status
	 * @param status The task status to set
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	/**
	 * Gets whether this is the final update
	 * @return Whether this is the final update
	 */
	public Boolean getIsFinal() {
		return isFinal;
	}

	/**
	 * Sets whether this is the final update
	 * @param isFinal Whether this is the final update
	 */
	public void setIsFinal(Boolean isFinal) {
		this.isFinal = isFinal;
	}

	/**
	 * Gets the metadata
	 * @return The metadata
	 */
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	/**
	 * Sets the metadata
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
		TaskStatusUpdateEvent that = (TaskStatusUpdateEvent) o;
		return Objects.equals(taskId, that.taskId) && Objects.equals(contextId, that.contextId)
				&& Objects.equals(kind, that.kind) && Objects.equals(status, that.status)
				&& Objects.equals(isFinal, that.isFinal) && Objects.equals(metadata, that.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(taskId, contextId, kind, status, isFinal, metadata);
	}

	@Override
	public String toString() {
		return "TaskStatusUpdateEvent{" + "taskId='" + taskId + '\'' + ", contextId='" + contextId + '\'' + ", kind='"
				+ kind + '\'' + ", status=" + status + ", isFinal=" + isFinal + ", metadata=" + metadata + '}';
	}

	/**
	 * Returns a builder for TaskStatusUpdateEvent
	 * @return A new builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder class for TaskStatusUpdateEvent
	 */
	public static class Builder {

		private String taskId;

		private String contextId;

		private String kind = "status-update";

		private TaskStatus status;

		private Boolean isFinal;

		private Map<String, Object> metadata;

		/**
		 * Default constructor
		 */
		private Builder() {
		}

		/**
		 * Sets the task ID
		 * @param taskId The task ID
		 * @return This builder for chaining
		 */
		public Builder taskId(String taskId) {
			this.taskId = taskId;
			return this;
		}

		/**
		 * Sets the context ID
		 * @param contextId The context ID
		 * @return This builder for chaining
		 */
		public Builder contextId(String contextId) {
			this.contextId = contextId;
			return this;
		}

		/**
		 * Sets the kind
		 * @param kind The kind
		 * @return This builder for chaining
		 */
		public Builder kind(String kind) {
			this.kind = kind != null ? kind : "status-update";
			return this;
		}

		/**
		 * Sets the task status
		 * @param status The task status
		 * @return This builder for chaining
		 */
		public Builder status(TaskStatus status) {
			this.status = status;
			return this;
		}

		/**
		 * Sets whether this is the final update
		 * @param isFinal Whether this is the final update
		 * @return This builder for chaining
		 */
		public Builder isFinal(Boolean isFinal) {
			this.isFinal = isFinal;
			return this;
		}

		/**
		 * Sets the metadata
		 * @param metadata The metadata
		 * @return This builder for chaining
		 */
		public Builder metadata(Map<String, Object> metadata) {
			this.metadata = metadata;
			return this;
		}

		/**
		 * Builds a new TaskStatusUpdateEvent instance
		 * @return The built instance
		 */
		public TaskStatusUpdateEvent build() {
			return new TaskStatusUpdateEvent(taskId, contextId, kind, status, isFinal, metadata);
		}

	}

}
