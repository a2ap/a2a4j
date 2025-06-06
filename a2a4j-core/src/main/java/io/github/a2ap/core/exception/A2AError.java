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

package io.github.a2ap.core.exception;

import java.util.Objects;

/**
 * Exception class for A2A protocol errors.
 * This exception contains error codes, additional data, and task-specific information
 * to provide comprehensive error context in A2A protocol communications.
 */
public class A2AError extends RuntimeException {

<<<<<<< HEAD
	private int code;
=======
    // Common error codes
    /**
     * Invalid parameters error code
     */
    public static final int INVALID_PARAMS = -32602;
    
    /**
     * Method not found error code
     */
    public static final int METHOD_NOT_FOUND = -32601;
    
    /**
     * Task not found error code
     */
    public static final int TASK_NOT_FOUND = 1001;
    
    /**
     * Task already cancelled error code
     */
    public static final int TASK_CANCELLED = 1002;
    
    /**
     * Agent execution error code
     */
    public static final int AGENT_EXECUTION_ERROR = 1003;
    
    /**
     * Authentication error code
     */
    public static final int AUTHENTICATION_ERROR = 1004;
    
    /**
     * Authorization error code
     */
    public static final int AUTHORIZATION_ERROR = 1005;

    private int code;
>>>>>>> origin/main

	private Object data;

	private String taskId;

	/**
	 * Default constructor
	 */
	public A2AError() {
		super();
	}

	/**
	 * Constructor with message
	 * @param message The error message
	 */
	public A2AError(String message) {
		super(message);
	}

	/**
	 * Constructor with message and cause
	 * @param message The error message
	 * @param cause The cause
	 */
	public A2AError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor with all properties
	 * @param message The error message
	 * @param code The error code
	 * @param data Additional data
	 * @param taskId The task ID
	 */
	public A2AError(String message, int code, Object data, String taskId) {
		super(message);
		this.code = code;
		this.data = data;
		this.taskId = taskId;
	}

	/**
	 * Constructor with all properties and cause
	 * @param message The error message
	 * @param cause The cause
	 * @param code The error code
	 * @param data Additional data
	 * @param taskId The task ID
	 */
	public A2AError(String message, Throwable cause, int code, Object data, String taskId) {
		super(message, cause);
		this.code = code;
		this.data = data;
		this.taskId = taskId;
	}

	/**
	 * Gets the error code
	 * @return The error code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Sets the error code
	 * @param code The error code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Gets the additional data
	 * @return The additional data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Sets the additional data
	 * @param data The additional data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * Gets the task ID
	 * @return The task ID
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * Sets the task ID
	 * @param taskId The task ID to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		A2AError a2AError = (A2AError) o;
		return code == a2AError.code && Objects.equals(data, a2AError.data) && Objects.equals(taskId, a2AError.taskId)
				&& Objects.equals(getMessage(), a2AError.getMessage());
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, data, taskId, getMessage());
	}

	@Override
	public String toString() {
		return "A2AError{" + "code=" + code + ", data=" + data + ", taskId='" + taskId + '\'' + ", message='"
				+ getMessage() + '\'' + '}';
	}

	/**
	 * Returns a builder for A2AError
	 * @return A new builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder class for A2AError
	 */
	public static class Builder {

		private String message;

		private int code;

		private Object data;

		private String taskId;

		private Throwable cause;

		/**
		 * Default constructor
		 */
		private Builder() {
		}

		/**
		 * Sets the error message
		 * @param message The error message
		 * @return This builder for chaining
		 */
		public Builder message(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Sets the error code
		 * @param code The error code
		 * @return This builder for chaining
		 */
		public Builder code(int code) {
			this.code = code;
			return this;
		}

		/**
		 * Sets the additional data
		 * @param data The additional data
		 * @return This builder for chaining
		 */
		public Builder data(Object data) {
			this.data = data;
			return this;
		}

		/**
		 * Sets the task ID
		 * @param taskId The task ID
		 * @return This builder for chaining
		 */
		public Builder taskId(String taskId) {
			this.taskId = taskId;
			return this;
		}

		/**
		 * Sets the cause
		 * @param cause The cause
		 * @return This builder for chaining
		 */
		public Builder cause(Throwable cause) {
			this.cause = cause;
			return this;
		}

		/**
		 * Builds a new A2AError instance
		 * @return The built instance
		 */
		public A2AError build() {
			if (cause != null) {
				return new A2AError(message, cause, code, data, taskId);
			}
			else {
				return new A2AError(message, code, data, taskId);
			}
		}

	}

}
