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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.Objects;

/**
 * Abstract base class for different types of content parts in the A2A protocol.
 * 
 * Parts represent discrete pieces of content that can be included in messages or artifacts.
 * This class provides a common structure and polymorphic behavior for handling various
 * content types in a type-safe manner.
 * 
 * Supported part types:
 * - TextPart: Plain text content
 * - FilePart: File-based content with metadata
 * - DataPart: Structured data content
 * 
 * The class uses Jackson's polymorphic serialization to maintain type information
 * during JSON serialization/deserialization, enabling proper reconstruction of
 * specific part types from generic Part references.
 * 
 * Each part can optionally include metadata for additional context or processing hints.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({@JsonSubTypes.Type(value = TextPart.class, name = "text"),
        @JsonSubTypes.Type(value = FilePart.class, name = "file"),
        @JsonSubTypes.Type(value = DataPart.class, name = "data")})
public abstract class Part {

    /**
     * The kind type of the part. Required field.
     */
    @JsonProperty("kind")
    private String kind;

    /**
     * Optional metadata associated with the part.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    protected Part() {
    }

    protected Part(String kind) {
        this.kind = kind;
    }

    protected Part(String kind, Map<String, Object> metadata) {
        this.kind = kind;
        this.metadata = metadata;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Part part = (Part) o;
        return Objects.equals(kind, part.kind) && Objects.equals(metadata, part.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, metadata);
    }

    @Override
    public String toString() {
        return "Part{" + "kind='" + kind + '\'' + ", metadata=" + metadata + '}';
    }

}
