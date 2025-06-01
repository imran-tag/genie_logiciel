package fr.uha.ensisa.gl.chatest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChatStepTest {
    private ChatStep chatStep;

    @BeforeEach
    public void setUp() {
        chatStep = new ChatStep();
    }

    @Test
    @DisplayName("Test ChatStep constructor")
    public void testConstructor() {
        ChatStep step = new ChatStep();
        assertNotNull(step);
        assertNull(step.getId());
        assertNull(step.getName());
        assertNull(step.getParentId());
        assertNull(step.getContent());
        assertNull(step.getStatus());
        assertNull(step.getComment());
    }

    @Test
    @DisplayName("Test parentId getter and setter")
    public void testParentId() {
        assertNull(chatStep.getParentId());
        
        Long parentId = 123L;
        chatStep.setParentId(parentId);
        assertEquals(parentId, chatStep.getParentId());
        
        chatStep.setParentId(null);
        assertNull(chatStep.getParentId());
    }

    @Test
    @DisplayName("Test id getter and setter")
    public void testId() {
        assertNull(chatStep.getId());
        
        Long id = 456L;
        chatStep.setId(id);
        assertEquals(id, chatStep.getId());
        
        chatStep.setId(null);
        assertNull(chatStep.getId());
    }

    @Test
    @DisplayName("Test name getter and setter")
    public void testName() {
        assertNull(chatStep.getName());
        
        String name = "Test Step Name";
        chatStep.setName(name);
        assertEquals(name, chatStep.getName());
        
        chatStep.setName(null);
        assertNull(chatStep.getName());
        
        chatStep.setName("");
        assertEquals("", chatStep.getName());
    }

    @Test
    @DisplayName("Test step setter")
    public void testStep() {
        String stepDescription = "This is a test step description";
        chatStep.setStep(stepDescription);
        // Note: There's no getStep() method in the ChatStep class
        // This test just ensures the setter doesn't throw an exception
        assertDoesNotThrow(() -> chatStep.setStep(stepDescription));
        
        assertDoesNotThrow(() -> chatStep.setStep(null));
        assertDoesNotThrow(() -> chatStep.setStep(""));
    }

    @Test
    @DisplayName("Test content getter and setter")
    public void testContent() {
        assertNull(chatStep.getContent());
        
        String content = "This is the step content";
        chatStep.setContent(content);
        assertEquals(content, chatStep.getContent());
        
        chatStep.setContent(null);
        assertNull(chatStep.getContent());
        
        chatStep.setContent("");
        assertEquals("", chatStep.getContent());
    }

    @Test
    @DisplayName("Test status getter and setter")
    public void testStatus() {
        assertNull(chatStep.getStatus());
        
        String status = "OK";
        chatStep.setStatus(status);
        assertEquals(status, chatStep.getStatus());
        
        chatStep.setStatus("KO");
        assertEquals("KO", chatStep.getStatus());
        
        chatStep.setStatus(null);
        assertNull(chatStep.getStatus());
        
        chatStep.setStatus("");
        assertEquals("", chatStep.getStatus());
    }

    @Test
    @DisplayName("Test comment getter and setter")
    public void testComment() {
        assertNull(chatStep.getComment());
        
        String comment = "This is a test comment";
        chatStep.setComment(comment);
        assertEquals(comment, chatStep.getComment());
        
        chatStep.setComment(null);
        assertNull(chatStep.getComment());
        
        chatStep.setComment("");
        assertEquals("", chatStep.getComment());
    }

    @Test
    @DisplayName("Test complete ChatStep workflow")
    public void testCompleteWorkflow() {
        Long parentId = 1L;
        Long id = 2L;
        String name = "Login Test Step";
        String step = "Enter username and password";
        String content = "Navigate to login page and enter credentials";
        String status = "OK";
        String comment = "Step completed successfully";

        chatStep.setParentId(parentId);
        chatStep.setId(id);
        chatStep.setName(name);
        chatStep.setStep(step);
        chatStep.setContent(content);
        chatStep.setStatus(status);
        chatStep.setComment(comment);

        assertEquals(parentId, chatStep.getParentId());
        assertEquals(id, chatStep.getId());
        assertEquals(name, chatStep.getName());
        assertEquals(content, chatStep.getContent());
        assertEquals(status, chatStep.getStatus());
        assertEquals(comment, chatStep.getComment());
    }
}