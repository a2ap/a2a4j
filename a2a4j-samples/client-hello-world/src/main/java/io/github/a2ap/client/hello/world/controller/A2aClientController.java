package io.github.a2ap.client.hello.world.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/a2a/client")
public class A2aClientController {

  @Value("${client.a2a-server-url:http://localhost:8089}")
  private String serverUrl;

  private final RestTemplate restTemplate = new RestTemplate();

  @PostMapping("/send")
  public ResponseEntity<String> sendMessage(@RequestBody(required = false) Map<String, Object> body) {
    String url = serverUrl + "/a2a/server";
    Map<String, Object> payload = body != null ? body : defaultPayload();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
  }

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