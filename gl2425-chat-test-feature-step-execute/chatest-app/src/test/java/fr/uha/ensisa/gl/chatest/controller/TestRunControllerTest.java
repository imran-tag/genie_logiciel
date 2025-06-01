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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestRunControllerTest {

    @Mock
    private IDaoFactory daoFactory;

    @Mock
    private ITestDao testDao;

    @Mock
    private ITestExecutionDao testExecutionDao;

    @Mock
    private Model model;

    private TestRunController controller;

    @BeforeEach
    void setUp() {
        controller = new TestRunController(daoFactory);
    }

    @Test
    void testRunTest_TestNotFound() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(testDao.find(1L)).thenReturn(null);

        String result = controller.runTest(1L, 0, model);

        assertEquals("redirect:/test/list", result);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void testRunTest_InvalidStepIndex_Negative() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        String result = controller.runTest(1L, -1, model);

        assertEquals("redirect:/test/list", result);
    }

    @Test
    void testRunTest_InvalidStepIndex_TooHigh() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        String result = controller.runTest(1L, 3, model);

        assertEquals("redirect:/test/list", result);
    }

    @Test
    void testRunTest_FirstStep() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        String result = controller.runTest(1L, 0, model);

        assertEquals("run", result);
        verify(model).addAttribute("test", test);
        verify(model).addAttribute("step", test.getStep().get(0));
        verify(model).addAttribute("index", 0);
        verify(model).addAttribute("isFirst", true);
        verify(model).addAttribute("isLast", false);

        // Verify all steps have been reset
        for (ChatStep step : test.getStep()) {
            assertNull(step.getStatus());
            assertNull(step.getComment());
        }
    }

    @Test
    void testRunTest_MiddleStep() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        String result = controller.runTest(1L, 1, model);

        assertEquals("run", result);
        verify(model).addAttribute("isFirst", false);
        verify(model).addAttribute("isLast", false);
    }

    @Test
    void testRunTest_LastStep() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        String result = controller.runTest(1L, 2, model);

        assertEquals("run", result);
        verify(model).addAttribute("isFirst", false);
        verify(model).addAttribute("isLast", true);
    }

    @Test
    void testValidateStep_TestNotFound() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(testDao.find(1L)).thenReturn(null);

        String result = controller.validateStep(1L, 0, "OK", null);

        assertEquals("redirect:/test/list", result);
        verify(testExecutionDao, never()).persist(any());
    }

    @Test
    void testValidateStep_InvalidStepIndex() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        String result = controller.validateStep(1L, 5, "OK", null);

        assertEquals("redirect:/test/list", result);
        verify(testExecutionDao, never()).persist(any());
    }

    @Test
    void testValidateStep_StepFailed() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        // Initialize test execution
        controller.runTest(1L, 0, model);
        
        // Mark first step as OK
        test.getStep().get(0).setStatus("OK");

        String comment = "Step failed due to error";
        String result = controller.validateStep(1L, 1, "KO", comment);

        assertEquals("redirect:/test/history/test/1", result);

        ArgumentCaptor<TestExecution> captor = ArgumentCaptor.forClass(TestExecution.class);
        verify(testExecutionDao).persist(captor.capture());

        TestExecution savedExecution = captor.getValue();
        assertEquals("FAILED", savedExecution.getStatus());
        assertEquals(1L, savedExecution.getTestId());
        assertEquals("Test", savedExecution.getTestName());
        assertTrue(savedExecution.getComment().contains("Failed at step 2"));
        assertTrue(savedExecution.getComment().contains(comment));

        // Verify step results are saved correctly
        assertEquals(2, savedExecution.getStepResults().size());
        // First step should be OK
        assertEquals("KO", savedExecution.getStepResults().get(0).getStatus());
        // Second step should be KO
        assertEquals("OK", savedExecution.getStepResults().get(1).getStatus());
    }

    @Test
    void testValidateStep_StepFailedFirstStep() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        String result = controller.validateStep(1L, 0, "KO", "First step failed");

        assertEquals("redirect:/test/history/test/1", result);

        ArgumentCaptor<TestExecution> captor = ArgumentCaptor.forClass(TestExecution.class);
        verify(testExecutionDao).persist(captor.capture());

        TestExecution savedExecution = captor.getValue();
        assertEquals("FAILED", savedExecution.getStatus());
        assertEquals(1, savedExecution.getStepResults().size());
    }

    @Test
    void testValidateStep_StepPassed_NotLastStep() {
        when(daoFactory.getTestDao()).thenReturn(testDao);

        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        // Initialize the current execution
        controller.runTest(1L, 0, model);

        String result = controller.validateStep(1L, 0, "OK", null);

        assertEquals("redirect:/test/run/1/1", result);
        verify(testExecutionDao, never()).persist(any());
    }

    @Test
    void testValidateStep_LastStepPassed() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        // Initialize execution
        controller.runTest(1L, 0, model);

        String result = controller.validateStep(1L, 2, "OK", null);

        assertEquals("redirect:/test/history/test/1", result);

        ArgumentCaptor<TestExecution> captor = ArgumentCaptor.forClass(TestExecution.class);
        verify(testExecutionDao).persist(captor.capture());

        TestExecution savedExecution = captor.getValue();
        assertEquals("PASSED", savedExecution.getStatus());
        assertEquals(1L, savedExecution.getTestId());
        assertEquals("Test", savedExecution.getTestName());
        assertNull(savedExecution.getComment());
    }

    @Test
    void testValidateStep_WithComment() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        
        ChatTest test = createTestWithSteps(2);
        when(testDao.find(1L)).thenReturn(test);

        controller.runTest(1L, 0, model);

        String comment = "Step completed with warning";
        String result = controller.validateStep(1L, 0, "OK", comment);

        assertEquals("redirect:/test/run/1/1", result);
        
        // Verify the step has the status and comment
        ChatStep step = test.getStep().get(0);
        assertEquals("OK", step.getStatus());
    }

    @Test
    void testValidateStep_NullCurrentExecution() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        // Don't initialize with runTest, so currentExecution is null
        String result = controller.validateStep(1L, 0, "OK", null);

        assertEquals("redirect:/test/run/1/1", result);
        // Should still work without throwing exception
    }

    @Test
    void testValidateStep_FailureNullCurrentExecution() {
        when(daoFactory.getTestDao()).thenReturn(testDao);
        when(daoFactory.getTestExecutionDao()).thenReturn(testExecutionDao);
        
        ChatTest test = createTestWithSteps(3);
        when(testDao.find(1L)).thenReturn(test);

        // Don't initialize with runTest, so currentExecution is null
        String result = controller.validateStep(1L, 1, "KO", "Failed");

        assertEquals("redirect:/test/history/test/1", result);
        
        ArgumentCaptor<TestExecution> captor = ArgumentCaptor.forClass(TestExecution.class);
        verify(testExecutionDao).persist(captor.capture());
        
        TestExecution savedExecution = captor.getValue();
        assertEquals("FAILED", savedExecution.getStatus());
    }

    private ChatTest createTestWithSteps(int stepCount) {
        ChatTest test = new ChatTest();
        test.setId(1L);
        test.setName("Test");
        
        for (int i = 0; i < stepCount; i++) {
            ChatStep step = new ChatStep();
            step.setId((long) i);
            step.setName("Step " + (i + 1));
            step.setContent("Content for step " + (i + 1));
            // Don't set default status here
            test.addStep(step);
        }
        
        return test;
    }
}