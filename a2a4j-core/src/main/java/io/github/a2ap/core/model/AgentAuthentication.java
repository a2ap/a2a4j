package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents authentication information for an agent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentAuthentication {

    /**
     * The type of authentication required by the agent.
     * Required field.
     */
    @JsonProperty("type")
    private String type;

    /**
     * Optional instructions for authentication.
     */
    @JsonProperty("instructions")
    private String instructions;
}
