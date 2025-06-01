package fr.uha.ensisa.gl.chatest.controller;

import fr.uha.ensisa.gl.chatest.ChatStep;
import fr.uha.ensisa.gl.chatest.ChatTest;
import fr.uha.ensisa.gl.chatest.TestExecution;
import fr.uha.ensisa.gl.chatest.dao.chatest.IDaoFactory;
import fr.uha.ensisa.gl.chatest.dao.chatest.ITestDao;
import fr.uha.ensisa.gl.chatest.dao.chatest.ITestExecutionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestExecutionControllerTest {

    @Mock
    private IDaoFactory daoFactory;

    @Mock
    private ITestExecutionDao testExecutionDao;

    @Mock
    private ITestDao testDao;

    @Mock
    private Model model;

    private TestExecutionController controller;

    @BeforeEach
    void setUp() {
        controller = new TestExecutionController(daoFactory);
    }

    @Test
    void testShowHistory_WithMixedExecutions() {
        // Setup mocks
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        
        // Create test executions with different statuses
        TestExecution failedExecution1 = createTestExecution(1L, "Test 1", "FAILED", new Date(1000));
        TestExecution passedExecution1 = createTestExecution(2L, "Test 2", "PASSED", new Date(2000));
        TestExecution failedExecution2 = createTestExecution(3L, "Test 3", "FAILED", new Date(3000));
        TestExecution passedExecution2 = createTestExecution(4L, "Test 4", "PASSED", new Date(4000));

        List<TestExecution> executions = Arrays.asList(
            failedExecution1, passedExecution1, failedExecution2, passedExecution2
        );

        when(testExecutionDao.findAll()).thenReturn(executions);

        String result = controller.showHistory(model);

        assertEquals("test/history", result);
        verify(model).addAttribute(eq("executions"), argThat(list -> {
            List<TestExecution> sortedList = (List<TestExecution>) list;
            // Check that failed tests come first
            assertEquals("FAILED", sortedList.get(0).getStatus());
            assertEquals("FAILED", sortedList.get(1).getStatus());
            assertEquals("PASSED", sortedList.get(2).getStatus());
            assertEquals("PASSED", sortedList.get(3).getStatus());
            // Check that within each status group, they are sorted by date (newest first)
            assertTrue(sortedList.get(0).getExecutionDate().after(sortedList.get(1).getExecutionDate()));
            assertTrue(sortedList.get(2).getExecutionDate().after(sortedList.get(3).getExecutionDate()));
            return true;
        }));
    }

    @Test
    void testShowHistory_EmptyExecutions() {
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        when(testExecutionDao.findAll()).thenReturn(Collections.emptyList());

        String result = controller.showHistory(model);

        assertEquals("test/history", result);
        verify(model).addAttribute("executions", Collections.emptyList());
    }

    @Test
    void testViewExecution_ExecutionNotFound() {
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        when(testExecutionDao.find(1L)).thenReturn(null);

        String result = controller.viewExecution(1L, model);

        assertEquals("redirect:/test/history", result);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void testViewExecution_Success() {
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        when(daoFactory.getTestDao()).thenReturn(testDao);
        
        TestExecution execution = createTestExecution(1L, "Test 1", "PASSED", new Date());
        execution.setTestId(10L);
        ChatTest test = new ChatTest();
        test.setId(10L);
        test.setName("Test 1");

        when(testExecutionDao.find(1L)).thenReturn(execution);
        when(testDao.find(10L)).thenReturn(test);

        String result = controller.viewExecution(1L, model);

        assertEquals("test/execution-details", result);
        verify(model).addAttribute("execution", execution);
        verify(model).addAttribute("test", test);
        verify(model, never()).addAttribute(eq("noStepResults"), anyBoolean());
    }

    @Test
    void testViewExecution_WithNoStepResults() {
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        when(daoFactory.getTestDao()).thenReturn(testDao);
        
        TestExecution execution = createTestExecution(1L, "Test 1", "PASSED", new Date());
        execution.setTestId(10L);
        execution.setStepResults(null); // No step results
        ChatTest test = new ChatTest();
        test.setId(10L);

        when(testExecutionDao.find(1L)).thenReturn(execution);
        when(testDao.find(10L)).thenReturn(test);

        String result = controller.viewExecution(1L, model);

        assertEquals("test/execution-details", result);
        verify(model).addAttribute("noStepResults", true);
    }

    @Test
    void testViewExecution_WithEmptyStepResults() {
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        when(daoFactory.getTestDao()).thenReturn(testDao);
        
        TestExecution execution = createTestExecution(1L, "Test 1", "PASSED", new Date());
        execution.setTestId(10L);
        execution.setStepResults(new ArrayList<>()); // Empty step results
        ChatTest test = new ChatTest();
        test.setId(10L);

        when(testExecutionDao.find(1L)).thenReturn(execution);
        when(testDao.find(10L)).thenReturn(test);

        String result = controller.viewExecution(1L, model);

        assertEquals("test/execution-details", result);
        verify(model).addAttribute("noStepResults", true);
    }

    @Test
    void testViewTestExecutions_TestNotFound() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(testDao.find(1L)).thenReturn(null);

        String result = controller.viewTestExecutions(1L, model);

        assertEquals("redirect:/test/history", result);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void testViewTestExecutions_Success() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        
        ChatTest test = new ChatTest();
        test.setId(1L);
        test.setName("Test 1");

        TestExecution failedExecution = createTestExecution(1L, "Test 1", "FAILED", new Date(1000));
        TestExecution passedExecution = createTestExecution(2L, "Test 1", "PASSED", new Date(2000));

        List<TestExecution> executions = Arrays.asList(failedExecution, passedExecution);

        when(testDao.find(1L)).thenReturn(test);
        when(testExecutionDao.findByTestId(1L)).thenReturn(executions);

        String result = controller.viewTestExecutions(1L, model);

        assertEquals("test/test-history", result);
        verify(model).addAttribute("test", test);
        verify(model).addAttribute(eq("executions"), argThat(list -> {
            List<TestExecution> sortedList = (List<TestExecution>) list;
            // Check that failed test comes first
            assertEquals("FAILED", sortedList.get(0).getStatus());
            assertEquals("PASSED", sortedList.get(1).getStatus());
            return true;
        }));
    }

    @Test
    void testViewTestExecutions_EmptyExecutions() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        
        ChatTest test = new ChatTest();
        test.setId(1L);
        test.setName("Test 1");

        when(testDao.find(1L)).thenReturn(test);
        when(testExecutionDao.findByTestId(1L)).thenReturn(Collections.emptyList());

        String result = controller.viewTestExecutions(1L, model);

        assertEquals("test/test-history", result);
        verify(model).addAttribute("test", test);
        verify(model).addAttribute("executions", Collections.emptyList());
    }

    @Test
    void testViewTestExecutions_SameStatusSortedByDate() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        
        ChatTest test = new ChatTest();
        test.setId(1L);

        TestExecution older = createTestExecution(1L, "Test", "PASSED", new Date(1000));
        TestExecution newer = createTestExecution(2L, "Test", "PASSED", new Date(2000));

        when(testDao.find(1L)).thenReturn(test);
        when(testExecutionDao.findByTestId(1L)).thenReturn(Arrays.asList(older, newer));

        String result = controller.viewTestExecutions(1L, model);

        assertEquals("test/test-history", result);
        verify(model).addAttribute(eq("executions"), argThat(list -> {
            List<TestExecution> sortedList = (List<TestExecution>) list;
            // Newer should come first
            assertEquals(2L, sortedList.get(0).getId().longValue());
            assertEquals(1L, sortedList.get(1).getId().longValue());
            return true;
        }));
    }

    private TestExecution createTestExecution(Long id, String testName, String status, Date date) {
        TestExecution execution = new TestExecution();
        execution.setId(id);
        execution.setTestName(testName);
        execution.setStatus(status);
        execution.setExecutionDate(date);
        
        // Add some step results
        TestExecution.StepResult stepResult = new TestExecution.StepResult(1L, "Step 1", "OK", null);
        execution.addStepResult(stepResult);
        
        return execution;
    }
}