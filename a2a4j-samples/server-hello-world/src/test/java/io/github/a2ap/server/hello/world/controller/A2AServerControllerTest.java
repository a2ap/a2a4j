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

package io.github.a2ap.server.hello.world.controller;

import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.AgentSkill;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class A2AServerControllerTest {

    private static final String API_KEY = "your-secure-api-key";
    private static final String INVALID_API_KEY = "invalid-key";
    private static final String FORBIDDEN_API_KEY = "forbidden-api-key";
    private static final String EXTENDED_SKILL_ID = "extended-skill-id";
    private static final String EXTENDED_SKILL_NAME = "Extended Skill";
    private static final String EXTENDED_SKILL_DESCRIPTION = "This skill is only visible to authenticated users.";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authenticatedExtendedCard_shouldReturn401_whenNoApiKey() throws Exception {
        mockMvc.perform(get("/a2a/agent/authenticatedExtendedCard")).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "ApiKey realm=\"A2A Server\""));
    }

    @Test
    void authenticatedExtendedCard_shouldReturn401_whenApiKeyEmpty() throws Exception {
        mockMvc.perform(get("/a2a/agent/authenticatedExtendedCard").header("X-API-Key", "")).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "ApiKey realm=\"A2A Server\""));
    }

    @Test
    void authenticatedExtendedCard_shouldReturn401_whenApiKeyInvalid() throws Exception {
        mockMvc.perform(get("/a2a/agent/authenticatedExtendedCard").header("X-API-Key", INVALID_API_KEY)).andExpect(status().isUnauthorized()).andExpect(header().string("WWW-Authenticate", "ApiKey realm=\"A2A Server\""));
    }

    @Test
    void authenticatedExtendedCard_shouldReturn403_whenApiKeyForbidden() throws Exception {
        mockMvc.perform(get("/a2a/agent/authenticatedExtendedCard").header("X-API-Key", FORBIDDEN_API_KEY)).andExpect(status().isForbidden()).andExpect(header().doesNotExist("WWW-Authenticate"));
    }

    @Test
    void authenticatedExtendedCard_shouldReturn200_andValidAgentCard_whenApiKeyValid() throws Exception {
        MvcResult result = mockMvc.perform(get("/a2a/agent/authenticatedExtendedCard").header("X-API-Key", API_KEY).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).isNotEmpty();

        // Verify response is a valid AgentCard object
        AgentCard agentCard = objectMapper.readValue(response, AgentCard.class);
        assertThat(agentCard).isNotNull();

        // Verify AgentCard basic fields
        assertThat(agentCard.getName()).isNotEmpty();
        assertThat(agentCard.getDescription()).isNotEmpty();
        assertThat(agentCard.getUrl()).isNotEmpty();
        assertThat(agentCard.getVersion()).isNotEmpty();
        assertThat(agentCard.getCapabilities()).isNotNull();
        assertThat(agentCard.getDefaultInputModes()).isNotEmpty();
        assertThat(agentCard.getDefaultOutputModes()).isNotEmpty();
        assertThat(agentCard.getSkills()).isNotEmpty();

        // Verify supportsAuthenticatedExtendedCard field
        assertThat(agentCard.isSupportsAuthenticatedExtendedCard()).isTrue();
    }

    @Test
    void authenticatedExtendedCard_shouldContainExtendedSkill_whenApiKeyValid() throws Exception {
        MvcResult result = mockMvc.perform(get("/a2a/agent/authenticatedExtendedCard").header("X-API-Key", API_KEY).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        String response = result.getResponse().getContentAsString();
        AgentCard agentCard = objectMapper.readValue(response, AgentCard.class);

        // Verify contains extended skill
        assertThat(agentCard.getSkills().stream().anyMatch(skill -> EXTENDED_SKILL_ID.equals(skill.getId()))).isTrue();

        // Verify extended skill details
        AgentSkill extendedSkill = agentCard.getSkills().stream().filter(skill -> EXTENDED_SKILL_ID.equals(skill.getId())).findFirst().orElse(null);

        assertThat(extendedSkill).isNotNull();
        assertThat(extendedSkill.getName()).isEqualTo(EXTENDED_SKILL_NAME);
        assertThat(extendedSkill.getDescription()).isEqualTo(EXTENDED_SKILL_DESCRIPTION);
        assertThat(extendedSkill.getTags()).isNotNull();
        assertThat(extendedSkill.getExamples()).isNotNull();
        assertThat(extendedSkill.getInputModes()).isNotNull();
        assertThat(extendedSkill.getOutputModes()).isNotNull();
    }
}
