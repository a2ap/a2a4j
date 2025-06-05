package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an artifact produced by a task.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Artifact implements TaskUpdate {

    /**
     * artifact id
     */
    @JsonProperty("artifactId")
    private String artifactId;
    
    /**
     * The name of the artifact.
     */
    @JsonProperty("name")
    private String name;

    /**
     * An optional description of the artifact.
     */
    @JsonProperty("description")
    private String description;

    /**
     * The parts that make up the artifact content.
     * Required field.
     */
    @JsonProperty("parts")
    private List<Part> parts;

    /**
     * Optional metadata associated with the artifact.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
}
