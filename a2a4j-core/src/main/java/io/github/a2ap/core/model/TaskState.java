package io.github.a2ap.core.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of possible states for a task.
 */
public enum TaskState {
    PENDING("pending"),
    RUNNING("running"),
    COMPLETED("completed"),
    FAILED("failed"),
    CANCELED("canceled");

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
