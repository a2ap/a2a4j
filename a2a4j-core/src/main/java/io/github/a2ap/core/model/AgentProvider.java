package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents information about the provider of an agent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentProvider {
    /**
     * The name of the organization providing the agent.
     */
    private String organization;

    /**
     * An optional URL pointing to the provider's website or information.
     */
    private String url;
}
