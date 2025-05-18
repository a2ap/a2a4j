package io.github.a2ap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Parameters for task ID related operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskIdParams {
    /**
     * The ID of the task.
     */
    private String taskId;
}
