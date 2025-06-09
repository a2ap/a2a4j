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

package io.github.a2ap.client.hello.world.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * A2A Client Controller for sending messages to an A2A server.
 * This controller handles the `/a2a/client/send` endpoint to send messages
 * in JSON-RPC format to the specified A2A server.
 * <p>
 * The server URL can be configured via the `client.a2a-server-url` property.
 */
@RestController
@RequestMapping("/a2a/client")
public class A2aClientController {
    
    /**
     * The URL of the A2A server to which this client will send messages.
     * Default is set to "http://localhost:8089" for local development.
     */
    @Value("${client.a2a-server-url:http://localhost:8089}")
    private String serverUrl;
    
    /**
     * RestTemplate instance for making HTTP requests to the A2A server.
     * This is used to send messages in JSON-RPC format.
     */
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * Endpoint to send a message to the A2A server.
     * It accepts a JSON payload with the message details.
     * If no body is provided, it uses a default message.
     *
     * @param body The JSON payload containing the message details.
     * @return ResponseEntity with the status and body of the response
     * from the server.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestBody(required = false) final Map<String, Object> body) {
        String url = serverUrl + "/a2a/server";
        Map<String, Object> payload = body != null ? body : defaultPayload();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return ResponseEntity.status(response.getStatusCode())
                .body(response.getBody());
    }
    
    /**
     * Constructs a default JSON-RPC payload for sending a message.
     * This is used when no body is provided in the request.
     *
     * @return A Map representing the default JSON-RPC payload.
     */
    private Map<String, Object> defaultPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("jsonrpc", "2.0");
        payload.put("method", "message/send");
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        Map<String, Object> part = new HashMap<>();
        part.put("type", "text");
        part.put("kind", "text");
        part.put("text", "Hello from client-hello-world!");
        message.put("parts", java.util.Collections.singletonList(part));
        params.put("message", message);
        payload.put("params", params);
        payload.put("id", "client-test-1");
        return payload;
    }
}
