package fr.uha.ensisa.gl.chatest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.chatest.dao.chatest.IDaoFactory;
import fr.uha.ensisa.gl.chatest.ChatTest;
import fr.uha.ensisa.gl.chatest.ChatStep;

@Controller
public class TestStepList {
    @Autowired
    private IDaoFactory daoFactory;

    @RequestMapping(value = "/test/steps/list/{id}", method = RequestMethod.GET)
    public ModelAndView create(@PathVariable long id) {
        ModelAndView ret = new ModelAndView("test/steps/list");
        ret.addObject("steps", daoFactory.getTestDao().findSteps(id));
        ret.addObject("name", daoFactory.getTestDao().find(id).getName());
        return ret;
    }

    @RequestMapping(value = "/test/steps/list/{id}", method = RequestMethod.POST)
    public ModelAndView create(@PathVariable long id, @RequestParam String name, @RequestParam String description) {
        ChatStep step = new ChatStep();
        step.setName(name);
        step.setStep(description);
        daoFactory.getTestDao().addStep(id, step);
        return new ModelAndView("redirect:/test/steps/list/" + id);
    }

    @RequestMapping(value = "/test/steps/remove/{testid}/{stepid}", method = RequestMethod.GET)
    public ModelAndView remove(@PathVariable long testid, @PathVariable long stepid) {
        try {
            daoFactory.getTestDao().removeStep(testid, stepid);
        } catch (Exception e) {
            return new ModelAndView("redirect:/");
        }
        return new ModelAndView("redirect:/test/steps/list/" + testid);
    }
}