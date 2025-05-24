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
     * The index of the artifact in a sequence of artifacts.
     */
    @JsonProperty("index")
    private Integer index;

    /**
     * Indicates if the artifact should be appended to previous artifacts.
     */
    @JsonProperty("append")
    private Boolean append;

    /**
     * Indicates if this is the last chunk of a streamed artifact.
     */
    @JsonProperty("lastChunk")
    private Boolean lastChunk;

    /**
     * Optional metadata associated with the artifact.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;
}
