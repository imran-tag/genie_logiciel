package fr.uha.ensisa.gl.chatest.dao.chatest;

import fr.uha.ensisa.gl.chatest.TestExecution;

import java.util.*;
import java.util.stream.Collectors;

public class TestExecutionDaoMem implements ITestExecutionDao {
    private final Map<Long, TestExecution> store = Collections.synchronizedMap(new TreeMap<>());
    private long nextId = 0;

    @Override
    public void persist(TestExecution execution) {
        if (execution.getId() == null) {
            execution.setId(nextId++);
        }
        store.put(execution.getId(), execution);
    }

    @Override
    public void remove(long id) {
        store.remove(id);
    }

    @Override
    public TestExecution find(long id) {
        return store.get(id);
    }

    @Override
    public Collection<TestExecution> findAll() {
        return store.values();
    }

    @Override
    public Collection<TestExecution> findByStatus(String status) {
        return store.values().stream()
                .filter(execution -> status.equals(execution.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<TestExecution> findByTestId(long testId) {
        return store.values().stream()
                .filter(execution -> execution.getTestId() == testId)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return store.size();
    }
}