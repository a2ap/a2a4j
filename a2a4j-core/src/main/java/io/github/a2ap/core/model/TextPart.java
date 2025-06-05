package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a text part of a message.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextPart extends Part {

    /**
     * The kind type of the part, always "text" for TextPart.
     * Required field.
     */
    @JsonProperty("kind")
    private final String kind = "text";

    /**
     * The text content.
     * Required field.
     */
    @JsonProperty("text")
    private String text;
}
