package fr.uha.ensisa.gl.chatest.dao.chatest;

public class DaoFactoryMem implements IDaoFactory {
    private final ITestDao testDao = new TestDaoMem();
    private final ITestExecutionDao testExecutionDao = new TestExecutionDaoMem();

    @Override
    public ITestDao getTestDao() {
        return this.testDao;
    }

    @Override
    public ITestExecutionDao getTestExecutionDao() {
        return this.testExecutionDao;
    }
}