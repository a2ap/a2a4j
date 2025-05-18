package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
     * The name of the skill.
     * Required field.
     */
    @JsonProperty("name")
    private String name;

    /**
     * A description of the skill.
     * Required field.
     */
    @JsonProperty("description")
    private String description;
}
