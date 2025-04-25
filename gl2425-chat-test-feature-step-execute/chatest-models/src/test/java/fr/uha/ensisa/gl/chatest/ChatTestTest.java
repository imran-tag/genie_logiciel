package fr.uha.ensisa.gl.chatest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ChatTestTest {
    ChatTest tst;

    @BeforeEach
    public void setUp() {
        tst = new ChatTest();
    }

    @Test
    @DisplayName("Test must have a name")
    public void setName() {
        assertNull(tst.getName());
        String name = "A sample test name";
        tst.setName(name);
        assertEquals(name, tst.getName());
    }
}
