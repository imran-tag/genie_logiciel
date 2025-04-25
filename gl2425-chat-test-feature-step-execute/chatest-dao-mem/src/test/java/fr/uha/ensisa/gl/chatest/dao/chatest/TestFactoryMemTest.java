package fr.uha.ensisa.gl.chatest.dao.chatest;

import fr.uha.ensisa.gl.chatest.ChatStep;
import fr.uha.ensisa.gl.chatest.ChatTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFactoryMemTest {
    private TestDaoMem testDaoMem;

    @BeforeEach
    public void setUp() {
        testDaoMem = new TestDaoMem();
    }

    @Test
    public void testPersist() {
        ChatTest test = new ChatTest();
        test.setId(1L);
        testDaoMem.persist(test);
        assertEquals(test, testDaoMem.find(1L));
    }

    @Test
    public void testRemove() {
        ChatTest test = new ChatTest();
        test.setId(1L);
        testDaoMem.persist(test);
        testDaoMem.remove(1L);
        assertNull(testDaoMem.find(1L));
    }

    @Test
    public void testFind() {
        ChatTest test = new ChatTest();
        test.setId(1L);
        testDaoMem.persist(test);
        assertEquals(test, testDaoMem.find(1L));
    }

    @Test
    public void testFindAll() {
        ChatTest test1 = new ChatTest();
        test1.setId(1L);
        ChatTest test2 = new ChatTest();
        test2.setId(2L);
        testDaoMem.persist(test1);
        testDaoMem.persist(test2);
        Collection<ChatTest> tests = testDaoMem.findAll();
        assertTrue(tests.contains(test1));
        assertTrue(tests.contains(test2));
    }

    @Test
    public void testCount() {
        ChatTest test1 = new ChatTest();
        test1.setId(1L);
        ChatTest test2 = new ChatTest();
        test2.setId(2L);
        testDaoMem.persist(test1);
        testDaoMem.persist(test2);
        assertEquals(2, testDaoMem.count());
    }

    @Test
    public void testAddStep() {
        ChatTest test = new ChatTest();
        test.setId(1L);
        testDaoMem.persist(test);
        ChatStep step = new ChatStep();
        testDaoMem.addStep(1L, step);
        assertTrue(test.getStep().contains(step));
    }

    @Test
    public void testFindSteps() {
        ChatTest test = new ChatTest();
        test.setId(1L);
        testDaoMem.persist(test);
        ChatStep step = new ChatStep();
        testDaoMem.addStep(1L, step);
        List<ChatStep> steps = testDaoMem.findSteps(1L);
        assertTrue(steps.contains(step));
    }
}
