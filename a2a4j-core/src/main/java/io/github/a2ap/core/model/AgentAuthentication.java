package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
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
     * The authentication schemes supported by the agent.
     * Required field.
     */
    @JsonProperty("schemes")
    private List<String> schemes;

    /**
     * Optional credentials for authentication.
     */
    @JsonProperty("credentials")
    private String credentials;
}
