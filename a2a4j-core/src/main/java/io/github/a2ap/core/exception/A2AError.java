package io.github.a2ap.core.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Thrown A2AError message
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class A2AError extends RuntimeException {
    
    private int code;
    
    private Object data;
    
    private String taskId;
}
