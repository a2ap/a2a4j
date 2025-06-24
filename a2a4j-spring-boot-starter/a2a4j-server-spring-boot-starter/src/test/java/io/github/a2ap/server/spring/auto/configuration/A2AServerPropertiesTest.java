package io.github.a2ap.server.spring.auto.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link A2AServerProperties}.
 */
class A2AServerPropertiesTest {

    @Test
    void testDefaultValues() {
        A2AServerProperties properties = new A2AServerProperties();
        assertNotNull(properties);
        assertTrue(properties.isEnabled());
    }

    @Test
    void testSettersAndGetters() {
        A2AServerProperties properties = new A2AServerProperties();
        
        // Test setters and getters
        properties.setId("test-id");
        properties.setName("Test Agent");
        properties.setDescription("Test Description");
        properties.setVersion("1.0.0");
        properties.setUrl("https://test.example.com");
        
        assertEquals("test-id", properties.getId());
        assertEquals("Test Agent", properties.getName());
        assertEquals("Test Description", properties.getDescription());
        assertEquals("1.0.0", properties.getVersion());
        assertEquals("https://test.example.com", properties.getUrl());
    }

    @Test
    void testCapabilities() {
        A2AServerProperties properties = new A2AServerProperties();
        A2AServerProperties.Capabilities capabilities = properties.getCapabilities();
        
        assertNotNull(capabilities);
        assertTrue(capabilities.isStreaming());
        assertTrue(capabilities.isStateTransitionHistory());
    }
} 