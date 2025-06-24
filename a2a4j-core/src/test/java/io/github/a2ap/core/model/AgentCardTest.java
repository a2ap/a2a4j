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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentCardTest {

    @Test
    void testDefaultConstructor() {
        AgentCard agentCard = new AgentCard();
        assertNotNull(agentCard);
        assertNull(agentCard.getId());
        assertNull(agentCard.getName());
        assertNull(agentCard.getDescription());
        assertNull(agentCard.getUrl());
        assertNull(agentCard.getProvider());
        assertNull(agentCard.getVersion());
        assertNull(agentCard.getDocumentationUrl());
        assertNull(agentCard.getCapabilities());
        assertNull(agentCard.getAuthentication());
        assertNull(agentCard.getSecuritySchemes());
        assertNull(agentCard.getSecurity());
        assertNotNull(agentCard.getDefaultInputModes());
        assertEquals(Arrays.asList("text"), agentCard.getDefaultInputModes());
        assertNotNull(agentCard.getDefaultOutputModes());
        assertEquals(Arrays.asList("text"), agentCard.getDefaultOutputModes());
        assertNull(agentCard.getSkills());
        assertFalse(agentCard.isSupportsAuthenticatedExtendedCard());
    }

    @Test
    void testSettersAndGetters() {
        AgentCard agentCard = new AgentCard();
        
        agentCard.setId("custom-id");
        agentCard.setName("Custom Agent");
        agentCard.setDescription("Custom description");
        agentCard.setUrl("https://custom.com/agent");
        agentCard.setVersion("2.0.0");
        agentCard.setDocumentationUrl("https://custom.com/docs");
        agentCard.setSupportsAuthenticatedExtendedCard(true);
        
        assertEquals("custom-id", agentCard.getId());
        assertEquals("Custom Agent", agentCard.getName());
        assertEquals("Custom description", agentCard.getDescription());
        assertEquals("https://custom.com/agent", agentCard.getUrl());
        assertEquals("2.0.0", agentCard.getVersion());
        assertEquals("https://custom.com/docs", agentCard.getDocumentationUrl());
        assertTrue(agentCard.isSupportsAuthenticatedExtendedCard());
    }

    @Test
    void testBuilder() {
        AgentProvider provider = new AgentProvider();
        provider.setOrganization("Builder Provider");
        
        AgentCapabilities capabilities = new AgentCapabilities();
        capabilities.setStreaming(true);
        
        List<AgentSkill> skills = Arrays.asList(new AgentSkill());

        AgentCard agentCard = AgentCard.builder()
                .id("builder-id")
                .name("Builder Agent")
                .description("Built with builder")
                .url("https://builder.com/agent")
                .provider(provider)
                .version("3.0.0")
                .documentationUrl("https://builder.com/docs")
                .capabilities(capabilities)
                .skills(skills)
                .supportsAuthenticatedExtendedCard(true)
                .build();

        assertEquals("builder-id", agentCard.getId());
        assertEquals("Builder Agent", agentCard.getName());
        assertEquals("Built with builder", agentCard.getDescription());
        assertEquals("https://builder.com/agent", agentCard.getUrl());
        assertEquals(provider, agentCard.getProvider());
        assertEquals("3.0.0", agentCard.getVersion());
        assertEquals("https://builder.com/docs", agentCard.getDocumentationUrl());
        assertEquals(capabilities, agentCard.getCapabilities());
        assertEquals(skills, agentCard.getSkills());
        assertTrue(agentCard.isSupportsAuthenticatedExtendedCard());
    }

    @Test
    void testBuilderWithPartialData() {
        AgentCard agentCard = AgentCard.builder()
                .id("partial-id")
                .name("Partial Agent")
                .url("https://partial.com/agent")
                .version("1.0.0")
                .build();

        assertEquals("partial-id", agentCard.getId());
        assertEquals("Partial Agent", agentCard.getName());
        assertEquals("https://partial.com/agent", agentCard.getUrl());
        assertEquals("1.0.0", agentCard.getVersion());
        assertNotNull(agentCard.getDefaultInputModes());
        assertEquals(Arrays.asList("text"), agentCard.getDefaultInputModes());
        assertNotNull(agentCard.getDefaultOutputModes());
        assertEquals(Arrays.asList("text"), agentCard.getDefaultOutputModes());
    }

    @Test
    void testEqualsAndHashCode() {
        AgentCard agentCard1 = AgentCard.builder()
                .id("test-id")
                .name("Test Agent")
                .url("https://test.com/agent")
                .version("1.0.0")
                .build();

        AgentCard agentCard2 = AgentCard.builder()
                .id("test-id")
                .name("Test Agent")
                .url("https://test.com/agent")
                .version("1.0.0")
                .build();

        AgentCard agentCard3 = AgentCard.builder()
                .id("different-id")
                .name("Test Agent")
                .url("https://test.com/agent")
                .version("1.0.0")
                .build();

        assertEquals(agentCard1, agentCard2);
        assertNotEquals(agentCard1, agentCard3);
        assertEquals(agentCard1.hashCode(), agentCard2.hashCode());
        assertNotEquals(agentCard1.hashCode(), agentCard3.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        AgentCard agentCard = AgentCard.builder()
                .id("test-id")
                .name("Test Agent")
                .build();
        assertNotEquals(null, agentCard);
    }

    @Test
    void testEqualsWithDifferentClass() {
        AgentCard agentCard = AgentCard.builder()
                .id("test-id")
                .name("Test Agent")
                .build();
        assertNotEquals("string", agentCard);
    }

    @Test
    void testToString() {
        AgentCard agentCard = AgentCard.builder()
                .id("test-id")
                .name("Test Agent")
                .url("https://test.com/agent")
                .version("1.0.0")
                .build();

        String toString = agentCard.toString();
        
        assertTrue(toString.contains("test-id"));
        assertTrue(toString.contains("Test Agent"));
        assertTrue(toString.contains("https://test.com/agent"));
        assertTrue(toString.contains("1.0.0"));
        assertTrue(toString.contains("AgentCard"));
    }

    @Test
    void testDefaultInputModesAndOutputModes() {
        AgentCard agentCard = new AgentCard();
        
        // Test default values
        assertEquals(Arrays.asList("text"), agentCard.getDefaultInputModes());
        assertEquals(Arrays.asList("text"), agentCard.getDefaultOutputModes());
        
        // Test custom values
        List<String> customInputModes = Arrays.asList("text", "file", "json");
        List<String> customOutputModes = Arrays.asList("text", "file");
        
        agentCard.setDefaultInputModes(customInputModes);
        agentCard.setDefaultOutputModes(customOutputModes);
        
        assertEquals(customInputModes, agentCard.getDefaultInputModes());
        assertEquals(customOutputModes, agentCard.getDefaultOutputModes());
    }

    @Test
    void testSecuritySchemesAndSecurity() {
        AgentCard agentCard = new AgentCard();
        
        Map<String, SecurityScheme> securitySchemes = new HashMap<>();
        SecurityScheme scheme = new SecurityScheme();
        scheme.setType("bearer");
        securitySchemes.put("bearerAuth", scheme);
        
        List<Map<String, List<String>>> security = Arrays.asList(
                Map.of("bearerAuth", Arrays.asList("read", "write"))
        );
        
        agentCard.setSecuritySchemes(securitySchemes);
        agentCard.setSecurity(security);
        
        assertEquals(securitySchemes, agentCard.getSecuritySchemes());
        assertEquals(security, agentCard.getSecurity());
    }
} 
