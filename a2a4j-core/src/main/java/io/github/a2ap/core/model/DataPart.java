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
import java.util.Map;
import java.util.Objects;

/**
 * Represents a data part of a message, containing structured data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataPart extends Part {

    /**
     * The kind type of the part, always "data" for DataPart.
     * Required field.
     */
    @JsonProperty("kind")
    private final String kind = "data";

    /**
     * The structured data content.
     * Required field.
     */
    @JsonProperty("data")
    private Object data;

    /**
     * Default constructor.
     */
    public DataPart() {
        super();
    }

    /**
     * Constructor with data.
     * 
     * @param data the structured data content
     */
    public DataPart(Object data) {
        super();
        this.data = data;
    }

    /**
     * Constructor with data and metadata.
     * 
     * @param data     the structured data content
     * @param metadata the metadata
     */
    public DataPart(Object data, Map<String, Object> metadata) {
        super("data", metadata);
        this.data = data;
    }

    /**
     * Gets the kind.
     * 
     * @return the kind, always "data"
     */
    @Override
    public String getKind() {
        return kind;
    }

    /**
     * Gets the structured data content.
     * 
     * @return the structured data content
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the structured data content.
     * 
     * @param data the structured data content to set
     */
    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        DataPart dataPart = (DataPart) o;
        return Objects.equals(kind, dataPart.kind) &&
                Objects.equals(data, dataPart.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kind, data);
    }

    @Override
    public String toString() {
        return "DataPart{" +
                "kind='" + kind + '\'' +
                ", data=" + data +
                ", metadata=" + getMetadata() +
                '}';
    }

    /**
     * Returns a builder for DataPart.
     * 
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for DataPart.
     */
    public static class Builder {
        private Object data;
        private Map<String, Object> metadata;

        /**
         * Default constructor.
         */
        private Builder() {
        }

        /**
         * Sets the structured data content.
         * 
         * @param data the structured data content
         * @return this builder for chaining
         */
        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        /**
         * Sets the metadata.
         * 
         * @param metadata the metadata
         * @return this builder for chaining
         */
        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Builds a new DataPart instance.
         * 
         * @return the built instance
         */
        public DataPart build() {
            return new DataPart(data, metadata);
        }
    }
}
