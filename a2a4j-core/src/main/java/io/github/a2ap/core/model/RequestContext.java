package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Objects;

/**
 * task context
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
     * The current state of the task when the handler is invoked or resumed.
     * Note: This is a snapshot. For the absolute latest state during async
     * operations,
     * the handler might need to reload the task via the store.
     */
    private Task task;

    /**
     * A list of other tasks related to the current request (e.g., for tool use).
     */
    private List<Task> relatedTasks;

    public RequestContext() {
    }

    public RequestContext(String taskId, String contextId, MessageSendParams request,
            Task task, List<Task> relatedTasks) {
        this.taskId = taskId;
        this.contextId = contextId;
        this.request = request;
        this.task = task;
        this.relatedTasks = relatedTasks;
    }

    public static RequestContextBuilder builder() {
        return new RequestContextBuilder();
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
        return Objects.equals(taskId, that.taskId) &&
                Objects.equals(contextId, that.contextId) &&
                Objects.equals(request, that.request) &&
                Objects.equals(task, that.task) &&
                Objects.equals(relatedTasks, that.relatedTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, contextId, request, task, relatedTasks);
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "taskId='" + taskId + '\'' +
                ", contextId='" + contextId + '\'' +
                ", request=" + request +
                ", task=" + task +
                ", relatedTasks=" + relatedTasks +
                '}';
    }

    public static class RequestContextBuilder {
        private String taskId;
        private String contextId;
        private MessageSendParams request;
        private Task task;
        private List<Task> relatedTasks;

        RequestContextBuilder() {
        }

        public RequestContextBuilder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public RequestContextBuilder contextId(String contextId) {
            this.contextId = contextId;
            return this;
        }

        public RequestContextBuilder request(MessageSendParams request) {
            this.request = request;
            return this;
        }

        public RequestContextBuilder task(Task task) {
            this.task = task;
            return this;
        }

        public RequestContextBuilder relatedTasks(List<Task> relatedTasks) {
            this.relatedTasks = relatedTasks;
            return this;
        }

        public RequestContext build() {
            return new RequestContext(taskId, contextId, request, task, relatedTasks);
        }

        @Override
        public String toString() {
            return "RequestContext.RequestContextBuilder(taskId=" + this.taskId +
                    ", contextId=" + this.contextId +
                    ", request=" + this.request +
                    ", task=" + this.task +
                    ", relatedTasks=" + this.relatedTasks + ")";
        }
    }
}
