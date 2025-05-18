package io.github.a2ap.core.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Parameters for querying tasks.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
