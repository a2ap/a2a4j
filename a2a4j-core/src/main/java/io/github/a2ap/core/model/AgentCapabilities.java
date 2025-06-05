package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the capabilities supported by an agent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentCapabilities {
    /**
     * Indicates if the agent supports streaming responses.
     */
    @Builder.Default
    private boolean streaming = false;

    /**
     * Indicates if the agent supports push notifications.
     */
    @Builder.Default
    private boolean pushNotifications = false;

    /**
     * Indicates if the agent supports state transition history.
     */
    @Builder.Default
    private boolean stateTransitionHistory = false;
}
