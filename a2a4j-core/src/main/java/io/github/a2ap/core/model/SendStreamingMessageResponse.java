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

/**
 * Marker interface for responses emitted by streaming message operations.
 * 
 * This interface serves as a common type for all possible response objects that can be
 * emitted in a streaming context when sending messages to an agent. The streaming
 * response can include various types of objects:
 * 
 * - Message objects: Immediate responses or intermediate messages
 * - Task objects: Task creation or final task state
 * - TaskStatusUpdateEvent objects: Status changes during task execution
 * - TaskArtifactUpdateEvent objects: Artifact updates during task processing
 * 
 * This design enables type-safe handling of heterogeneous streaming responses while
 * providing flexibility for the A2A protocol's asynchronous communication patterns.
 * Clients can use pattern matching or type checking to handle different response
 * types appropriately in the streaming context.
 */
public interface SendStreamingMessageResponse {

}
