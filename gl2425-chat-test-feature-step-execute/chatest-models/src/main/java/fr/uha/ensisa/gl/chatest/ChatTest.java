package fr.uha.ensisa.gl.chatest;

import java.util.ArrayList;
import java.util.List;

public class ChatTest {
    private Long id;
    private String name;
    private String description;
    private List<ChatStep> steps = new ArrayList<>();

    public ChatTest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addStep(ChatStep step) {
        steps.add(step);
    }

    public List<ChatStep> getStep() {
        return steps;
    }

    public void removeStep(long stepId) {
        steps.removeIf(step -> step.getId() == stepId);
    }
}
