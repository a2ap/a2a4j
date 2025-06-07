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
 * Marker interface for task update entities in the A2A protocol.
 * 
 * This interface serves as a common type for all task update events that can be
 * applied to modify the state of a task during its lifecycle. Implementations
 * of this interface represent different types of updates that can occur:
 * 
 * - Status updates (via TaskStatusUpdateEvent)
 * - Artifact updates (via TaskArtifactUpdateEvent)
 * - Other custom update types as needed
 * 
 * The interface enables type-safe handling of various update events in the
 * task management system while maintaining flexibility for future extensions.
 */
public interface TaskUpdate {

}
