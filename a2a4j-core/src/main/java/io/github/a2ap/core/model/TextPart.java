package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Represents a text part of a message.
 */
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

    /**
     * Default constructor
     */
    public TextPart() {
        super();
    }

    /**
     * Constructor with text
     * 
     * @param text The text content
     */
    public TextPart(String text) {
        super();
        this.text = text;
    }

    /**
     * Gets the kind
     * 
     * @return The kind, always "text"
     */
    public String getKind() {
        return kind;
    }

    /**
     * Gets the text content
     * 
     * @return The text content
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text content
     * 
     * @param text The text content to set
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TextPart textPart = (TextPart) o;
        return Objects.equals(kind, textPart.kind) &&
                Objects.equals(text, textPart.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, text);
    }

    @Override
    public String toString() {
        return "TextPart{" +
                "kind='" + kind + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    /**
     * Returns a builder for TextPart
     * 
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for TextPart
     */
    public static class Builder {
        private String text;

        /**
         * Default constructor
         */
        private Builder() {
        }

        /**
         * Sets the text content
         * 
         * @param text The text content
         * @return This builder for chaining
         */
        public Builder text(String text) {
            this.text = text;
            return this;
        }

        /**
         * Builds a new TextPart instance
         * 
         * @return The built instance
         */
        public TextPart build() {
            return new TextPart(text);
        }
    }
}
