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

import java.util.Objects;

/**
 * Parameters for querying tasks.
 */
public class TaskQueryParams {

    /**
     * The ID of the task.
     */
    private String taskId;

    /**
     * The session ID associated with the task.
     */
    private String sessionId;

    // Add other query parameters as needed based on A2A protocol

    /**
     * Default constructor
     */
    public TaskQueryParams() {
    }

    /**
     * Constructor with taskId
     *
     * @param taskId The task ID
     */
    public TaskQueryParams(String taskId) {
        this.taskId = taskId;
    }

    /**
     * Constructor with all fields
     *
     * @param taskId    The task ID
     * @param sessionId The session ID
     */
    public TaskQueryParams(String taskId, String sessionId) {
        this.taskId = taskId;
        this.sessionId = sessionId;
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
     * Gets the session ID
     *
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID
     *
     * @param sessionId The session ID to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaskQueryParams that = (TaskQueryParams) o;
        return Objects.equals(taskId, that.taskId) && Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, sessionId);
    }

    @Override
    public String toString() {
        return "TaskQueryParams{" + "taskId='" + taskId + '\'' + ", sessionId='" + sessionId + '\'' + '}';
    }

    /**
     * Returns a builder for TaskQueryParams
     *
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for TaskQueryParams
     */
    public static class Builder {

        private String taskId;

        private String sessionId;

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
         * Sets the session ID
         *
         * @param sessionId The session ID
         * @return This builder for chaining
         */
        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        /**
         * Builds a new TaskQueryParams instance
         *
         * @return The built instance
         */
        public TaskQueryParams build() {
            return new TaskQueryParams(taskId, sessionId);
        }

    }

}
