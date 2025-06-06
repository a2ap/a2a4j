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

package io.github.a2ap.core.configuration;

import java.io.Serial;
import java.io.Serializable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for A2A protocol. This class can be extended to include
 * specific properties as needed.
 */
@ConfigurationProperties(prefix = "a2a.server")
@Component
public class A2AServerProperties implements Serializable {

	@Serial
	private static final long serialVersionUID = -608274692651491547L;

	private boolean enabled = true;

	private String name;

	private String description;

	private String version;

	private String url;

	private Capabilities capabilities = new Capabilities();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Capabilities getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Capabilities capabilities) {
		this.capabilities = capabilities;
	}

	public static class Capabilities implements Serializable {

		private static final long serialVersionUID = 2371695651871067858L;

		private boolean streaming = true;

		private boolean pushNotifications = false;

		private boolean stateTransitionHistory = true;

		public boolean isStreaming() {
			return streaming;
		}

		public void setStreaming(boolean streaming) {
			this.streaming = streaming;
		}

		public boolean isPushNotifications() {
			return pushNotifications;
		}

		public void setPushNotifications(boolean pushNotifications) {
			this.pushNotifications = pushNotifications;
		}

		public boolean isStateTransitionHistory() {
			return stateTransitionHistory;
		}

		public void setStateTransitionHistory(boolean stateTransitionHistory) {
			this.stateTransitionHistory = stateTransitionHistory;
		}

	}

}
