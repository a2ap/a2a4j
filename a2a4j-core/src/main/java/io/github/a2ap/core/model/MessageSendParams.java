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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Objects;

public class MessageSendParams {

	/**
	 * message context
	 */
	@JsonProperty("message")
	private Message message;

	/**
	 * message others configuration
	 */
	@JsonProperty("configuration")
	private MessageSendConfiguration configuration;

	/**
	 * message metadata
	 */
	@JsonProperty("metadata")
	private Map<String, Object> metadata;

	public MessageSendParams() {
	}

	public MessageSendParams(Message message, MessageSendConfiguration configuration, Map<String, Object> metadata) {
		this.message = message;
		this.configuration = configuration;
		this.metadata = metadata;
	}

	public static MessageSendParamsBuilder builder() {
		return new MessageSendParamsBuilder();
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public MessageSendConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(MessageSendConfiguration configuration) {
		this.configuration = configuration;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MessageSendParams that = (MessageSendParams) o;
		return Objects.equals(message, that.message) && Objects.equals(configuration, that.configuration)
				&& Objects.equals(metadata, that.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message, configuration, metadata);
	}

	@Override
	public String toString() {
		return "MessageSendParams{" + "message=" + message + ", configuration=" + configuration + ", metadata="
				+ metadata + '}';
	}

	public static class MessageSendParamsBuilder {

		private Message message;

		private MessageSendConfiguration configuration;

		private Map<String, Object> metadata;

		MessageSendParamsBuilder() {
		}

		public MessageSendParamsBuilder message(Message message) {
			this.message = message;
			return this;
		}

		public MessageSendParamsBuilder configuration(MessageSendConfiguration configuration) {
			this.configuration = configuration;
			return this;
		}

		public MessageSendParamsBuilder metadata(Map<String, Object> metadata) {
			this.metadata = metadata;
			return this;
		}

		public MessageSendParams build() {
			return new MessageSendParams(message, configuration, metadata);
		}

		@Override
		public String toString() {
			return "MessageSendParams.MessageSendParamsBuilder(message=" + this.message + ", configuration="
					+ this.configuration + ", metadata=" + this.metadata + ")";
		}

	}

}
