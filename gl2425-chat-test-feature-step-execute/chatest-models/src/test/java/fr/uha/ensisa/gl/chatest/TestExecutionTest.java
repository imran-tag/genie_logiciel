package fr.uha.ensisa.gl.chatest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestExecutionTest {
    private TestExecution testExecution;
    private Date testDate;

    @BeforeEach
    public void setUp() {
        testExecution = new TestExecution();
        testDate = new Date();
    }

    @Test
    @DisplayName("Test default constructor")
    public void testDefaultConstructor() {
        TestExecution execution = new TestExecution();
        
        assertNotNull(execution);
        assertNull(execution.getId());
        assertNull(execution.getTestId());
        assertNull(execution.getTestName());
        assertNotNull(execution.getExecutionDate());
        assertNull(execution.getStatus());
        assertNull(execution.getComment());
        assertNotNull(execution.getStepResults());
        assertTrue(execution.getStepResults().isEmpty());
        
        // Verify execution date is recent (within last few seconds)
        long timeDiff = new Date().getTime() - execution.getExecutionDate().getTime();
        assertTrue(timeDiff < 5000); // Less than 5 seconds
    }

    @Test
    @DisplayName("Test parameterized constructor")
    public void testParameterizedConstructor() {
        Long testId = 123L;
        String testName = "Login Test";
        String status = "PASSED";
        
        TestExecution execution = new TestExecution(testId, testName, status);
        
        assertNotNull(execution);
        assertEquals(testId, execution.getTestId());
        assertEquals(testName, execution.getTestName());
        assertEquals(status, execution.getStatus());
        assertNotNull(execution.getExecutionDate());
        assertNull(execution.getId());
        assertNull(execution.getComment());
        assertNotNull(execution.getStepResults());
        assertTrue(execution.getStepResults().isEmpty());
        
        // Verify execution date is recent
        long timeDiff = new Date().getTime() - execution.getExecutionDate().getTime();
        assertTrue(timeDiff < 5000);
    }

    @Test
    @DisplayName("Test id getter and setter")
    public void testId() {
        assertNull(testExecution.getId());
        
        Long id = 456L;
        testExecution.setId(id);
        assertEquals(id, testExecution.getId());
        
        testExecution.setId(null);
        assertNull(testExecution.getId());
    }

    @Test
    @DisplayName("Test testId getter and setter")
    public void testTestId() {
        assertNull(testExecution.getTestId());
        
        Long testId = 789L;
        testExecution.setTestId(testId);
        assertEquals(testId, testExecution.getTestId());
        
        testExecution.setTestId(null);
        assertNull(testExecution.getTestId());
    }

    @Test
    @DisplayName("Test testName getter and setter")
    public void testTestName() {
        assertNull(testExecution.getTestName());
        
        String testName = "User Registration Test";
        testExecution.setTestName(testName);
        assertEquals(testName, testExecution.getTestName());
        
        testExecution.setTestName(null);
        assertNull(testExecution.getTestName());
        
        testExecution.setTestName("");
        assertEquals("", testExecution.getTestName());
    }

    @Test
    @DisplayName("Test executionDate getter and setter")
    public void testExecutionDate() {
        assertNotNull(testExecution.getExecutionDate());
        
        Date customDate = new Date(testDate.getTime() - 10000); // 10 seconds ago
        testExecution.setExecutionDate(customDate);
        assertEquals(customDate, testExecution.getExecutionDate());
        
        testExecution.setExecutionDate(null);
        assertNull(testExecution.getExecutionDate());
    }

    @Test
    @DisplayName("Test status getter and setter")
    public void testStatus() {
        assertNull(testExecution.getStatus());
        
        testExecution.setStatus("PASSED");
        assertEquals("PASSED", testExecution.getStatus());
        
        testExecution.setStatus("FAILED");
        assertEquals("FAILED", testExecution.getStatus());
        
        testExecution.setStatus(null);
        assertNull(testExecution.getStatus());
        
        testExecution.setStatus("");
        assertEquals("", testExecution.getStatus());
    }

    @Test
    @DisplayName("Test comment getter and setter")
    public void testComment() {
        assertNull(testExecution.getComment());
        
        String comment = "Test failed at step 3 due to timeout";
        testExecution.setComment(comment);
        assertEquals(comment, testExecution.getComment());
        
        testExecution.setComment(null);
        assertNull(testExecution.getComment());
        
        testExecution.setComment("");
        assertEquals("", testExecution.getComment());
    }

    @Test
    @DisplayName("Test stepResults getter and setter")
    public void testStepResults() {
        List<TestExecution.StepResult> stepResults = testExecution.getStepResults();
        assertNotNull(stepResults);
        assertTrue(stepResults.isEmpty());
        
        TestExecution.StepResult result1 = new TestExecution.StepResult(1L, "Step 1", "OK", null);
        TestExecution.StepResult result2 = new TestExecution.StepResult(2L, "Step 2", "KO", "Failed validation");
        
        stepResults.add(result1);
        stepResults.add(result2);
        
        assertEquals(2, testExecution.getStepResults().size());
        assertTrue(testExecution.getStepResults().contains(result1));
        assertTrue(testExecution.getStepResults().contains(result2));
        
        // Test setter
        List<TestExecution.StepResult> newResults = List.of(result1);
        testExecution.setStepResults(newResults);
        assertEquals(1, testExecution.getStepResults().size());
        assertTrue(testExecution.getStepResults().contains(result1));
        assertFalse(testExecution.getStepResults().contains(result2));
    }

    @Test
    @DisplayName("Test addStepResult with StepResult object")
    public void testAddStepResult() {
        assertTrue(testExecution.getStepResults().isEmpty());
        
        TestExecution.StepResult result1 = new TestExecution.StepResult(1L, "Login Step", "OK", null);
        testExecution.addStepResult(result1);
        
        assertEquals(1, testExecution.getStepResults().size());
        assertEquals(result1, testExecution.getStepResults().get(0));
        
        TestExecution.StepResult result2 = new TestExecution.StepResult(2L, "Validation Step", "KO", "Validation failed");
        testExecution.addStepResult(result2);
        
        assertEquals(2, testExecution.getStepResults().size());
        assertEquals(result1, testExecution.getStepResults().get(0));
        assertEquals(result2, testExecution.getStepResults().get(1));
    }

    @Test
    @DisplayName("Test addStepResult with ChatStep object")
    public void testAddStepResultFromChatStep() {
        assertTrue(testExecution.getStepResults().isEmpty());
        
        ChatStep step1 = new ChatStep();
        step1.setId(10L);
        step1.setName("Navigation Step");
        step1.setStatus("OK");
        step1.setComment("Navigation successful");
        
        testExecution.addStepResult(step1);
        
        assertEquals(1, testExecution.getStepResults().size());
        TestExecution.StepResult result = testExecution.getStepResults().get(0);
        assertEquals(step1.getId(), result.getStepId());
        assertEquals(step1.getName(), result.getStepName());
        assertEquals(step1.getStatus(), result.getStatus());
        assertEquals(step1.getComment(), result.getComment());
        
        ChatStep step2 = new ChatStep();
        step2.setId(20L);
        step2.setName("Input Step");
        step2.setStatus("KO");
        step2.setComment(null);
        
        testExecution.addStepResult(step2);
        
        assertEquals(2, testExecution.getStepResults().size());
        TestExecution.StepResult result2 = testExecution.getStepResults().get(1);
        assertEquals(step2.getId(), result2.getStepId());
        assertEquals(step2.getName(), result2.getStepName());
        assertEquals(step2.getStatus(), result2.getStatus());
        assertEquals(step2.getComment(), result2.getComment());
    }

    @Test
    @DisplayName("Test complete TestExecution workflow")
    public void testCompleteWorkflow() {
        Long id = 1L;
        Long testId = 100L;
        String testName = "Complete Login Test";
        String status = "FAILED";
        String comment = "Test failed at authentication step";
        Date customDate = new Date();

        testExecution.setId(id);
        testExecution.setTestId(testId);
        testExecution.setTestName(testName);
        testExecution.setStatus(status);
        testExecution.setComment(comment);
        testExecution.setExecutionDate(customDate);

        assertEquals(id, testExecution.getId());
        assertEquals(testId, testExecution.getTestId());
        assertEquals(testName, testExecution.getTestName());
        assertEquals(status, testExecution.getStatus());
        assertEquals(comment, testExecution.getComment());
        assertEquals(customDate, testExecution.getExecutionDate());

        // Add step results
        ChatStep step1 = new ChatStep();
        step1.setId(1L);
        step1.setName("Navigate to login");
        step1.setStatus("OK");
        
        ChatStep step2 = new ChatStep();
        step2.setId(2L);
        step2.setName("Enter credentials");
        step2.setStatus("KO");
        step2.setComment("Invalid username");

        testExecution.addStepResult(step1);
        testExecution.addStepResult(step2);

        assertEquals(2, testExecution.getStepResults().size());
        
        TestExecution.StepResult result1 = testExecution.getStepResults().get(0);
        assertEquals(step1.getId(), result1.getStepId());
        assertEquals(step1.getName(), result1.getStepName());
        assertEquals(step1.getStatus(), result1.getStatus());
        
        TestExecution.StepResult result2 = testExecution.getStepResults().get(1);
        assertEquals(step2.getId(), result2.getStepId());
        assertEquals(step2.getName(), result2.getStepName());
        assertEquals(step2.getStatus(), result2.getStatus());
        assertEquals(step2.getComment(), result2.getComment());
    }

    @Test
    @DisplayName("Test addStepResult with null ChatStep")
    public void testAddStepResultWithNullChatStep() {
        ChatStep nullStep = null;
        
        // This should not throw an exception but might cause NullPointerException
        // depending on implementation
        assertThrows(NullPointerException.class, () -> {
            testExecution.addStepResult(nullStep);
        });
    }
}