package fr.uha.ensisa.gl.chatest.dao.chatest;

public interface IDaoFactory {
    ITestDao getTestDao();
    ITestExecutionDao getTestExecutionDao();
}