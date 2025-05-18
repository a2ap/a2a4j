package io.github.a2ap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor;
@AllArgsConstructor
public class TaskQueryParams {
    private String id;
    private String sessionId;
    // Add other query parameters as needed based on A2A protocol
}
