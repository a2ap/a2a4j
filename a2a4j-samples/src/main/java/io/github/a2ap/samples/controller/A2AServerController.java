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

package io.github.a2ap.samples.controller;

import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import io.github.a2ap.core.model.AgentCard;
import io.github.a2ap.core.server.A2AServer;
import io.github.a2ap.core.server.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Spring Boot Controller to handle A2A protocol JSON-RPC requests.
 */
@RestController
public class A2AServerController {

	private static final Logger log = LoggerFactory.getLogger(A2AServerController.class);

	private final A2AServer a2aServer;

	private final Dispatcher a2aDispatch;

	public A2AServerController(A2AServer a2aServer, Dispatcher a2aDispatch) {
		this.a2aServer = a2aServer;
		this.a2aDispatch = a2aDispatch;
	}

	@GetMapping(".well-known/agent.json")
	public ResponseEntity<AgentCard> getAgentCard() {
		AgentCard card = a2aServer.getSelfAgentCard();
		return ResponseEntity.ok(card);
	}

	@PostMapping(value = "/a2a/server", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONRPCResponse> handleA2ARequestTask(@RequestBody JSONRPCRequest request) {
		return ResponseEntity.ok(a2aDispatch.dispatch(request));
	}

	@PostMapping(value = "/a2a/server", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<JSONRPCResponse>> handleA2ARequestTaskSubscribe(@RequestBody JSONRPCRequest request) {
		return a2aDispatch.dispatchStream(request)
			.map(event -> ServerSentEvent.<JSONRPCResponse>builder().data(event).event("task-update").build());
	}

}
