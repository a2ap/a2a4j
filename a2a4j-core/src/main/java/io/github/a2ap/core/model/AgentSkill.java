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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents a skill that an agent possesses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentSkill {

    /**
     * The unique identifier of the skill. Required field.
     */
    @NotNull
    @JsonProperty("id")
    private String id;

    /**
     * The name of the skill. Required field.
     */
    @NotNull
    @JsonProperty("name")
    private String name;

    /**
     * An optional description of the skill.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Optional tags associated with the skill.
     */
    @JsonProperty("tags")
    private List<String> tags;

    /**
     * Optional examples of how to use the skill.
     */
    @JsonProperty("examples")
    private List<String> examples;

    /**
     * Optional input modes supported by the skill.
     */
    @JsonProperty("inputModes")
    private List<String> inputModes;

    /**
     * Optional output modes supported by the skill.
     */
    @JsonProperty("outputModes")
    private List<String> outputModes;

    public AgentSkill() {
    }

    public AgentSkill(String id, String name, String description, List<String> tags, List<String> examples,
                      List<String> inputModes, List<String> outputModes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.examples = examples;
        this.inputModes = inputModes;
        this.outputModes = outputModes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }

    public List<String> getInputModes() {
        return inputModes;
    }

    public void setInputModes(List<String> inputModes) {
        this.inputModes = inputModes;
    }

    public List<String> getOutputModes() {
        return outputModes;
    }

    public void setOutputModes(List<String> outputModes) {
        this.outputModes = outputModes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AgentSkill that = (AgentSkill) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
                && Objects.equals(description, that.description) && Objects.equals(tags, that.tags)
                && Objects.equals(examples, that.examples) && Objects.equals(inputModes, that.inputModes)
                && Objects.equals(outputModes, that.outputModes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, tags, examples, inputModes, outputModes);
    }

    @Override
    public String toString() {
        return "AgentSkill{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", description='" + description + '\''
                + ", tags=" + tags + ", examples=" + examples + ", inputModes=" + inputModes + ", outputModes="
                + outputModes + '}';
    }

    /**
     * Builder for {@link AgentSkill}
     */
    public static class Builder {

        private String id;

        private String name;

        private String description;

        private List<String> tags;

        private List<String> examples;

        private List<String> inputModes;

        private List<String> outputModes;

        Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder examples(List<String> examples) {
            this.examples = examples;
            return this;
        }

        public Builder inputModes(List<String> inputModes) {
            this.inputModes = inputModes;
            return this;
        }

        public Builder outputModes(List<String> outputModes) {
            this.outputModes = outputModes;
            return this;
        }

        public AgentSkill build() {
            return new AgentSkill(id, name, description, tags, examples, inputModes, outputModes);
        }

        @Override
        public String toString() {
            return "AgentSkill.AgentSkillBuilder(id=" + this.id + ", name=" + this.name + ", description="
                    + this.description + ", tags=" + this.tags + ", examples=" + this.examples + ", inputModes="
                    + this.inputModes + ", outputModes=" + this.outputModes + ")";
        }

    }

}
