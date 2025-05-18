package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Artifact {

    /**
     * The unique identifier of the artifact.
     * Required field.
     */
    @JsonProperty("id")
    private String id;

    /**
     * The type of the artifact.
     * Required field.
     */
    @JsonProperty("type")
    private String type;

    /**
     * The MIME type of the artifact.
     * Required field.
     */
    @JsonProperty("mime_type")
    private String mimeType;

    /**
     * The data of the artifact.
     * Required field.
     */
    @JsonProperty("data")
    private Object data;
}
