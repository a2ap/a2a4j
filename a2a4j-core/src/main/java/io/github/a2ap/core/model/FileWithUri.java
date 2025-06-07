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
 * file url content
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileWithUri extends FileContent {

    /**
     * The URI of the file.
     */
    @JsonProperty("uri")
    private String uri;

    /**
     * Default constructor.
     */
    public FileWithUri() {
        super();
    }

    /**
     * Constructor with URI.
     *
     * @param uri the file URI
     */
    public FileWithUri(String uri) {
        super();
        this.uri = uri;
    }

    /**
     * Constructor with name, MIME type, and URI.
     *
     * @param name     the file name
     * @param mimeType the MIME type
     * @param uri      the file URI
     */
    public FileWithUri(String name, String mimeType, String uri) {
        super(name, mimeType);
        this.uri = uri;
    }

    /**
     * Gets the URI of the file.
     *
     * @return the URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the URI of the file.
     *
     * @param uri the URI to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        FileWithUri that = (FileWithUri) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uri);
    }

    @Override
    public String toString() {
        return "FileWithUri{" + "uri='" + uri + '\'' + ", name='" + getName() + '\'' + ", mimeType='" + getMimeType()
                + '\'' + '}';
    }

    /**
     * Returns a builder for FileWithUri.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for FileWithUri.
     */
    public static class Builder {

        private String name;

        private String mimeType;

        private String uri;

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
         * Sets the URI.
         *
         * @param uri the URI
         * @return this builder for chaining
         */
        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        /**
         * Builds a new FileWithUri instance.
         *
         * @return the built instance
         */
        public FileWithUri build() {
            return new FileWithUri(name, mimeType, uri);
        }

    }

}
