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

package io.github.a2ap.core.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for JSON serialization and deserialization operations using Jackson.
 * 
 * This class provides a centralized, thread-safe JSON processing facility with pre-configured
 * ObjectMapper settings optimized for the A2A protocol. It handles common JSON operations
 * including object serialization, deserialization with type safety, and JSON validation.
 * 
 * Key features:
 * - Ignores unknown properties during deserialization for backward compatibility
 * - Supports Java 8 time types through JavaTimeModule
 * - Provides null-safe operations with proper error handling
 * - Includes JSON string validation utilities
 * 
 * All methods are static and thread-safe, making this class suitable for concurrent usage
 * across the A2A framework.
 */
public final class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtil() {
    }

    static {
        OBJECT_MAPPER
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .registerModule(new JavaTimeModule());
    }

    public static String toJson(Object source) {
        if (source == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        if (StringUtil.isNullOrEmpty(jsonStr)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T fromJson(String jsonStr, TypeReference<T> type) {
        if (StringUtil.isNullOrEmpty(jsonStr)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(jsonStr, type);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
    
    public static JsonNode fromJson(String jsonStr) {
        if (StringUtil.isNullOrEmpty(jsonStr)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(jsonStr);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * check if the string is a json string
     * @param jsonStr json string
     * @return true if the string is a json string
     */
    public static boolean isJsonStr(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().isEmpty()) {
            return false;
        }
        jsonStr = jsonStr.trim();
        if (!(jsonStr.startsWith("{") && jsonStr.endsWith("}"))
                && !(jsonStr.startsWith("[") && jsonStr.endsWith("]"))) {
            return false;
        }
        try {
            OBJECT_MAPPER.readTree(jsonStr);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
