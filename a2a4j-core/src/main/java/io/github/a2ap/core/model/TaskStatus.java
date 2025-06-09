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

import java.util.Objects;

/**
 * Represents the status of a task.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStatus implements TaskUpdate {

    /**
     * Completed task status constant.
     */
    public static final TaskStatus COMPLETED = TaskStatus.builder().state(TaskState.COMPLETED).build();

    /**
     * Cancelled task status constant.
     */
    public static final TaskStatus CANCELLED = TaskStatus.builder().state(TaskState.CANCELED).build();

    /**
     * The state of the task. Required field.
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

    public TaskStatus() {
    }

    public TaskStatus(TaskState state, Message message, String timestamp, String error) {
        this.state = state;
        this.message = message;
        this.timestamp = timestamp;
        this.error = error;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaskStatus that = (TaskStatus) o;
        return state == that.state && Objects.equals(message, that.message) && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, message, timestamp, error);
    }

    @Override
    public String toString() {
        return "TaskStatus{" + "state=" + state + ", message=" + message + ", timestamp='" + timestamp + '\''
                + ", error='" + error + '\'' + '}';
    }

    /**
     * Builder for creating instances of TaskStatus.
     */
    public static class Builder {

        private TaskState state;

        private Message message;

        private String timestamp;

        private String error;

        private Builder() {
        }

        public Builder state(TaskState state) {
            this.state = state;
            return this;
        }

        public Builder message(Message message) {
            this.message = message;
            return this;
        }

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        /**
         * Builds a TaskStatus instance with the provided values.
         */
        public TaskStatus build() {
            return new TaskStatus(state, message, timestamp, error);
        }

    }

}
