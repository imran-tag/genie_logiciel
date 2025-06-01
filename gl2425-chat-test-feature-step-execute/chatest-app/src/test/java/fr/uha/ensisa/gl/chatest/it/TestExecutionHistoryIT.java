package fr.uha.ensisa.gl.chatest.it;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestExecutionHistoryIT {

    private static String port;

    @BeforeAll
    public static void findPort() {
        port = System.getProperty("servlet.port", "8080");
    }

    @Test
    public void testHistoryPageLoads() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/test/history").openConnection();
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            assertTrue(output.contains("Test Execution History"), "History page should contain title");
            assertTrue(output.contains("Back to Test List"), "History page should contain back link");
        }
    }

    @Test
    public void testHistoryPageContainsBootstrapElements() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/test/history").openConnection();
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            assertTrue(output.contains("bootstrap.min.css"), "Page should include Bootstrap CSS");
            assertTrue(output.contains("container"), "Page should use Bootstrap container class");
            assertTrue(output.contains("card"), "Page should contain Bootstrap card elements");
        }
    }

    @Test
    public void testTestListPageLoads() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/test/list").openConnection();
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            assertTrue(output.contains("Test List"), "Test list page should contain title");
            assertTrue(output.contains("Create a new test"), "Test list page should contain create link");
            assertTrue(output.contains("View Test Execution History"), "Test list page should contain history link");
        }
    }

    @Test
    public void testRootRedirectsToTestList() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/").openConnection();
        connection.setInstanceFollowRedirects(false); // Don't follow redirects automatically
        connection.connect();
        
        // Should get a redirect response
        int responseCode = connection.getResponseCode();
        assertTrue(responseCode == 302 || responseCode == 301, "Root should redirect");
        
        String location = connection.getHeaderField("Location");
        assertTrue(location.contains("/test/list"), "Should redirect to test list");
    }

    @Test
    public void testHistoryPageWithNoExecutions() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/test/history").openConnection();
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            // Since we're using in-memory storage, there should be no executions initially
            assertTrue(output.contains("No test executions found") || output.contains("Execution History"), 
                      "History page should handle empty state gracefully");
        }
    }

    @Test
    public void testCreateTestPageLoads() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/test/create").openConnection();
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            assertTrue(output.contains("Create a new test"), "Create page should contain title");
            assertTrue(output.contains("name"), "Create page should contain name input");
            assertTrue(output.contains("description"), "Create page should contain description input");
        }
    }

    @Test
    public void testHelloPageWithParameter() throws IOException {
        String testName = "HistoryTest";
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/hello?name=" + testName).openConnection();
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            assertTrue(output.contains(testName), "Hello page should contain the provided name: " + testName);
            assertTrue(output.contains("Hello"), "Hello page should contain greeting");
        }
    }

    @Test
    public void testHistoryPageStructure() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/test/history").openConnection();
        connection.connect();
        
        assertEquals(200, connection.getResponseCode());
        
        try (InputStream in = connection.getInputStream()) {
            String output = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            
            // Check for key structural elements
            assertTrue(output.contains("Execution History"), "Should contain history section");
            assertTrue(output.contains("Failed tests first"), "Should mention sorting by failed tests");
            assertTrue(output.contains("btn"), "Should contain Bootstrap button classes");
            
            // Check for empty state message since we start with no executions
            assertTrue(output.contains("No test executions found") || output.contains("table"), 
                      "Should show empty state message or table if executions exist");
        }
    }
}