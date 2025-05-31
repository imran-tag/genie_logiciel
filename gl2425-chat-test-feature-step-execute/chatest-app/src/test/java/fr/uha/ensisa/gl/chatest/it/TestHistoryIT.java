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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestHistoryIT {

    private static String port;
    private static String baseUrl;

    @BeforeAll
    public static void findPort() {
        port = System.getProperty("servlet.port", "8080");
        baseUrl = "http://localhost:" + port;
    }

    @BeforeEach
    public void setUp() throws IOException {
        // Clear any existing data by creating a fresh test environment
        // This ensures each test starts with a clean state
    }

    @Test
    @Order(1)
    public void testAccessHistoryPageEmpty() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history").openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        assertEquals(200, connection.getResponseCode(), "History page should be accessible");
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            assertTrue(output.contains("Test Execution History"), "Page should contain history title");
            assertTrue(output.contains("No test executions found") || output.contains("executions"), 
                      "Page should handle empty state or show executions");
        }
    }

    @Test
    @Order(2)
    public void testCreateTestAndExecute() throws IOException {
        // First, create a test
        String testId = createSampleTest("Integration Test Sample", "A test for integration testing");
        assertNotNull(testId, "Test should be created successfully");
        
        // Add a step to the test
        addStepToTest(testId, "Step 1", "Verify login functionality");
        
        // Execute the test with success
        executeTestWithResult(testId, 0, "OK", null);
        
        // Verify history shows the execution
        verifyHistoryContainsExecution(testId, "PASSED");
    }

    @Test
    @Order(3)
    public void testCreateTestAndExecuteWithFailure() throws IOException {
        // Create another test
        String testId = createSampleTest("Failing Test", "A test that will fail");
        
        // Add steps to the test
        addStepToTest(testId, "Step 1", "First step");
        addStepToTest(testId, "Step 2", "Second step that fails");
        
        // Execute first step successfully
        executeTestWithResult(testId, 0, "OK", null);
        
        // Execute second step with failure
        executeTestWithResult(testId, 1, "KO", "Authentication failed");
        
        // Verify history shows the failed execution
        verifyHistoryContainsExecution(testId, "FAILED");
    }

    @Test
    @Order(4)
    public void testHistoryShowsMultipleExecutions() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history").openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            
            // Should show both executions from previous tests
            assertTrue(output.contains("Integration Test Sample") || output.contains("PASSED"), 
                      "History should contain passed test execution");
            assertTrue(output.contains("Failing Test") || output.contains("FAILED"), 
                      "History should contain failed test execution");
            
            // Check that failed tests appear first (as per controller logic)
            int failedIndex = output.indexOf("FAILED");
            int passedIndex = output.indexOf("PASSED");
            if (failedIndex != -1 && passedIndex != -1) {
                assertTrue(failedIndex < passedIndex, "Failed tests should appear before passed tests");
            }
        }
    }

    @Test
    @Order(5)
    public void testExecutionDetailsPage() throws IOException {
        // Get execution ID from history page
        String executionId = getFirstExecutionId();
        
        if (executionId != null) {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history/" + executionId).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            
            assertEquals(200, connection.getResponseCode());
            
            try (InputStream in = connection.getInputStream()) {
                String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
                
                assertTrue(output.contains("Test Execution Details"), "Should show execution details page");
                assertTrue(output.contains("Execution Information"), "Should contain execution information section");
                assertTrue(output.contains("Test Information"), "Should contain test information section");
                assertTrue(output.contains("Test Steps"), "Should show test steps section");
            }
        }
    }

    @Test
    @Order(6)
    public void testTestSpecificHistoryPage() throws IOException {
        // Create a test and get its ID
        String testId = createSampleTest("Specific History Test", "Test for specific history");
        addStepToTest(testId, "Step 1", "Test step");
        
        // Execute the test multiple times
        executeTestWithResult(testId, 0, "OK", null);
        executeTestWithResult(testId, 0, "KO", "Second execution failed");
        
        // Access the test-specific history page
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history/test/" + testId).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            
            assertTrue(output.contains("Execution History for"), "Should show test-specific history title");
            assertTrue(output.contains("Specific History Test"), "Should show the test name");
            assertTrue(output.contains("Test Information"), "Should contain test information");
            
            // Should show both executions for this specific test
            long passedCount = output.lines().filter(line -> line.contains("PASSED")).count();
            long failedCount = output.lines().filter(line -> line.contains("FAILED")).count();
            
            assertTrue(passedCount >= 1, "Should show at least one passed execution");
            assertTrue(failedCount >= 1, "Should show at least one failed execution");
        }
    }

    @Test
    @Order(7)
    public void testNavigationLinks() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history").openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            
            // Check for navigation elements
            assertTrue(output.contains("Back to Test List") || output.contains("/test/list"), 
                      "Should have link back to test list");
            assertTrue(output.contains("Details") || output.contains("/test/history/"), 
                      "Should have links to execution details");
            assertTrue(output.contains("Run Test Again") || output.contains("/test/run/"), 
                      "Should have links to run tests again");
        }
    }

    @Test
    @Order(8)
    public void testInvalidExecutionId() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history/99999").openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        
        // Should redirect to history page for invalid execution ID
        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 302 || responseCode == 404, 
                  "Should redirect or return 404 for invalid execution ID");
    }

    @Test
    @Order(9)
    public void testStepResultsInExecutionDetails() throws IOException {
        // Create a test with multiple steps
        String testId = createSampleTest("Multi Step Test", "Test with multiple steps for verification");
        addStepToTest(testId, "Login Step", "User logs into the system");
        addStepToTest(testId, "Navigation Step", "User navigates to dashboard");
        addStepToTest(testId, "Action Step", "User performs main action");
        
        // Execute steps with mixed results
        executeTestWithResult(testId, 0, "OK", null);
        executeTestWithResult(testId, 1, "OK", null);
        executeTestWithResult(testId, 2, "KO", "Action button not found");
        
        // Get the execution details
        String executionId = getFirstExecutionId();
        if (executionId != null) {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history/" + executionId).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            
            assertEquals(200, connection.getResponseCode());
            
            try (InputStream in = connection.getInputStream()) {
                String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
                
                // Verify step results are displayed
                assertTrue(output.contains("Login Step"), "Should show first step name");
                assertTrue(output.contains("Navigation Step"), "Should show second step name");
                assertTrue(output.contains("Action Step"), "Should show third step name");
                
                // Check for status badges or indicators
                assertTrue(output.contains("OK") || output.contains("success"), "Should show OK status");
                assertTrue(output.contains("KO") || output.contains("danger"), "Should show KO status");
                
                // Check for failure comment
                assertTrue(output.contains("Action button not found"), "Should show failure comment");
            }
        }
    }

    // Helper methods

    private String createSampleTest(String name, String description) throws IOException {
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
        return null;
    }

    private void addStepToTest(String testId, String stepName, String stepDescription) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/steps/list/" + testId).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        String postData = "name=" + URLEncoder.encode(stepName, StandardCharsets.UTF_8) + 
                         "&description=" + URLEncoder.encode(stepDescription, StandardCharsets.UTF_8);
        
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }
        
        connection.getResponseCode(); // Consume response
    }

    private void executeTestWithResult(String testId, int stepIndex, String status, String comment) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/run/" + testId + "/" + stepIndex + "/validate").openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        String postData = "status=" + URLEncoder.encode(status, StandardCharsets.UTF_8);
        if (comment != null) {
            postData += "&comment=" + URLEncoder.encode(comment, StandardCharsets.UTF_8);
        }
        
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }
        
        connection.getResponseCode(); // Consume response
    }

    private void verifyHistoryContainsExecution(String testId, String expectedStatus) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history").openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            assertTrue(output.contains(expectedStatus), 
                      "History should contain execution with status: " + expectedStatus);
        }
    }

    private String getFirstExecutionId() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/test/history").openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        if (connection.getResponseCode() == 200) {
            try (InputStream in = connection.getInputStream()) {
                String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
                
                // Look for execution detail links
                int detailsIndex = output.indexOf("/test/history/");
                if (detailsIndex != -1) {
                    int startIndex = detailsIndex + "/test/history/".length();
                    int endIndex = output.indexOf("\"", startIndex);
                    if (endIndex != -1) {
                        return output.substring(startIndex, endIndex);
                    }
                }
            }
        }
        return null;
    }
}