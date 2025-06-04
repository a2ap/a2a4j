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
     * Note: This is a snapshot. For the absolute latest state during async operations,
     * the handler might need to reload the task via the store.
     */
    private Task task;

    /**
     * A list of other tasks related to the current request (e.g., for tool use).
     */
    private List<Task> relatedTasks;
}
