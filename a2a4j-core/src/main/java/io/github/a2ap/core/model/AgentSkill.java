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
     * The unique identifier of the skill.
     * Required field.
     */
    @NotNull
    @JsonProperty("id")
    private String id;

    /**
     * The name of the skill.
     * Required field.
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

    public AgentSkill(String id, String name, String description, List<String> tags,
            List<String> examples, List<String> inputModes, List<String> outputModes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.examples = examples;
        this.inputModes = inputModes;
        this.outputModes = outputModes;
    }

    public static AgentSkillBuilder builder() {
        return new AgentSkillBuilder();
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
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(examples, that.examples) &&
                Objects.equals(inputModes, that.inputModes) &&
                Objects.equals(outputModes, that.outputModes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, tags, examples, inputModes, outputModes);
    }

    @Override
    public String toString() {
        return "AgentSkill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", examples=" + examples +
                ", inputModes=" + inputModes +
                ", outputModes=" + outputModes +
                '}';
    }

    public static class AgentSkillBuilder {
        private String id;
        private String name;
        private String description;
        private List<String> tags;
        private List<String> examples;
        private List<String> inputModes;
        private List<String> outputModes;

        AgentSkillBuilder() {
        }

        public AgentSkillBuilder id(String id) {
            this.id = id;
            return this;
        }

        public AgentSkillBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AgentSkillBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AgentSkillBuilder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public AgentSkillBuilder examples(List<String> examples) {
            this.examples = examples;
            return this;
        }

        public AgentSkillBuilder inputModes(List<String> inputModes) {
            this.inputModes = inputModes;
            return this;
        }

        public AgentSkillBuilder outputModes(List<String> outputModes) {
            this.outputModes = outputModes;
            return this;
        }

        public AgentSkill build() {
            return new AgentSkill(id, name, description, tags, examples, inputModes, outputModes);
        }

        @Override
        public String toString() {
            return "AgentSkill.AgentSkillBuilder(id=" + this.id +
                    ", name=" + this.name +
                    ", description=" + this.description +
                    ", tags=" + this.tags +
                    ", examples=" + this.examples +
                    ", inputModes=" + this.inputModes +
                    ", outputModes=" + this.outputModes + ")";
        }
    }
}
