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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArtifactTest {

    @Test
    void testDefaultConstructor() {
        Artifact artifact = new Artifact();
        assertNotNull(artifact);
        assertNull(artifact.getArtifactId());
        assertNull(artifact.getName());
        assertNull(artifact.getDescription());
        assertNull(artifact.getParts());
        assertNull(artifact.getMetadata());
    }

    @Test
    void testConstructorWithParameters() {
        String artifactId = "artifact-123";
        String name = "Test Artifact";
        String description = "A test artifact";
        List<Part> parts = Arrays.asList(new TextPart("Hello"));
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        Artifact artifact = new Artifact(artifactId, name, description, parts, metadata);
        
        assertEquals(artifactId, artifact.getArtifactId());
        assertEquals(name, artifact.getName());
        assertEquals(description, artifact.getDescription());
        assertEquals(parts, artifact.getParts());
        assertEquals(metadata, artifact.getMetadata());
    }

    @Test
    void testSettersAndGetters() {
        Artifact artifact = new Artifact();
        
        artifact.setArtifactId("artifact-123");
        artifact.setName("Test Artifact");
        artifact.setDescription("A test artifact");
        
        List<Part> parts = Arrays.asList(new TextPart("Content"));
        artifact.setParts(parts);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("priority", "high");
        artifact.setMetadata(metadata);
        
        assertEquals("artifact-123", artifact.getArtifactId());
        assertEquals("Test Artifact", artifact.getName());
        assertEquals("A test artifact", artifact.getDescription());
        assertEquals(parts, artifact.getParts());
        assertEquals(metadata, artifact.getMetadata());
    }

    @Test
    void testBuilder() {
        List<Part> parts = Arrays.asList(new TextPart("Builder content"));
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "builder");

        Artifact artifact = Artifact.builder()
                .artifactId("builder-artifact-123")
                .name("Builder Artifact")
                .description("Built with builder")
                .parts(parts)
                .metadata(metadata)
                .build();

        assertEquals("builder-artifact-123", artifact.getArtifactId());
        assertEquals("Builder Artifact", artifact.getName());
        assertEquals("Built with builder", artifact.getDescription());
        assertEquals(parts, artifact.getParts());
        assertEquals(metadata, artifact.getMetadata());
    }

    @Test
    void testBuilderWithPartialData() {
        Artifact artifact = Artifact.builder()
                .artifactId("partial-artifact-123")
                .name("Partial Artifact")
                .build();

        assertEquals("partial-artifact-123", artifact.getArtifactId());
        assertEquals("Partial Artifact", artifact.getName());
        assertNull(artifact.getDescription());
        assertNull(artifact.getParts());
        assertNull(artifact.getMetadata());
    }

    @Test
    void testEqualsAndHashCode() {
        Artifact artifact1 = Artifact.builder()
                .artifactId("artifact-123")
                .name("Test Artifact")
                .description("A test artifact")
                .build();

        Artifact artifact2 = Artifact.builder()
                .artifactId("artifact-123")
                .name("Test Artifact")
                .description("A test artifact")
                .build();

        Artifact artifact3 = Artifact.builder()
                .artifactId("different-artifact-123")
                .name("Test Artifact")
                .description("A test artifact")
                .build();

        assertEquals(artifact1, artifact2);
        assertNotEquals(artifact1, artifact3);
        assertEquals(artifact1.hashCode(), artifact2.hashCode());
        assertNotEquals(artifact1.hashCode(), artifact3.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        Artifact artifact = Artifact.builder()
                .artifactId("artifact-123")
                .name("Test Artifact")
                .build();
        assertNotEquals(null, artifact);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Artifact artifact = Artifact.builder()
                .artifactId("artifact-123")
                .name("Test Artifact")
                .build();
        assertNotEquals("string", artifact);
    }

    @Test
    void testToString() {
        Artifact artifact = Artifact.builder()
                .artifactId("artifact-123")
                .name("Test Artifact")
                .description("A test artifact")
                .build();

        String toString = artifact.toString();
        
        assertTrue(toString.contains("artifact-123"));
        assertTrue(toString.contains("Test Artifact"));
        assertTrue(toString.contains("A test artifact"));
        assertTrue(toString.contains("Artifact"));
    }

    @Test
    void testToStringWithNullValues() {
        Artifact artifact = new Artifact();
        String toString = artifact.toString();
        
        assertTrue(toString.contains("Artifact"));
        assertTrue(toString.contains("artifactId=null"));
        assertTrue(toString.contains("name=null"));
    }

    @Test
    void testArtifactWithTextParts() {
        TextPart textPart1 = new TextPart("Hello");
        TextPart textPart2 = new TextPart("World");
        
        List<Part> parts = Arrays.asList(textPart1, textPart2);
        
        Artifact artifact = Artifact.builder()
                .artifactId("text-artifact")
                .name("Text Artifact")
                .parts(parts)
                .build();
        
        assertEquals(parts, artifact.getParts());
        assertEquals(2, artifact.getParts().size());
        assertTrue(artifact.getParts().get(0) instanceof TextPart);
        assertTrue(artifact.getParts().get(1) instanceof TextPart);
        assertEquals("Hello", ((TextPart) artifact.getParts().get(0)).getText());
        assertEquals("World", ((TextPart) artifact.getParts().get(1)).getText());
    }

    @Test
    void testArtifactWithFileParts() {
        FileWithUri fileContent = new FileWithUri("document.pdf", "application/pdf", "https://example.com/doc.pdf");
        FilePart filePart = new FilePart(fileContent);
        
        List<Part> parts = Arrays.asList(filePart);
        
        Artifact artifact = Artifact.builder()
                .artifactId("file-artifact")
                .name("File Artifact")
                .parts(parts)
                .build();
        
        assertEquals(parts, artifact.getParts());
        assertEquals(1, artifact.getParts().size());
        assertTrue(artifact.getParts().get(0) instanceof FilePart);
        assertEquals("document.pdf", ((FilePart) artifact.getParts().get(0)).getFile().getName());
        assertEquals("application/pdf", ((FilePart) artifact.getParts().get(0)).getFile().getMimeType());
    }

    @Test
    void testArtifactWithDataParts() {
        DataPart dataPart = new DataPart();
        dataPart.setData("structured-data");
        
        List<Part> parts = Arrays.asList(dataPart);
        
        Artifact artifact = Artifact.builder()
                .artifactId("data-artifact")
                .name("Data Artifact")
                .parts(parts)
                .build();
        
        assertEquals(parts, artifact.getParts());
        assertEquals(1, artifact.getParts().size());
        assertTrue(artifact.getParts().get(0) instanceof DataPart);
        assertEquals("structured-data", ((DataPart) artifact.getParts().get(0)).getData());
    }

    @Test
    void testArtifactWithMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("priority", "high");
        metadata.put("category", "test");
        metadata.put("tags", Arrays.asList("tag1", "tag2"));
        metadata.put("config", Map.of("timeout", 30, "retries", 3));
        
        Artifact artifact = Artifact.builder()
                .artifactId("metadata-artifact")
                .name("Metadata Artifact")
                .metadata(metadata)
                .build();
        
        assertEquals(metadata, artifact.getMetadata());
        assertEquals("high", artifact.getMetadata().get("priority"));
        assertEquals("test", artifact.getMetadata().get("category"));
    }

    @Test
    void testArtifactWithNullParts() {
        Artifact artifact = Artifact.builder()
                .artifactId("null-parts-artifact")
                .name("Null Parts Artifact")
                .parts(null)
                .build();
        
        assertNull(artifact.getParts());
    }

    @Test
    void testArtifactWithEmptyParts() {
        Artifact artifact = Artifact.builder()
                .artifactId("empty-parts-artifact")
                .name("Empty Parts Artifact")
                .parts(Arrays.asList())
                .build();
        
        assertNotNull(artifact.getParts());
        assertEquals(0, artifact.getParts().size());
    }

    @Test
    void testArtifactWithNullMetadata() {
        Artifact artifact = Artifact.builder()
                .artifactId("null-metadata-artifact")
                .name("Null Metadata Artifact")
                .metadata(null)
                .build();
        
        assertNull(artifact.getMetadata());
    }

    @Test
    void testArtifactWithEmptyMetadata() {
        Artifact artifact = Artifact.builder()
                .artifactId("empty-metadata-artifact")
                .name("Empty Metadata Artifact")
                .metadata(new HashMap<>())
                .build();
        
        assertNotNull(artifact.getMetadata());
        assertEquals(0, artifact.getMetadata().size());
    }

    @Test
    void testArtifactWithMixedParts() {
        TextPart textPart = new TextPart("Text content");
        FileWithUri fileContent = new FileWithUri("document.pdf", "application/pdf", "https://example.com/doc.pdf");
        FilePart filePart = new FilePart(fileContent);
        DataPart dataPart = new DataPart();
        dataPart.setData("binary-data");
        
        List<Part> mixedParts = Arrays.asList(textPart, filePart, dataPart);
        
        Artifact artifact = Artifact.builder()
                .artifactId("mixed-artifact")
                .name("Mixed Artifact")
                .parts(mixedParts)
                .build();
        
        assertEquals(3, artifact.getParts().size());
        assertTrue(artifact.getParts().get(0) instanceof TextPart);
        assertTrue(artifact.getParts().get(1) instanceof FilePart);
        assertTrue(artifact.getParts().get(2) instanceof DataPart);
    }

    @Test
    void testArtifactWithLongDescription() {
        String longDescription = "This is a very long description for testing purposes. "
                + "It contains multiple sentences and should be handled properly by the toString method "
                + "and other operations that work with the description field.";
        
        Artifact artifact = Artifact.builder()
                .artifactId("long-desc-artifact")
                .name("Long Description Artifact")
                .description(longDescription)
                .build();
        
        assertEquals(longDescription, artifact.getDescription());
        assertTrue(artifact.toString().contains(longDescription));
    }

    @Test
    void testArtifactWithSpecialCharacters() {
        String specialName = "Artifact with special chars: !@#$%^&*()";
        String specialDescription = "Description with unicode: 中文 Español Français";
        
        Artifact artifact = Artifact.builder()
                .artifactId("special-artifact")
                .name(specialName)
                .description(specialDescription)
                .build();
        
        assertEquals(specialName, artifact.getName());
        assertEquals(specialDescription, artifact.getDescription());
        assertTrue(artifact.toString().contains(specialName));
        assertTrue(artifact.toString().contains(specialDescription));
    }
} 
