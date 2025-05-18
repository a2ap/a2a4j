package io.github.a2ap.core.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * task context
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskContext {

    /**
     * The current state of the task when the handler is invoked or resumed.
     * Note: This is a snapshot. For the absolute latest state during async operations,
     * the handler might need to reload the task via the store.
     */
    private Task task;

    /**
     * The specific user message that triggered this handler invocation or resumption.
     */
    private Message userMessage;

    /**
     * Function to check if cancellation has been requested for this task.
     * Handlers should ideally check this periodically during long-running operations.
     * True if cancellation has been requested, false otherwise.
     */
    private boolean isCancelled;

    /**
     * The message history associated with the task up to the point the handler is invoked.
     * Optional, as history might not always be available or relevant.
     */
    private List<Message> history;
}
