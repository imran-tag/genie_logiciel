package fr.uha.ensisa.gl.chatest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.chatest.dao.chatest.IDaoFactory;
import fr.uha.ensisa.gl.chatest.ChatTest;

@Controller
public class TestCreateController {
    private static long counter = 0;
    @Autowired
    private IDaoFactory daoFactory;

    @RequestMapping(value = "/test/create", method = RequestMethod.GET)
    public ModelAndView create() {
        ModelAndView ret = new ModelAndView("test/create");
        return ret;
    }

    @RequestMapping(value = "/test/create", method = RequestMethod.POST)
    public ModelAndView create(@RequestParam String name, @RequestParam String description) {
        ChatTest test = new ChatTest();
        test.setName(name);
        test.setDescription(description);
        test.setId(counter++);

        long testid = daoFactory.getTestDao().count();
        daoFactory.getTestDao().persist(test);
        return new ModelAndView("redirect:/test/steps/list/" + testid);
    }
}