package fr.uha.ensisa.gl.chatest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    @DisplayName("Test Main class main method")
    public void testMain() {
        String[] args = {};
        
        // This should not throw any exception
        assertDoesNotThrow(() -> Main.main(args));
        
        // Verify the expected output
        assertEquals("Hello world!", outputStreamCaptor.toString().trim());
    }

    @Test
    @DisplayName("Test Main class main method with arguments")
    public void testMainWithArguments() {
        String[] args = {"arg1", "arg2", "arg3"};
        
        // This should not throw any exception even with arguments
        assertDoesNotThrow(() -> Main.main(args));
        
        // Output should be the same regardless of arguments
        assertEquals("Hello world!", outputStreamCaptor.toString().trim());
    }

    @Test
    @DisplayName("Test Main class main method with null arguments")
    public void testMainWithNullArguments() {
        String[] args = null;
        
        // This should not throw any exception
        assertDoesNotThrow(() -> Main.main(args));
        
        // Output should be the same
        assertEquals("Hello world!", outputStreamCaptor.toString().trim());
    }

    @Test
    @DisplayName("Test Main class constructor")
    public void testMainConstructor() {
        // Test that Main class can be instantiated
        assertDoesNotThrow(() -> {
            Main main = new Main();
            assertNotNull(main);
        });
    }
}