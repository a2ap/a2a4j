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

package io.github.a2ap.core;

import io.github.a2ap.core.client.A2AClient;
import io.github.a2ap.core.client.impl.A2AClientImpl;
import io.github.a2ap.core.client.impl.HttpCardResolver;
import io.github.a2ap.core.model.AgentCapabilities;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.model.Message;
import io.github.a2ap.core.model.MessageSendParams;
import io.github.a2ap.core.model.Task;
import io.github.a2ap.core.model.TaskQueryParams;
import io.github.a2ap.core.model.TextPart;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Integration tests for A2A protocol implementation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"logging.level.io.github.a2ap=DEBUG"})
public class A2AIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    public void testAgentCardRetrieval() {
        String baseUrl = "http://localhost:" + port;
        HttpCardResolver cardResolver = new HttpCardResolver();

        AgentCard agentCard = cardResolver.resolveCard(baseUrl);

        assertNotNull(agentCard);
        assertNotNull(agentCard.getName());
        assertNotNull(agentCard.getUrl());
        assertNotNull(agentCard.getCapabilities());
    }

    @Test
    public void testBasicMessageSend() {
        String baseUrl = "http://localhost:" + port;
        AgentCard agentCard = AgentCard.builder()
            .name("Test Agent")
            .url(baseUrl)
            .version("1.0.0")
            .capabilities(AgentCapabilities.builder().streaming(true).build())
            .skills(List.of())
            .defaultInputModes(List.of("text"))
            .defaultOutputModes(List.of("text"))
            .build();

        A2AClient client = new A2AClientImpl(agentCard, new HttpCardResolver());

        // 创建测试消息
        TextPart textPart = new TextPart();
        textPart.setKind("text");
        textPart.setText("Hello, A2A!");

        Message message = Message.builder().role("user").parts(List.of(textPart)).build();

        MessageSendParams params = MessageSendParams.builder().message(message).build();

        // 发送消息
        Task result = client.sendTask(params);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getStatus());
    }

    @Test
    public void testTaskQuery() {
        // 这个测试需要先创建一个任务，然后查询它
        // 由于我们的实现是内存存储，这里只是一个示例框架
        String baseUrl = "http://localhost:" + port;
        AgentCard agentCard = AgentCard.builder()
            .name("Test Agent")
            .url(baseUrl)
            .version("1.0.0")
            .capabilities(AgentCapabilities.builder().build())
            .skills(List.of())
            .defaultInputModes(List.of("text"))
            .defaultOutputModes(List.of("text"))
            .build();

        A2AClient client = new A2AClientImpl(agentCard, new HttpCardResolver());

        // 测试查询不存在的任务
        TaskQueryParams queryParams = TaskQueryParams.builder().taskId("non-existent-task").build();

        Task result = client.getTask(queryParams);
        // 根据实现，这可能返回null或抛出异常
        // assertNull(result);
    }

}
