/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
     * The kind type of the part, always "text" for TextPart. Required field.
     */
    @JsonProperty("kind")
    private final String kind = "text";

    /**
     * The text content. Required field.
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
        return Objects.equals(kind, textPart.kind) && Objects.equals(text, textPart.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, text);
    }

    @Override
    public String toString() {
        return "TextPart{" + "kind='" + kind + '\'' + ", text='" + text + '\'' + '}';
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
