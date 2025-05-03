package fr.uha.ensisa.gl.chatest.controller;

import fr.uha.ensisa.gl.chatest.ChatStep;
import fr.uha.ensisa.gl.chatest.ChatTest;
import fr.uha.ensisa.gl.chatest.TestExecution;
import fr.uha.ensisa.gl.chatest.dao.chatest.IDaoFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/test/run")
public class TestRunController {

    private final IDaoFactory daoFactory;
    // Store the current test execution in progress
    private TestExecution currentExecution;

    public TestRunController(IDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @GetMapping("/{id}/{stepIndex}")
    public String runTest(
            @PathVariable("id") long testId,
            @PathVariable("stepIndex") int stepIndex,
            Model model) {

        ChatTest test = daoFactory.getTestDao().find(testId);
        if (test == null) {
            return "redirect:/test/list";
        }

        List<ChatStep> steps = test.getStep();

        if (stepIndex < 0 || stepIndex >= steps.size()) {
            return "redirect:/test/list";
        }

        // Start a new test execution when beginning at step 0
        if (stepIndex == 0) {
            currentExecution = new TestExecution();
            currentExecution.setTestId(testId);
            currentExecution.setTestName(test.getName());
            
            // Reset all step statuses for display purposes
            for (ChatStep step : steps) {
                step.setStatus(null);
                step.setComment(null);
            }
        }

        model.addAttribute("test", test);
        model.addAttribute("step", steps.get(stepIndex));
        model.addAttribute("index", stepIndex);
        model.addAttribute("isFirst", stepIndex == 0);
        model.addAttribute("isLast", stepIndex == steps.size() - 1);

        return "run";
    }

    @PostMapping("/{id}/{stepIndex}/validate")
    public String validateStep(
            @PathVariable("id") long testId,
            @PathVariable("stepIndex") int stepIndex,
            @RequestParam("status") String status,
            @RequestParam(value = "comment", required = false) String comment
    ) {
        ChatTest test = daoFactory.getTestDao().find(testId);
        if (test == null) {
            return "redirect:/test/list";
        }

        List<ChatStep> steps = test.getStep();
        if (stepIndex < 0 || stepIndex >= steps.size()) {
            return "redirect:/test/list";
        }

        ChatStep step = steps.get(stepIndex);
        step.setStatus(status);
        
        if ("KO".equals(status)) {
            step.setComment(comment);
            
            // Save this step result in the current execution
            TestExecution.StepResult stepResult = new TestExecution.StepResult(
                step.getId(), step.getName(), status, comment
            );
            
            // Ensure currentExecution is initialized
            if (currentExecution == null) {
                currentExecution = new TestExecution(testId, test.getName(), "FAILED");
            } else {
                currentExecution.setStatus("FAILED");
            }
            
            currentExecution.addStepResult(stepResult);
            
            // Add all previous steps that were OK
            for (int i = 0; i < stepIndex; i++) {
                ChatStep prevStep = steps.get(i);
                TestExecution.StepResult prevResult = new TestExecution.StepResult(
                    prevStep.getId(), prevStep.getName(), prevStep.getStatus(), prevStep.getComment()
                );
                currentExecution.addStepResult(prevResult);
            }
            
            // Set the comment for the execution
            currentExecution.setComment("Failed at step " + (stepIndex + 1) + ": " + step.getName() + 
                              (comment != null ? " - " + comment : ""));
            
            // Save the execution
            daoFactory.getTestExecutionDao().persist(currentExecution);
            
            // Redirect to test history
            return "redirect:/test/history/test/" + testId;
        }

        // Add this step result to the current execution
        TestExecution.StepResult stepResult = new TestExecution.StepResult(
            step.getId(), step.getName(), status, comment
        );
        
        // Ensure currentExecution is initialized
        if (currentExecution == null) {
            currentExecution = new TestExecution(testId, test.getName(), "PASSED");
        }
        
        currentExecution.addStepResult(stepResult);

        // If this is the last step and status is OK, the test is passed
        if (stepIndex == steps.size() - 1) {
            currentExecution.setStatus("PASSED");
            
            // Save the execution
            daoFactory.getTestExecutionDao().persist(currentExecution);
            
            return "redirect:/test/history/test/" + testId;
        }

        // Otherwise, move to the next step
        return "redirect:/test/run/" + testId + "/" + (stepIndex + 1);
    }
}