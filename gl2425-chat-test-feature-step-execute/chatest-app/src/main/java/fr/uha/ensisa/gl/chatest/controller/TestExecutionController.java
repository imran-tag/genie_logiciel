package fr.uha.ensisa.gl.chatest.controller;

import fr.uha.ensisa.gl.chatest.ChatTest;
import fr.uha.ensisa.gl.chatest.TestExecution;
import fr.uha.ensisa.gl.chatest.dao.chatest.IDaoFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/test/history")
public class TestExecutionController {

    private final IDaoFactory daoFactory;

    public TestExecutionController(IDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @GetMapping
    public String showHistory(Model model) {
        // Get all test executions and sort them: failed first, then by date (newest first)
        List<TestExecution> executions = daoFactory.getTestExecutionDao().findAll().stream()
                .sorted(Comparator
                        .comparing(TestExecution::getStatus).reversed() // "FAILED" comes before "PASSED" alphabetically
                        .thenComparing(TestExecution::getExecutionDate, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        model.addAttribute("executions", executions);
        return "test/history";
    }

    @GetMapping("/{id}")
    public String viewExecution(@PathVariable("id") long executionId, Model model) {
        TestExecution execution = daoFactory.getTestExecutionDao().find(executionId);
        if (execution == null) {
            return "redirect:/test/history";
        }

        // Get the test details
        ChatTest test = daoFactory.getTestDao().find(execution.getTestId());
        
        model.addAttribute("execution", execution);
        model.addAttribute("test", test);
        return "test/execution-details";
    }

    @GetMapping("/test/{id}")
    public String viewTestExecutions(@PathVariable("id") long testId, Model model) {
        ChatTest test = daoFactory.getTestDao().find(testId);
        if (test == null) {
            return "redirect:/test/history";
        }
        
        // Get all executions for this test
        List<TestExecution> executions = daoFactory.getTestExecutionDao().findByTestId(testId).stream()
                .sorted(Comparator
                        .comparing(TestExecution::getStatus).reversed()
                        .thenComparing(TestExecution::getExecutionDate, Comparator.reverseOrder()))
                .collect(Collectors.toList());
        
        model.addAttribute("test", test);
        model.addAttribute("executions", executions);
        return "test/test-history";
    }
}