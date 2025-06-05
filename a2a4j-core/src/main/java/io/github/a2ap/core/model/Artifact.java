package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an artifact produced by a task.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Artifact implements TaskUpdate {

    /**
     * artifact id
     */
    @JsonProperty("artifactId")
    private String artifactId;

    /**
     * The name of the artifact.
     */
    @JsonProperty("name")
    private String name;

    /**
     * An optional description of the artifact.
     */
    @JsonProperty("description")
    private String description;

    /**
     * The parts that make up the artifact content.
     * Required field.
     */
    @JsonProperty("parts")
    private List<Part> parts;

    /**
     * Optional metadata associated with the artifact.
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    public Artifact() {
    }

    public Artifact(String artifactId, String name, String description, List<Part> parts,
            Map<String, Object> metadata) {
        this.artifactId = artifactId;
        this.name = name;
        this.description = description;
        this.parts = parts;
        this.metadata = metadata;
    }

    public static ArtifactBuilder builder() {
        return new ArtifactBuilder();
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
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
        Artifact artifact = (Artifact) o;
        return Objects.equals(artifactId, artifact.artifactId) &&
                Objects.equals(name, artifact.name) &&
                Objects.equals(description, artifact.description) &&
                Objects.equals(parts, artifact.parts) &&
                Objects.equals(metadata, artifact.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, name, description, parts, metadata);
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "artifactId='" + artifactId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parts=" + parts +
                ", metadata=" + metadata +
                '}';
    }

    public static class ArtifactBuilder {
        private String artifactId;
        private String name;
        private String description;
        private List<Part> parts;
        private Map<String, Object> metadata;

        ArtifactBuilder() {
        }

        public ArtifactBuilder artifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public ArtifactBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ArtifactBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ArtifactBuilder parts(List<Part> parts) {
            this.parts = parts;
            return this;
        }

        public ArtifactBuilder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Artifact build() {
            return new Artifact(artifactId, name, description, parts, metadata);
        }

        @Override
        public String toString() {
            return "Artifact.ArtifactBuilder(artifactId=" + this.artifactId +
                    ", name=" + this.name +
                    ", description=" + this.description +
                    ", parts=" + this.parts +
                    ", metadata=" + this.metadata + ")";
        }
    }
}
