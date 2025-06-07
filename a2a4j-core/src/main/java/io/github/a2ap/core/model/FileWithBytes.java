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
 * file bytes content
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileWithBytes extends FileContent {

    /**
     * The base64-encoded content of the file.
     */
    @JsonProperty("bytes")
    private String bytes;

    /**
     * Default constructor.
     */
    public FileWithBytes() {
        super();
    }

    /**
     * Constructor with bytes.
     *
     * @param bytes the base64-encoded content
     */
    public FileWithBytes(String bytes) {
        super();
        this.bytes = bytes;
    }

    /**
     * Constructor with name, MIME type, and bytes.
     *
     * @param name     the file name
     * @param mimeType the MIME type
     * @param bytes    the base64-encoded content
     */
    public FileWithBytes(String name, String mimeType, String bytes) {
        super(name, mimeType);
        this.bytes = bytes;
    }

    /**
     * Gets the base64-encoded content.
     *
     * @return the base64-encoded content
     */
    public String getBytes() {
        return bytes;
    }

    /**
     * Sets the base64-encoded content.
     *
     * @param bytes the base64-encoded content to set
     */
    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        FileWithBytes that = (FileWithBytes) o;
        return Objects.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bytes);
    }

    @Override
    public String toString() {
        return "FileWithBytes{" + "bytes='" + (bytes != null ? "[BASE64]" : "null") + '\'' + ", name='" + getName()
                + '\'' + ", mimeType='" + getMimeType() + '\'' + '}';
    }

    /**
     * Returns a builder for FileWithBytes.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for FileWithBytes.
     */
    public static class Builder {

        private String name;

        private String mimeType;

        private String bytes;

        /**
         * Default constructor.
         */
        private Builder() {
        }

        /**
         * Sets the file name.
         *
         * @param name the file name
         * @return this builder for chaining
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the MIME type.
         *
         * @param mimeType the MIME type
         * @return this builder for chaining
         */
        public Builder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        /**
         * Sets the base64-encoded content.
         *
         * @param bytes the base64-encoded content
         * @return this builder for chaining
         */
        public Builder bytes(String bytes) {
            this.bytes = bytes;
            return this;
        }

        /**
         * Builds a new FileWithBytes instance.
         *
         * @return the built instance
         */
        public FileWithBytes build() {
            return new FileWithBytes(name, mimeType, bytes);
        }

    }

}
