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

package io.github.a2ap.core.server;

import io.github.a2ap.core.jsonrpc.JSONRPCRequest;
import io.github.a2ap.core.jsonrpc.JSONRPCResponse;
import reactor.core.publisher.Flux;

/**
 * Interface for dispatching JSON-RPC requests to appropriate handlers.
 * The Dispatcher is responsible for routing incoming JSON-RPC requests to the 
 * correct method handlers and managing both synchronous and streaming responses.
 */
public interface Dispatcher {

    /**
     * Dispatches a JSON-RPC request for synchronous processing.
     * 
     * @param request The JSON-RPC request to be processed
     * @return A JSON-RPC response containing the result or error
     */
    JSONRPCResponse dispatch(JSONRPCRequest request);

    /**
     * Dispatches a JSON-RPC request for streaming/asynchronous processing.
     * This method is used for operations that return multiple responses over time,
     * such as streaming updates or event subscriptions.
     * 
     * @param request The JSON-RPC request to be processed
     * @return A Flux of JSON-RPC responses for streaming results
     */
    Flux<JSONRPCResponse> dispatchStream(JSONRPCRequest request);
} 
