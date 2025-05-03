package fr.uha.ensisa.gl.chatest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestExecution {
    private Long id;
    private Long testId;
    private String testName;
    private Date executionDate;
    private String status; // "PASSED" or "FAILED"
    private String comment;
    private List<StepResult> stepResults = new ArrayList<>();

    public TestExecution() {
        this.executionDate = new Date();
    }

    public TestExecution(Long testId, String testName, String status) {
        this.testId = testId;
        this.testName = testName;
        this.status = status;
        this.executionDate = new Date();
    }

    // Inner class to store step results
    public static class StepResult {
        private Long stepId;
        private String stepName;
        private String status; // "OK" or "KO"
        private String comment;

        public StepResult() {
        }

        public StepResult(Long stepId, String stepName, String status, String comment) {
            this.stepId = stepId;
            this.stepName = stepName;
            this.status = status;
            this.comment = comment;
        }

        public Long getStepId() {
            return stepId;
        }

        public void setStepId(Long stepId) {
            this.stepId = stepId;
        }

        public String getStepName() {
            return stepName;
        }

        public void setStepName(String stepName) {
            this.stepName = stepName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    // Method to add a step result
    public void addStepResult(StepResult stepResult) {
        this.stepResults.add(stepResult);
    }

    // Method to add a step result from a ChatStep
    public void addStepResult(ChatStep step) {
        StepResult result = new StepResult(
            step.getId(),
            step.getName(),
            step.getStatus(),
            step.getComment()
        );
        this.stepResults.add(result);
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<StepResult> getStepResults() {
        return stepResults;
    }

    public void setStepResults(List<StepResult> stepResults) {
        this.stepResults = stepResults;
    }
}