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

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of possible states for a task in the A2A protocol lifecycle.
 * 
 * This enum defines all valid states that a task can be in during its execution,
 * from initial submission through completion or termination. The states follow
 * a logical progression and help clients understand the current status of their
 * requests.
 * 
 * State transitions typically follow this pattern:
 * SUBMITTED → WORKING → (INPUT_REQUIRED ↔ WORKING) → COMPLETED/FAILED/CANCELED
 * 
 * Special states like REJECTED, AUTH_REQUIRED, and UNKNOWN handle exceptional
 * cases in the task lifecycle.
 */
public enum TaskState {

    /**
     * Task has been submitted but not yet started processing.
     */
    SUBMITTED("submitted"), 
    
    /**
     * Task is currently being processed by the agent.
     */
    WORKING("working"), 
    
    /**
     * Task execution is paused, waiting for additional input from the client.
     */
    INPUT_REQUIRED("input-required"), 
    
    /**
     * Task has been successfully completed.
     */
    COMPLETED("completed"),
    
    /**
     * Task execution failed due to an error.
     */
    FAILED("failed"), 
    
    /**
     * Task was canceled by client request or system intervention.
     */
    CANCELED("canceled"), 
    
    /**
     * Task was rejected by the agent (e.g., invalid parameters, unsupported operation).
     */
    REJECTED("rejected"), 
    
    /**
     * Task requires authentication before it can proceed.
     */
    AUTH_REQUIRED("auth-required"), 
    
    /**
     * Task state is unknown or could not be determined.
     */
    UNKNOWN("unknown");

    private final String value;

    TaskState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static TaskState fromValue(String value) {
        for (TaskState state : TaskState.values()) {
            if (state.value.equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown TaskState value: " + value);
    }

}
