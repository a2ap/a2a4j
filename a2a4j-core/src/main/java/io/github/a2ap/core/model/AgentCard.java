package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an Agent Card that describes the capabilities and metadata of an
 * agent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentCard {    
    /**
     * The unique identifier of the agent.
     */
    private String id;
    /**
     * The name of the agent.
     */
    private String name;

    /**
     * An optional description of the agent.
     */
    private String description;

    /**
     * The base URL endpoint for interacting with the agent.
     */
    private String url;

    /**
     * Information about the provider of the agent.
     */
    private AgentProvider provider;

    /**
     * The version identifier for the agent or its API.
     */
    private String version;

    /**
     * An optional URL pointing to the agent's documentation.
     */
    private String documentationUrl;

    /**
     * The capabilities supported by the agent.
     */
    private AgentCapabilities capabilities;

    /**
     * Authentication details required to interact with the agent.
     */
    private AgentAuthentication authentication;

    /**
     * Default input modes supported by the agent (e.g., 'text', 'file', 'json').
     * Defaults to ["text"] if not specified.
     */
    @Builder.Default
    private List<String> defaultInputModes = List.of("text");

    /**
     * Default output modes supported by the agent (e.g., 'text', 'file', 'json').
     * Defaults to ["text"] if not specified.
     */
    @Builder.Default
    private List<String> defaultOutputModes = List.of("text");

    /**
     * List of specific skills offered by the agent.
     */
    private List<AgentSkill> skills;
}
