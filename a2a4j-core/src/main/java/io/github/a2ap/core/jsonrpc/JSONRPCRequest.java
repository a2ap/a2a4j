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

package io.github.a2ap.core.jsonrpc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a JSON-RPC request.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONRPCRequest {

	/**
	 * The JSON-RPC version, always "2.0". Required field.
	 */
	@JsonProperty("jsonrpc")
	private final String jsonrpc = "2.0";

	/**
	 * The method to be invoked. Required field.
	 */
	@JsonProperty("method")
	private String method;

	/**
	 * The parameters to the method.
	 */
	@JsonProperty("params")
	private Object params;

	/**
	 * The request identifier. Required field.
	 */
	@JsonProperty("id")
	private String id;

	public JSONRPCRequest() {
	}

	public JSONRPCRequest(String method, Object params, String id) {
		this.method = method;
		this.params = params;
		this.id = id;
	}

	public static JSONRPCRequestBuilder builder() {
		return new JSONRPCRequestBuilder();
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		JSONRPCRequest that = (JSONRPCRequest) o;
		return Objects.equals(jsonrpc, that.jsonrpc) && Objects.equals(method, that.method)
				&& Objects.equals(params, that.params) && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(jsonrpc, method, params, id);
	}

	@Override
	public String toString() {
		return "JSONRPCRequest{" + "jsonrpc='" + jsonrpc + '\'' + ", method='" + method + '\'' + ", params=" + params
				+ ", id='" + id + '\'' + '}';
	}

	public static class JSONRPCRequestBuilder {

		private String method;

		private Object params;

		private String id;

		JSONRPCRequestBuilder() {
		}

		public JSONRPCRequestBuilder method(String method) {
			this.method = method;
			return this;
		}

		public JSONRPCRequestBuilder params(Object params) {
			this.params = params;
			return this;
		}

		public JSONRPCRequestBuilder id(String id) {
			this.id = id;
			return this;
		}

		public JSONRPCRequest build() {
			return new JSONRPCRequest(method, params, id);
		}

		@Override
		public String toString() {
			return "JSONRPCRequest.JSONRPCRequestBuilder(method=" + this.method + ", params=" + this.params + ", id="
					+ this.id + ")";
		}

	}

}
