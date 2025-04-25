package fr.uha.ensisa.gl.chatest.dao.chatest;


import fr.uha.ensisa.gl.chatest.ChatStep;
import fr.uha.ensisa.gl.chatest.ChatTest;

import java.util.*;

public class TestDaoMem implements ITestDao {
    private final Map<Long, ChatTest> store = Collections.synchronizedMap(new TreeMap<>());

    public void persist(ChatTest test) {
        store.put(test.getId(), test);
    }

    public void remove(long id) {
        store.remove(id);
    }

    public ChatTest find(long id) {
        return store.get(id);
    }

    public Collection<ChatTest> findAll() {
        return store.values();
    }

    public long count() {
        return store.size();
    }

    public void addStep(long testId, ChatStep step) {
        ChatTest test = store.get(testId);
        //using the id of last step as the id of the new step +1
        long id = test.getStep().size() - 1 >= 0 ? test.getStep().get(test.getStep().size() - 1).getId() + 1 : 0;
        step.setId(id);
        if (test != null) {
            test.addStep(step);
        }
    }

    public List<ChatStep> findSteps(long testId) {
        ChatTest test = store.get(testId);
        if (test != null) {
            return test.getStep();
        }
        return null;
    }

    public void removeStep(long testId, long stepId) {
        ChatTest test = store.get(testId);
        if (test != null) {
            test.removeStep(stepId);
        }
    }
}
