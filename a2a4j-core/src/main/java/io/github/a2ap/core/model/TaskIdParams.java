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
 * Parameters for task ID related operations.
 */
public class TaskIdParams {

	/**
	 * The ID of the task.
	 */
	@JsonProperty("id")
	private String id;

	/**
	 * message metadata
	 */
	@JsonProperty("metadata")
	private Map<String, Object> metadata;

	/**
	 * Default constructor
	 */
	public TaskIdParams() {
	}

	/**
	 * Constructor with id
	 * @param id The task ID
	 */
	public TaskIdParams(String id) {
		this.id = id;
	}

	/**
	 * Constructor with all fields
	 * @param id The task ID
	 * @param metadata The metadata
	 */
	public TaskIdParams(String id, Map<String, Object> metadata) {
		this.id = id;
		this.metadata = metadata;
	}

	/**
	 * Gets the task ID
	 * @return The task ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the task ID
	 * @param id The task ID to set
	 */
	public void setId(String id) {
		this.id = id;
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
		TaskIdParams that = (TaskIdParams) o;
		return Objects.equals(id, that.id) && Objects.equals(metadata, that.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, metadata);
	}

	@Override
	public String toString() {
		return "TaskIdParams{" + "id='" + id + '\'' + ", metadata=" + metadata + '}';
	}

	/**
	 * Returns a builder for TaskIdParams
	 * @return A new builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder class for TaskIdParams
	 */
	public static class Builder {

		private String id;

		private Map<String, Object> metadata;

		/**
		 * Default constructor
		 */
		private Builder() {
		}

		/**
		 * Sets the task ID
		 * @param id The task ID
		 * @return This builder for chaining
		 */
		public Builder id(String id) {
			this.id = id;
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
		 * Builds a new TaskIdParams instance
		 * @return The built instance
		 */
		public TaskIdParams build() {
			return new TaskIdParams(id, metadata);
		}

	}

}
