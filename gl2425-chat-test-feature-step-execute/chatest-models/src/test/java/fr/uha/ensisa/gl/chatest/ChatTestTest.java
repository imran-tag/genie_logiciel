package fr.uha.ensisa.gl.chatest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChatTestTest {
    private ChatTest chatTest;

    @BeforeEach
    public void setUp() {
        chatTest = new ChatTest();
    }

    @Test
    @DisplayName("Test ChatTest constructor")
    public void testConstructor() {
        ChatTest test = new ChatTest();
        assertNotNull(test);
        assertNull(test.getId());
        assertNull(test.getName());
        assertNull(test.getDescription());
        assertNotNull(test.getStep());
        assertTrue(test.getStep().isEmpty());
    }

    @Test
    @DisplayName("Test must have a name")
    public void setName() {
        assertNull(chatTest.getName());
        String name = "A sample test name";
        chatTest.setName(name);
        assertEquals(name, chatTest.getName());
        
        chatTest.setName(null);
        assertNull(chatTest.getName());
        
        chatTest.setName("");
        assertEquals("", chatTest.getName());
    }

    @Test
    @DisplayName("Test id getter and setter")
    public void testId() {
        assertNull(chatTest.getId());
        
        Long id = 123L;
        chatTest.setId(id);
        assertEquals(id, chatTest.getId());
        
        chatTest.setId(null);
        assertNull(chatTest.getId());
    }

    @Test
    @DisplayName("Test description getter and setter")
    public void testDescription() {
        assertNull(chatTest.getDescription());
        
        String description = "This is a test description";
        chatTest.setDescription(description);
        assertEquals(description, chatTest.getDescription());
        
        chatTest.setDescription(null);
        assertNull(chatTest.getDescription());
        
        chatTest.setDescription("");
        assertEquals("", chatTest.getDescription());
    }

    @Test
    @DisplayName("Test getStep returns steps list")
    public void testGetStep() {
        List<ChatStep> steps = chatTest.getStep();
        assertNotNull(steps);
        assertTrue(steps.isEmpty());
        
        // Verify it's the same list instance
        assertSame(steps, chatTest.getStep());
    }

    @Test
    @DisplayName("Test addStep functionality")
    public void testAddStep() {
        List<ChatStep> steps = chatTest.getStep();
        assertTrue(steps.isEmpty());
        
        ChatStep step1 = new ChatStep();
        step1.setId(1L);
        step1.setName("Step 1");
        
        chatTest.addStep(step1);
        assertEquals(1, steps.size());
        assertTrue(steps.contains(step1));
        
        ChatStep step2 = new ChatStep();
        step2.setId(2L);
        step2.setName("Step 2");
        
        chatTest.addStep(step2);
        assertEquals(2, steps.size());
        assertTrue(steps.contains(step1));
        assertTrue(steps.contains(step2));
    }

    @Test
    @DisplayName("Test addStep with null step")
    public void testAddStepWithNull() {
        chatTest.addStep(null);
        assertEquals(1, chatTest.getStep().size());
        assertNull(chatTest.getStep().get(0));
    }

    @Test
    @DisplayName("Test removeStep functionality")
    public void testRemoveStep() {
        // Add some steps first
        ChatStep step1 = new ChatStep();
        step1.setId(1L);
        step1.setName("Step 1");
        
        ChatStep step2 = new ChatStep();
        step2.setId(2L);
        step2.setName("Step 2");
        
        ChatStep step3 = new ChatStep();
        step3.setId(3L);
        step3.setName("Step 3");
        
        chatTest.addStep(step1);
        chatTest.addStep(step2);
        chatTest.addStep(step3);
        
        assertEquals(3, chatTest.getStep().size());
        
        // Remove step with id 2
        chatTest.removeStep(2L);
        assertEquals(2, chatTest.getStep().size());
        assertFalse(chatTest.getStep().contains(step2));
        assertTrue(chatTest.getStep().contains(step1));
        assertTrue(chatTest.getStep().contains(step3));
        
        // Remove step with id 1
        chatTest.removeStep(1L);
        assertEquals(1, chatTest.getStep().size());
        assertFalse(chatTest.getStep().contains(step1));
        assertTrue(chatTest.getStep().contains(step3));
        
        // Remove step with id 3
        chatTest.removeStep(3L);
        assertTrue(chatTest.getStep().isEmpty());
    }

    @Test
    @DisplayName("Test removeStep with non-existent id")
    public void testRemoveStepNonExistent() {
        ChatStep step1 = new ChatStep();
        step1.setId(1L);
        step1.setName("Step 1");
        
        chatTest.addStep(step1);
        assertEquals(1, chatTest.getStep().size());
        
        // Try to remove a step with an id that doesn't exist
        chatTest.removeStep(999L);
        assertEquals(1, chatTest.getStep().size());
        assertTrue(chatTest.getStep().contains(step1));
    }

    @Test
    @DisplayName("Test removeStep with null id in steps")
    public void testRemoveStepWithNullIds() {
        ChatStep step1 = new ChatStep();
        step1.setId(null);
        step1.setName("Step 1");
        
        ChatStep step2 = new ChatStep();
        step2.setId(2L);
        step2.setName("Step 2");
        
        chatTest.addStep(step1);
        chatTest.addStep(step2);
        assertEquals(2, chatTest.getStep().size());
        
        // Try to remove step with id 2
        chatTest.removeStep(2L);
        assertEquals(1, chatTest.getStep().size());
        assertTrue(chatTest.getStep().contains(step1));
        assertFalse(chatTest.getStep().contains(step2));
    }

    @Test
    @DisplayName("Test complete ChatTest workflow")
    public void testCompleteWorkflow() {
        Long id = 100L;
        String name = "Login Test";
        String description = "Test the login functionality";

        chatTest.setId(id);
        chatTest.setName(name);
        chatTest.setDescription(description);

        assertEquals(id, chatTest.getId());
        assertEquals(name, chatTest.getName());
        assertEquals(description, chatTest.getDescription());
        assertTrue(chatTest.getStep().isEmpty());

        // Add multiple steps
        ChatStep step1 = new ChatStep();
        step1.setId(1L);
        step1.setName("Navigate to login page");
        
        ChatStep step2 = new ChatStep();
        step2.setId(2L);
        step2.setName("Enter credentials");
        
        ChatStep step3 = new ChatStep();
        step3.setId(3L);
        step3.setName("Click login button");

        chatTest.addStep(step1);
        chatTest.addStep(step2);
        chatTest.addStep(step3);

        assertEquals(3, chatTest.getStep().size());
        assertEquals(step1, chatTest.getStep().get(0));
        assertEquals(step2, chatTest.getStep().get(1));
        assertEquals(step3, chatTest.getStep().get(2));

        // Remove middle step
        chatTest.removeStep(2L);
        assertEquals(2, chatTest.getStep().size());
        assertEquals(step1, chatTest.getStep().get(0));
        assertEquals(step3, chatTest.getStep().get(1));
    }
}