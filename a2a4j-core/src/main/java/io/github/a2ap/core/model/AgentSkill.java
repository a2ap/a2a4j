package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a skill that an agent possesses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentSkill {

    /**
     * The unique identifier of the skill.
     * Required field.
     */
    @NotNull
    @JsonProperty("id")
    private String id;

    /**
     * The name of the skill.
     * Required field.
     */
    @NotNull
    @JsonProperty("name")
    private String name;

    /**
     * An optional description of the skill.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Optional tags associated with the skill.
     */
    @JsonProperty("tags")
    private java.util.List<String> tags;

    /**
     * Optional examples of how to use the skill.
     */
    @JsonProperty("examples")
    private java.util.List<String> examples;

    /**
     * Optional input modes supported by the skill.
     */
    @JsonProperty("inputModes")
    private java.util.List<String> inputModes;

    /**
     * Optional output modes supported by the skill.
     */
    @JsonProperty("outputModes")
    private java.util.List<String> outputModes;
}
