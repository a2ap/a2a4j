package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the content of a file.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileContent {

    /**
     * The MIME type of the file.
     * Required field.
     */
    @JsonProperty("mime_type")
    private String mimeType;

    /**
     * The base64-encoded content of the file.
     * Required field.
     */
    @JsonProperty("data")
    private String data;
}
