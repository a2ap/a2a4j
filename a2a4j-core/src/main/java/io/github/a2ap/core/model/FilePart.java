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
     * The kind type of the part, always "file" for FilePart.
     * Required field.
     */
    @JsonProperty("kind")
    private final String kind = "file";

    /**
     * The file content.
     * Required field.
     */
    @JsonProperty("file")
    private FileContent file;

    /**
     * Default constructor
     */
    public FilePart() {
        super();
    }

    /**
     * Constructor with file content
     * 
     * @param file The file content
     */
    public FilePart(FileContent file) {
        super();
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
     * Gets the kind
     * 
     * @return The kind, always "file"
     */
    @Override
    public String getKind() {
        return kind;
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
        return Objects.equals(kind, filePart.kind) &&
                Objects.equals(file, filePart.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kind, file);
    }

    @Override
    public String toString() {
        return "FilePart{" +
                "kind='" + kind + '\'' +
                ", file=" + file +
                ", metadata=" + getMetadata() +
                '}';
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
