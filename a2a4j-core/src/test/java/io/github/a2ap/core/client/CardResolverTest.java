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

package io.github.a2ap.core.client;

import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.AgentCapabilities;
import io.github.a2ap.core.model.AgentSkill;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CardResolverTest {

    @Test
    void testMockCardResolver() {
        MockCardResolver resolver = new MockCardResolver();
        
        AgentCard card = resolver.resolveCard();
        
        assertNotNull(card);
        assertEquals("mock-agent", card.getId());
        assertEquals("Mock Agent", card.getName());
        assertEquals("A mock agent for testing", card.getDescription());
        assertEquals("https://mock.com/agent", card.getUrl());
        assertEquals("1.0.0", card.getVersion());
        assertNotNull(card.getCapabilities());
        assertNotNull(card.getSkills());
    }

    @Test
    void testCardResolverWithCustomData() {
        CustomCardResolver resolver = new CustomCardResolver("custom-id", "Custom Agent");
        
        AgentCard card = resolver.resolveCard();
        
        assertNotNull(card);
        assertEquals("custom-id", card.getId());
        assertEquals("Custom Agent", card.getName());
        assertEquals("https://custom.com/agent", card.getUrl());
        assertEquals("2.0.0", card.getVersion());
    }

    @Test
    void testCardResolverWithNullData() {
        NullCardResolver resolver = new NullCardResolver();
        
        AgentCard card = resolver.resolveCard();
        
        assertNull(card);
    }

    // Mock implementation for testing
    static class MockCardResolver implements CardResolver {
        @Override
        public AgentCard resolveCard() {
            AgentCard card = new AgentCard();
            card.setId("mock-agent");
            card.setName("Mock Agent");
            card.setDescription("A mock agent for testing");
            card.setUrl("https://mock.com/agent");
            card.setVersion("1.0.0");
            
            AgentCapabilities capabilities = new AgentCapabilities();
            capabilities.setStreaming(true);
            card.setCapabilities(capabilities);
            
            AgentSkill skill = new AgentSkill();
            skill.setName("mock-skill");
            skill.setDescription("A mock skill");
            card.setSkills(Arrays.asList(skill));
            
            return card;
        }
    }

    // Custom implementation for testing
    static class CustomCardResolver implements CardResolver {
        private final String id;
        private final String name;

        public CustomCardResolver(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public AgentCard resolveCard() {
            AgentCard card = new AgentCard();
            card.setId(id);
            card.setName(name);
            card.setDescription("A custom agent for testing");
            card.setUrl("https://custom.com/agent");
            card.setVersion("2.0.0");
            
            AgentCapabilities capabilities = new AgentCapabilities();
            capabilities.setStreaming(false);
            card.setCapabilities(capabilities);
            
            return card;
        }
    }

    // Null implementation for testing
    static class NullCardResolver implements CardResolver {
        @Override
        public AgentCard resolveCard() {
            return null;
        }
    }
} 
