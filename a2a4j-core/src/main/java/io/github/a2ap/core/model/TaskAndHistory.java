package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Objects;

/**
 * Task and history
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskAndHistory {

    private Task task;

    private List<Message> history;

    /**
     * Default constructor.
     */
    public TaskAndHistory() {
    }

    /**
     * Constructor with task and history.
     * 
     * @param task    the task
     * @param history the message history
     */
    public TaskAndHistory(Task task, List<Message> history) {
        this.task = task;
        this.history = history;
    }

    /**
     * Gets the task.
     * 
     * @return the task
     */
    public Task getTask() {
        return task;
    }

    /**
     * Sets the task.
     * 
     * @param task the task to set
     */
    public void setTask(Task task) {
        this.task = task;
    }

    /**
     * Gets the message history.
     * 
     * @return the message history
     */
    public List<Message> getHistory() {
        return history;
    }

    /**
     * Sets the message history.
     * 
     * @param history the message history to set
     */
    public void setHistory(List<Message> history) {
        this.history = history;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaskAndHistory that = (TaskAndHistory) o;
        return Objects.equals(task, that.task) &&
                Objects.equals(history, that.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, history);
    }

    @Override
    public String toString() {
        return "TaskAndHistory{" +
                "task=" + task +
                ", history=" + history +
                '}';
    }

    /**
     * Returns a builder for TaskAndHistory.
     * 
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for TaskAndHistory.
     */
    public static class Builder {
        private Task task;
        private List<Message> history;

        /**
         * Default constructor.
         */
        private Builder() {
        }

        /**
         * Sets the task.
         * 
         * @param task the task
         * @return this builder for chaining
         */
        public Builder task(Task task) {
            this.task = task;
            return this;
        }

        /**
         * Sets the message history.
         * 
         * @param history the message history
         * @return this builder for chaining
         */
        public Builder history(List<Message> history) {
            this.history = history;
            return this;
        }

        /**
         * Builds a new TaskAndHistory instance.
         * 
         * @return the built instance
         */
        public TaskAndHistory build() {
            return new TaskAndHistory(task, history);
        }
    }
}
