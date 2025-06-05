package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
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
    @JsonProperty("id")
    private String id;

    /**
     * message metadata
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
}
