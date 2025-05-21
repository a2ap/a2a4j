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
     * The type of the part, always "file" for FilePart.
     * Required field.
     */
    @JsonProperty("type")
    private final String type = "file";

    /**
     * The file content.
     * Required field.
     */
    @JsonProperty("file")
    private FileContent file;
}
