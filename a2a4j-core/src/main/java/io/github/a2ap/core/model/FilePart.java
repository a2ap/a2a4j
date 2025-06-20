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
import java.util.Map;

/**
 * Represents a file part of a message.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilePart extends Part {

    /**
     * The file content. Required field.
     */
    @JsonProperty("file")
    private FileContent file;

    /**
     * Default constructor
     */
    public FilePart() {
        super("file");
    }

    /**
     * Constructor with file content
     *
     * @param file The file content
     */
    public FilePart(FileContent file) {
        super("file");
        this.file = file;
    }

    /**
     * Constructor with file content and metadata
     *
     * @param file     The file content
     * @param metadata The metadata
     */
    public FilePart(FileContent file, Map<String, Object> metadata) {
        super("file", metadata);
        this.file = file;
    }

    /**
     * Gets the file content
     *
     * @return The file content
     */
    public FileContent getFile() {
        return file;
    }

    /**
     * Sets the file content
     *
     * @param file The file content to set
     */
    public void setFile(FileContent file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        FilePart filePart = (FilePart) o;
        return Objects.equals(file, filePart.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getKind(), file);
    }

    @Override
    public String toString() {
        return "FilePart{" + "kind='" + getKind() + '\'' + ", file=" + file + ", metadata=" + getMetadata() + '}';
    }

    /**
     * Returns a builder for FilePart
     *
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for FilePart
     */
    public static class Builder {

        private FileContent file;

        private Map<String, Object> metadata;

        /**
         * Default constructor
         */
        private Builder() {
        }

        /**
         * Sets the file content
         *
         * @param file The file content
         * @return This builder for chaining
         */
        public Builder file(FileContent file) {
            this.file = file;
            return this;
        }

        /**
         * Sets the metadata
         *
         * @param metadata The metadata
         * @return This builder for chaining
         */
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Builds a new FilePart instance
         *
         * @return The built instance
         */
        public FilePart build() {
            return new FilePart(file, metadata);
        }

    }

}
