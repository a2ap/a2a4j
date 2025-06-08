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

import java.util.List;
import java.util.Objects;

/**
 * Represents the execution context for a request being processed by an agent.
 * 
 * This class encapsulates all the contextual information needed for an agent to
 * process a request, including the original request parameters, current task state,
 * and any related tasks. It serves as a comprehensive data container that is passed
 * to agent executors and other processing components.
 * 
 * Key components:
 * - taskId: Unique identifier for the primary task being processed
 * - contextId: Optional session or conversation identifier for grouping related tasks
 * - request: Original message send parameters that initiated the task
 * - task: Current snapshot of the task state (may need reloading for latest state)
 * - relatedTasks: Additional tasks that are part of the same processing context
 * 
 * The context provides a snapshot view of the task state at the time of handler
 * invocation. For operations requiring the absolute latest state, components should
 * reload the task from the persistent store.
 * 
 * This design enables stateful request processing while maintaining clear separation
 * between request parameters, execution state, and related task information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestContext {

    /**
     * current task id
     */
    private String taskId;

    /**
     * context id
     */
    private String contextId;

    /**
     * request params
     */
    private MessageSendParams request;

    /**
     * The current state of the task when the handler is invoked or resumed. Note: This is
     * a snapshot. For the absolute latest state during async operations, the handler
     * might need to reload the task via the store.
     */
    private Task task;

    /**
     * A list of other tasks related to the current request (e.g., for tool use).
     */
    private List<Task> relatedTasks;

    public RequestContext() {
    }

    public RequestContext(String taskId, String contextId, MessageSendParams request, Task task,
                          List<Task> relatedTasks) {
        this.taskId = taskId;
        this.contextId = contextId;
        this.request = request;
        this.task = task;
        this.relatedTasks = relatedTasks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public MessageSendParams getRequest() {
        return request;
    }

    public void setRequest(MessageSendParams request) {
        this.request = request;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<Task> getRelatedTasks() {
        return relatedTasks;
    }

    public void setRelatedTasks(List<Task> relatedTasks) {
        this.relatedTasks = relatedTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RequestContext that = (RequestContext) o;
        return Objects.equals(taskId, that.taskId) && Objects.equals(contextId, that.contextId)
                && Objects.equals(request, that.request) && Objects.equals(task, that.task)
                && Objects.equals(relatedTasks, that.relatedTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, contextId, request, task, relatedTasks);
    }

    @Override
    public String toString() {
        return "RequestContext{" + "taskId='" + taskId + '\'' + ", contextId='" + contextId + '\'' + ", request="
                + request + ", task=" + task + ", relatedTasks=" + relatedTasks + '}';
    }

    /**
     * Builder for creating instances of RequestContext.
     */
    public static class Builder {

        private String taskId;

        private String contextId;

        private MessageSendParams request;

        private Task task;

        private List<Task> relatedTasks;

        Builder() {
        }

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder contextId(String contextId) {
            this.contextId = contextId;
            return this;
        }

        public Builder request(MessageSendParams request) {
            this.request = request;
            return this;
        }

        public Builder task(Task task) {
            this.task = task;
            return this;
        }

        public Builder relatedTasks(List<Task> relatedTasks) {
            this.relatedTasks = relatedTasks;
            return this;
        }

        /**
         * Builds the RequestContext instance.
         */
        public RequestContext build() {
            return new RequestContext(taskId, contextId, request, task, relatedTasks);
        }

        @Override
        public String toString() {
            return "RequestContext.RequestContextBuilder(taskId=" + this.taskId + ", contextId=" + this.contextId
                    + ", request=" + this.request + ", task=" + this.task + ", relatedTasks=" + this.relatedTasks + ")";
        }

    }

}
