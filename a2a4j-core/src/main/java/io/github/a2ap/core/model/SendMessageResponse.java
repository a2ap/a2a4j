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
 * Marker interface for responses returned by synchronous message sending operations.
 * 
 * This interface serves as a common type for all possible response objects that can be
 * returned when sending a message to an agent in a non-streaming context. The response
 * can be either:
 * 
 * - A Message object: When the agent provides an immediate response
 * - A Task object: When the agent creates a task to handle the request asynchronously
 * 
 * This design allows for flexible response handling while maintaining type safety
 * in the A2A protocol implementation. Clients can check the actual type of the
 * response to determine the appropriate handling strategy.
 */
public interface SendMessageResponse {

}
