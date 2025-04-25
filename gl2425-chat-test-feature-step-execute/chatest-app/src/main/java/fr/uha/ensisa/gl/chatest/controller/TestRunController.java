package fr.uha.ensisa.gl.chatest.controller;

import fr.uha.ensisa.gl.chatest.ChatStep;
import fr.uha.ensisa.gl.chatest.ChatTest;
import fr.uha.ensisa.gl.chatest.dao.chatest.IDaoFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/test/run")
public class TestRunController {

    private final IDaoFactory daoFactory;

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

        ChatStep step = test.getStep().get(stepIndex);
        step.setStatus(status);
        if ("KO".equals(status)) {
            step.setComment(comment);
        }

        if ("OK".equals(status) && stepIndex + 1 < test.getStep().size()) {
            return "redirect:/test/run/" + testId + "/" + (stepIndex + 1);
        }

        return "redirect:/test/run/" + testId + "/" + stepIndex;
    }
}
