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
