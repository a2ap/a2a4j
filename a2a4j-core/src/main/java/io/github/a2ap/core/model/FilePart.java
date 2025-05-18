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
     * The file content.
     * Required field.
     */
    @JsonProperty("file")
    private FileContent file;
    
    public FilePart(String type, java.util.Map<String, Object> metadata, FileContent file) {
        super(type, metadata);
        this.file = file;
    }
}
