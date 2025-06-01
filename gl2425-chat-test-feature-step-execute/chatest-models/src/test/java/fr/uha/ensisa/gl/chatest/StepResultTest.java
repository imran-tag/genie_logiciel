package fr.uha.ensisa.gl.chatest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StepResultTest {
    private TestExecution.StepResult stepResult;

    @BeforeEach
    public void setUp() {
        stepResult = new TestExecution.StepResult();
}

    @Test
    @DisplayName("Test StepResult default constructor")
    public void testDefaultConstructor() {
        TestExecution.StepResult result = new TestExecution.StepResult();
        
        assertNotNull(result);
        assertNull(result.getStepId());
        assertNull(result.getStepName());
        assertNull(result.getStatus());
        assertNull(result.getComment());
    }

    @Test
    @DisplayName("Test StepResult parameterized constructor")
    public void testParameterizedConstructor() {
        Long stepId = 123L;
        String stepName = "Login Step";
        String status = "OK";
        String comment = "Step completed successfully";
        
        TestExecution.StepResult result = new TestExecution.StepResult(stepId, stepName, status, comment);
        
        assertNotNull(result);
        assertEquals(stepId, result.getStepId());
        assertEquals(stepName, result.getStepName());
        assertEquals(status, result.getStatus());
        assertEquals(comment, result.getComment());
    }

    @Test
    @DisplayName("Test StepResult parameterized constructor with nulls")
    public void testParameterizedConstructorWithNulls() {
        TestExecution.StepResult result = new TestExecution.StepResult(null, null, null, null);
        
        assertNotNull(result);
        assertNull(result.getStepId());
        assertNull(result.getStepName());
        assertNull(result.getStatus());
        assertNull(result.getComment());
    }

    @Test
    @DisplayName("Test stepId getter and setter")
    public void testStepId() {
        assertNull(stepResult.getStepId());
        
        Long stepId = 456L;
        stepResult.setStepId(stepId);
        assertEquals(stepId, stepResult.getStepId());
        
        stepResult.setStepId(null);
        assertNull(stepResult.getStepId());
    }

    @Test
    @DisplayName("Test stepName getter and setter")
    public void testStepName() {
        assertNull(stepResult.getStepName());
        
        String stepName = "Validation Step";
        stepResult.setStepName(stepName);
        assertEquals(stepName, stepResult.getStepName());
        
        stepResult.setStepName(null);
        assertNull(stepResult.getStepName());
        
        stepResult.setStepName("");
        assertEquals("", stepResult.getStepName());
    }

    @Test
    @DisplayName("Test status getter and setter")
    public void testStatus() {
        assertNull(stepResult.getStatus());
        
        stepResult.setStatus("OK");
        assertEquals("OK", stepResult.getStatus());
        
        stepResult.setStatus("KO");
        assertEquals("KO", stepResult.getStatus());
        
        stepResult.setStatus(null);
        assertNull(stepResult.getStatus());
        
        stepResult.setStatus("");
        assertEquals("", stepResult.getStatus());
    }

    @Test
    @DisplayName("Test comment getter and setter")
    public void testComment() {
        assertNull(stepResult.getComment());
        
        String comment = "Step failed due to timeout";
        stepResult.setComment(comment);
        assertEquals(comment, stepResult.getComment());
        
        stepResult.setComment(null);
        assertNull(stepResult.getComment());
        
        stepResult.setComment("");
        assertEquals("", stepResult.getComment());
    }

    @Test
    @DisplayName("Test complete StepResult workflow")
    public void testCompleteWorkflow() {
        Long stepId = 789L;
        String stepName = "Authentication Step";
        String status = "KO";
        String comment = "Authentication failed - invalid credentials";

        stepResult.setStepId(stepId);
        stepResult.setStepName(stepName);
        stepResult.setStatus(status);
        stepResult.setComment(comment);

        assertEquals(stepId, stepResult.getStepId());
        assertEquals(stepName, stepResult.getStepName());
        assertEquals(status, stepResult.getStatus());
        assertEquals(comment, stepResult.getComment());
    }

    @Test
    @DisplayName("Test StepResult with edge cases")
    public void testEdgeCases() {
        // Test with very long strings
        String longStepName = "A".repeat(1000);
        String longComment = "B".repeat(2000);
        
        stepResult.setStepName(longStepName);
        stepResult.setComment(longComment);
        
        assertEquals(longStepName, stepResult.getStepName());
        assertEquals(longComment, stepResult.getComment());
        
        // Test with special characters
        String specialStepName = "Step with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        String specialComment = "Comment with newlines\nand\ttabs\rand unicode: ñáéíóú";
        
        stepResult.setStepName(specialStepName);
        stepResult.setComment(specialComment);
        
        assertEquals(specialStepName, stepResult.getStepName());
        assertEquals(specialComment, stepResult.getComment());
    }

    @Test
    @DisplayName("Test multiple StepResult instances")
    public void testMultipleInstances() {
        TestExecution.StepResult result1 = new TestExecution.StepResult(1L, "Step 1", "OK", null);
        TestExecution.StepResult result2 = new TestExecution.StepResult(2L, "Step 2", "KO", "Error occurred");
        TestExecution.StepResult result3 = new TestExecution.StepResult();
        
        // Verify independence
        assertNotEquals(result1.getStepId(), result2.getStepId());
        assertNotEquals(result1.getStepName(), result2.getStepName());
        assertNotEquals(result1.getStatus(), result2.getStatus());
        assertNotEquals(result1.getComment(), result2.getComment());
        
        assertNull(result3.getStepId());
        assertNull(result3.getStepName());
        assertNull(result3.getStatus());
        assertNull(result3.getComment());
        
        // Modify one and verify others unchanged
        result1.setStatus("KO");
        assertEquals("KO", result1.getStatus());
        assertEquals("KO", result2.getStatus()); // This should remain unchanged
        assertNull(result3.getStatus());
    }
}