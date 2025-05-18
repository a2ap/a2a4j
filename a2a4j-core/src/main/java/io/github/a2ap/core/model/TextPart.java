package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a text part of a message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextPart {

    /**
     * The type of the part, always "text" for TextPart.
     * Required field.
     */
    @JsonProperty("type")
    private final String type = "text";

    /**
     * The text content.
     * Required field.
     */
    @JsonProperty("text")
    private String text;
}
