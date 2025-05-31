package fr.uha.ensisa.gl.chatest.it;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestExecutionResultsIT {

    private static String port;
    private static String baseUrl;

    @BeforeAll
    public static void findPort() {
        port = System.getProperty("servlet.port", "8080");
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    public void testCompleteTestExecutionWithAllStepsSuccess() throws IOException {
        // Create a comprehensive test
        String testId = createTest("Complete Success Test", "All steps should pass");
        
        // Add multiple steps
        addStep(testId, "Initialize System", "Set up test environment");
        addStep(testId, "Login Process", "User authentication");
        addStep(testId, "Main Workflow", "Execute primary functionality");
        addStep(testId, "Cleanup", "Clean up test data");
        
        // Execute all steps successfully
        executeStep(testId, 0, "OK", null);
        executeStep(testId, 1, "OK", null);
        executeStep(testId, 2, "OK", null);
        executeStep(testId, 3, "OK", null);
        
        // Verify execution was saved and marked as PASSED
        String executionDetails = getLatestExecutionDetails();
        assertTrue(executionDetails.contains("PASSED"), "Execution should be marked as PASSED");
        assertTrue(executionDetails.contains("Initialize System"), "Should contain all step names");
        assertTrue(executionDetails.contains("Login Process"), "Should contain all step names");
        assertTrue(executionDetails.contains("Main Workflow"), "Should contain all step names");
        assertTrue(executionDetails.contains("Cleanup"), "Should contain all step names");
        
        // Verify all steps show OK status
        long okCount = executionDetails.lines().filter(line -> line.contains("OK") || line.contains("success")).count();
        assertTrue(okCount >= 4, "Should show OK status for all 4 steps");
    }

    @Test
    @Order(2)
    public void testPartialTestExecutionWithEarlyFailure() throws IOException {
        // Create a test that fails early
        String testId = createTest("Early Failure Test", "Fails on second step");
        
        addStep(testId, "Preparation", "Prepare test data");
        addStep(testId, "Critical Operation", "This step will fail");
        addStep(testId, "Post Processing", "Should not be executed");
        addStep(testId, "Final Cleanup", "Should not be executed");
        
        // Execute first step successfully
        executeStep(testId, 0, "OK", null);
        
        // Execute second step with failure
        executeStep(testId, 1, "KO", "Database connection timeout");
        
        // Verify execution was saved and marked as FAILED
        String executionDetails = getLatestExecutionDetails();
        assertTrue(executionDetails.contains("FAILED"), "Execution should be marked as FAILED");
        assertTrue(executionDetails.contains("Failed at step 2"), "Should indicate failure location");
        assertTrue(executionDetails.contains("Database connection timeout"), "Should contain failure reason");
        
        // Verify step results
        assertTrue(executionDetails.contains("Preparation"), "Should show executed step");
        assertTrue(executionDetails.contains("Critical Operation"), "Should show failed step");
        
        // Check that only executed steps are recorded
        String[] lines = executionDetails.split("\n");
        boolean foundPrepOk = false;
        boolean foundCriticalKo = false;
        
        for (String line : lines) {
            if (line.contains("Preparation") && (line.contains("OK") || line.contains("success"))) {
                foundPrepOk = true;
            }
            if (line.contains("Critical Operation") && (line.contains("KO") || line.contains("danger"))) {
                foundCriticalKo = true;
            }
        }
        
        assertTrue(foundPrepOk, "Should show Preparation step as OK");
        assertTrue(foundCriticalKo, "Should show Critical Operation step as KO");
    }

    @Test
    @Order(3)
    public void testStepExecutionWithDetailedComments() throws IOException {
        String testId = createTest("Detailed Comments Test", "Test with detailed step comments");
        
        addStep(testId, "User Interface Check", "Verify UI elements");
        addStep(testId, "Data Validation", "Validate input data");
        addStep(testId, "Performance Test", "Check response times");
        
        // Execute with detailed comments
        executeStep(testId, 0, "OK", null);
        executeStep(testId, 1, "OK", null);
        executeStep(testId, 2, "KO", "Response time exceeded 2 seconds. Expected: <2s, Actual: 3.5s");
        
        String executionDetails = getLatestExecutionDetails();
        assertTrue(executionDetails.contains("Response time exceeded 2 seconds"), 
                  "Should contain detailed failure comment");
        assertTrue(executionDetails.contains("Expected: <2s, Actual: 3.5s"), 
                  "Should contain technical details in comment");
    }

    @Test
    @Order(4)
    public void testMultipleExecutionsOfSameTest() throws IOException {
        String testId = createTest("Repeated Test", "Test executed multiple times");
        
        addStep(testId, "Step 1", "First step");
        addStep(testId, "Step 2", "Second step");
        
        // First execution - success
        executeStep(testId, 0, "OK", null);
        executeStep(testId, 1, "OK", null);
        
        // Second execution - failure
        executeStep(testId, 0, "OK", null);
        executeStep(testId, 1, "KO", "Second run failed");
        
        // Third execution - success again
        executeStep(testId, 0, "OK", null);
        executeStep(testId, 1, "OK", null);
        
        // Check test-specific history
        String testHistory = getTestSpecificHistory(testId);
        
        // Should show 3 executions
        long passedCount = testHistory.lines().filter(line -> line.contains("PASSED")).count();
        long failedCount = testHistory.lines().filter(line -> line.contains("FAILED")).count();
        
        assertEquals(2, passedCount, "Should have 2 passed executions");
        assertEquals(1, failedCount, "Should have 1 failed execution");
        
        // Verify failed tests appear first (as per sorting logic)
        int firstFailedIndex = testHistory.indexOf("FAILED");
        int firstPassedIndex = testHistory.indexOf("PASSED");
        assertTrue(firstFailedIndex < firstPassedIndex, 
                  "Failed executions should appear before passed ones");
    }

    @Test
    @Order(5)
    public void testExecutionWithoutStepResults() throws IOException {
        // Test the scenario mentioned in the controller for older executions
        // This simulates executions that were created before step results were implemented
        
        String testId = createTest("Legacy Execution Test", "Simulates older execution format");
        addStep(testId, "Legacy Step", "Step from older version");
        
        executeStep(testId, 0, "OK", null);
        
        String executionDetails = getLatestExecutionDetails();
        
        // Should handle gracefully even if no step results are available
        // The controller checks for null or empty step results
        assertTrue(executionDetails.contains("Test Execution Details"), 
                  "Should still show execution details page");
        assertTrue(executionDetails.contains("Legacy Execution Test"), 
                  "Should show test name");
    }

    @Test
    @Order(6)
    public void testStepResultsConsistency() throws IOException {
        String testId = createTest("Consistency Test", "Verify step results consistency");
        
        addStep(testId, "Step A", "First step");
        addStep(testId, "Step B", "Second step");
        addStep(testId, "Step C", "Third step");
        
        // Execute steps with mixed results
        executeStep(testId, 0, "OK", null);
        executeStep(testId, 1, "KO", "Intentional failure for testing");
        
        String executionDetails = getLatestExecutionDetails();
        
        // Verify that the step results match the execution
        assertTrue(executionDetails.contains("Step A"), "Should contain Step A");
        assertTrue(executionDetails.contains("Step B"), "Should contain Step B");
        
        // Step C should not be present as execution stopped at Step B
        // Count occurrences more carefully
        boolean stepCInResults = executionDetails.lines()
            .anyMatch(line -> line.contains("Step C") && 
                     (line.contains("OK") || line.contains("KO") || 
                      line.contains("success") || line.contains("danger")));
        assertFalse(stepCInResults, "Step C should not have execution results");
        
        // Verify execution status is FAILED due to step failure
        assertTrue(executionDetails.contains("FAILED"), "Overall execution should be FAILED");
    }

    @Test
    @Order(7)
    public void testStepNavigationAndLinks() throws IOException {
        String testId = createTest("Navigation Test", "Test navigation elements");
        addStep(testId, "Nav Step", "Step for navigation testing");
        
        executeStep(testId, 0, "OK", null);
        
        String executionDetails = getLatestExecutionDetails();
        
        // Check for navigation elements in execution details
        assertTrue(executionDetails.contains("Back to History") || 
                  executionDetails.contains("/test/history"), 
                  "Should have link back to history");
        assertTrue(executionDetails.contains("View All Test Runs") || 
                  executionDetails.contains("/test/history/test/"), 
                  "Should have link to test-specific history");
        assertTrue(executionDetails.contains("Run Test Again") || 
                  executionDetails.contains("/test/run/"), 
                  "Should have link to run test again");
    }

    @Test
    @Order(8)
    public void testEmptyStepExecution() throws IOException {
        String testId = createTest("No Steps Test", "Test with no steps");
        
        // Try to execute step 0 on a test with no steps
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/run/" + testId + "/0").openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        
        // Should redirect to test list if no steps
        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 302, "Should redirect when no steps available");
        
        String location = connection.getHeaderField("Location");
        assertTrue(location != null && location.contains("/test/list"), 
                  "Should redirect to test list");
    }

    // Helper methods

    private String createTest(String name, String description) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/create").openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        String postData = "name=" + URLEncoder.encode(name, StandardCharsets.UTF_8) + 
                         "&description=" + URLEncoder.encode(description, StandardCharsets.UTF_8);
        
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }
        
        int responseCode = connection.getResponseCode();
        if (responseCode == 302) {
            String location = connection.getHeaderField("Location");
            if (location != null && location.contains("/test/steps/list/")) {
                return location.substring(location.lastIndexOf("/") + 1);
            }
        }
        throw new IOException("Failed to create test");
    }

    private void addStep(String testId, String stepName, String stepDescription) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/steps/list/" + testId).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        String postData = "name=" + URLEncoder.encode(stepName, StandardCharsets.UTF_8) + 
                         "&description=" + URLEncoder.encode(stepDescription, StandardCharsets.UTF_8);
        
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 302) {
            throw new IOException("Failed to add step");
        }
    }

    private void executeStep(String testId, int stepIndex, String status, String comment) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/run/" + testId + "/" + stepIndex + "/validate").openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        String postData = "status=" + URLEncoder.encode(status, StandardCharsets.UTF_8);
        if (comment != null && !comment.trim().isEmpty()) {
            postData += "&comment=" + URLEncoder.encode(comment, StandardCharsets.UTF_8);
        }
        
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 302) {
            throw new IOException("Failed to execute step");
        }
    }

    private String getLatestExecutionDetails() throws IOException {
        // First get the history page to find the latest execution
        String executionId = getLatestExecutionId();
        if (executionId == null) {
            throw new IOException("No executions found");
        }
        
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history/" + executionId).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        }
    }

    private String getLatestExecutionId() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history").openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        if (connection.getResponseCode() == 200) {
            try (InputStream in = connection.getInputStream()) {
                String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
                
                // Extract execution IDs using regex
                Pattern pattern = Pattern.compile("/test/history/(\\d+)");
                Matcher matcher = pattern.matcher(output);
                
                List<String> executionIds = new ArrayList<>();
                while (matcher.find()) {
                    executionIds.add(matcher.group(1));
                }
                
                // Return the first execution ID (should be the latest due to sorting)
                return executionIds.isEmpty() ? null : executionIds.get(0);
            }
        }
        return null;
    }

    private String getTestSpecificHistory(String testId) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history/test/" + testId).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        }
    }
}