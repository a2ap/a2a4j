package io.github.a2ap.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentCapabilitiesTest {

    @Test
    void testDefaultConstructor() {
        AgentCapabilities capabilities = new AgentCapabilities();
        assertNotNull(capabilities);
        assertFalse(capabilities.isStreaming());
        assertFalse(capabilities.isPushNotifications());
        assertFalse(capabilities.isStateTransitionHistory());
    }

    @Test
    void testConstructorWithParameters() {
        AgentCapabilities capabilities = new AgentCapabilities(true, true, true);
        
        assertTrue(capabilities.isStreaming());
        assertTrue(capabilities.isPushNotifications());
        assertTrue(capabilities.isStateTransitionHistory());
    }

    @Test
    void testSettersAndGetters() {
        AgentCapabilities capabilities = new AgentCapabilities();
        
        capabilities.setStreaming(true);
        capabilities.setPushNotifications(true);
        capabilities.setStateTransitionHistory(true);
        
        assertTrue(capabilities.isStreaming());
        assertTrue(capabilities.isPushNotifications());
        assertTrue(capabilities.isStateTransitionHistory());
        
        capabilities.setStreaming(false);
        capabilities.setPushNotifications(false);
        capabilities.setStateTransitionHistory(false);
        
        assertFalse(capabilities.isStreaming());
        assertFalse(capabilities.isPushNotifications());
        assertFalse(capabilities.isStateTransitionHistory());
    }

    @Test
    void testBuilder() {
        AgentCapabilities capabilities = AgentCapabilities.builder()
                .streaming(true)
                .pushNotifications(true)
                .stateTransitionHistory(true)
                .build();
        
        assertTrue(capabilities.isStreaming());
        assertTrue(capabilities.isPushNotifications());
        assertTrue(capabilities.isStateTransitionHistory());
    }

    @Test
    void testBuilderWithPartialData() {
        AgentCapabilities capabilities = AgentCapabilities.builder()
                .streaming(true)
                .stateTransitionHistory(true)
                .build();
        
        assertTrue(capabilities.isStreaming());
        assertFalse(capabilities.isPushNotifications());
        assertTrue(capabilities.isStateTransitionHistory());
    }

    @Test
    void testEqualsAndHashCode() {
        AgentCapabilities capabilities1 = new AgentCapabilities(true, false, true);
        AgentCapabilities capabilities2 = new AgentCapabilities(true, false, true);
        AgentCapabilities capabilities3 = new AgentCapabilities(false, true, false);
        
        assertEquals(capabilities1, capabilities2);
        assertNotEquals(capabilities1, capabilities3);
        assertEquals(capabilities1.hashCode(), capabilities2.hashCode());
        assertNotEquals(capabilities1.hashCode(), capabilities3.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        AgentCapabilities capabilities = new AgentCapabilities(true, false, true);
        assertNotEquals(null, capabilities);
    }

    @Test
    void testEqualsWithDifferentClass() {
        AgentCapabilities capabilities = new AgentCapabilities(true, false, true);
        assertNotEquals("string", capabilities);
    }

    @Test
    void testEqualsWithNullFields() {
        AgentCapabilities capabilities1 = new AgentCapabilities();
        AgentCapabilities capabilities2 = new AgentCapabilities();
        
        assertEquals(capabilities1, capabilities2);
        assertEquals(capabilities1.hashCode(), capabilities2.hashCode());
    }

    @Test
    void testToString() {
        AgentCapabilities capabilities = new AgentCapabilities(true, false, true);
        String toString = capabilities.toString();
        
        assertTrue(toString.contains("streaming=true"));
        assertTrue(toString.contains("pushNotifications=false"));
        assertTrue(toString.contains("stateTransitionHistory=true"));
        assertTrue(toString.contains("AgentCapabilities"));
    }

    @Test
    void testToStringWithAllTrue() {
        AgentCapabilities capabilities = new AgentCapabilities(true, true, true);
        String toString = capabilities.toString();
        
        assertTrue(toString.contains("streaming=true"));
        assertTrue(toString.contains("pushNotifications=true"));
        assertTrue(toString.contains("stateTransitionHistory=true"));
        assertTrue(toString.contains("AgentCapabilities"));
    }

    @Test
    void testToStringWithAllFalse() {
        AgentCapabilities capabilities = new AgentCapabilities(false, false, false);
        String toString = capabilities.toString();
        
        assertTrue(toString.contains("streaming=false"));
        assertTrue(toString.contains("pushNotifications=false"));
        assertTrue(toString.contains("stateTransitionHistory=false"));
        assertTrue(toString.contains("AgentCapabilities"));
    }

    @Test
    void testBuilderWithNullValues() {
        AgentCapabilities capabilities = AgentCapabilities.builder()
                .streaming(false)
                .pushNotifications(false)
                .stateTransitionHistory(false)
                .build();
        
        assertFalse(capabilities.isStreaming());
        assertFalse(capabilities.isPushNotifications());
        assertFalse(capabilities.isStateTransitionHistory());
    }

    @Test
    void testCapabilitiesCombinations() {
        // Test various combinations
        AgentCapabilities streamingOnly = new AgentCapabilities(true, false, false);
        assertTrue(streamingOnly.isStreaming());
        assertFalse(streamingOnly.isPushNotifications());
        assertFalse(streamingOnly.isStateTransitionHistory());
        
        AgentCapabilities pushOnly = new AgentCapabilities(false, true, false);
        assertFalse(pushOnly.isStreaming());
        assertTrue(pushOnly.isPushNotifications());
        assertFalse(pushOnly.isStateTransitionHistory());
        
        AgentCapabilities historyOnly = new AgentCapabilities(false, false, true);
        assertFalse(historyOnly.isStreaming());
        assertFalse(historyOnly.isPushNotifications());
        assertTrue(historyOnly.isStateTransitionHistory());
        
        AgentCapabilities all = new AgentCapabilities(true, true, true);
        assertTrue(all.isStreaming());
        assertTrue(all.isPushNotifications());
        assertTrue(all.isStateTransitionHistory());
        
        AgentCapabilities none = new AgentCapabilities(false, false, false);
        assertFalse(none.isStreaming());
        assertFalse(none.isPushNotifications());
        assertFalse(none.isStateTransitionHistory());
    }
} 