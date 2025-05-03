package fr.uha.ensisa.gl.chatest;

import java.util.Date;

public class TestExecution {
    private Long id;
    private Long testId;
    private String testName;
    private Date executionDate;
    private String status; // "PASSED" or "FAILED"
    private String comment;

    public TestExecution() {
    }

    public TestExecution(Long testId, String testName, String status) {
        this.testId = testId;
        this.testName = testName;
        this.status = status;
        this.executionDate = new Date();
    }

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
}