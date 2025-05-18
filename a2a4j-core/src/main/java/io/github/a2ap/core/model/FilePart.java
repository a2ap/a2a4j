package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a file part of a message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilePart {

    /**
     * The type of the part, always "file" for FilePart.
     * Required field.
     */
    @JsonProperty("type")
    private final String type = "file";

    /**
     * The name of the file.
     * Required field.
     */
    @JsonProperty("name")
    private String name;

    /**
     * The content of the file.
     * Required field.
     */
    @JsonProperty("content")
    private FileContent content;
}
