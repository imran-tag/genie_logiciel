package fr.uha.ensisa.gl.chatest.dao.chatest;


public class DaoFactoryMem implements IDaoFactory {
    public final ITestDao testDao = new TestDaoMem();

    public ITestDao getTestDao() {
        return this.testDao;
    }
}
