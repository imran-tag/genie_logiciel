package fr.uha.ensisa.gl.chatest.dao.chatest;

import fr.uha.ensisa.gl.chatest.ChatStep;
import fr.uha.ensisa.gl.chatest.ChatTest;

import java.util.Collection;
import java.util.List;

public interface ITestDao {
    public void persist(ChatTest test);

    public void remove(long id);

    public ChatTest find(long id);

    public Collection<ChatTest> findAll();

    public List<ChatStep> findSteps(long testId);

    public void addStep(long testId, ChatStep step);

    public void removeStep(long testId, long stepId);

    public long count();
}
