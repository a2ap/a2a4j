package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Represents the content of a file.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class FileContent {

    /**
     * The name of the file.
     */
    @JsonProperty("name")
    private String name;

    /**
     * The MIME type of the file.
     */
    @JsonProperty("mimeType")
    private String mimeType;

    /**
     * Default constructor.
     */
    public FileContent() {
    }

    /**
     * Constructor with name and MIME type.
     * 
     * @param name     the file name
     * @param mimeType the MIME type
     */
    public FileContent(String name, String mimeType) {
        this.name = name;
        this.mimeType = mimeType;
    }

    /**
     * Gets the file name.
     * 
     * @return the file name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the file name.
     * 
     * @param name the file name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the MIME type.
     * 
     * @return the MIME type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the MIME type.
     * 
     * @param mimeType the MIME type to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FileContent that = (FileContent) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mimeType);
    }

    @Override
    public String toString() {
        return "FileContent{" +
                "name='" + name + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
