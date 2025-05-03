package fr.uha.ensisa.gl.chatest.dao.chatest;

import fr.uha.ensisa.gl.chatest.TestExecution;

import java.util.Collection;

public interface ITestExecutionDao {
    void persist(TestExecution execution);
    void remove(long id);
    TestExecution find(long id);
    Collection<TestExecution> findAll();
    Collection<TestExecution> findByStatus(String status);
    Collection<TestExecution> findByTestId(long testId);
    long count();
}