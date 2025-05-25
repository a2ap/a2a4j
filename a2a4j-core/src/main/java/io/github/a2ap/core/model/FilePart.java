package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a file part of a message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilePart extends Part {

    /**
     * The kind type of the part, always "file" for FilePart.
     * Required field.
     */
    @JsonProperty("kind")
    private final String kind = "file";

    /**
     * The file content.
     * Required field.
     */
    @JsonProperty("file")
    private FileContent file;
}
