package payroll;

import java.sql.SQLException;

public class RemoveService {
    private EmployeeDAO dao;

    public RemoveService() {
        this.dao = new EmployeeDAO();
    }


    public boolean removeEmployeeByName(String name) throws SQLException {
        return dao.removeEmployeeByName(name);
    }
}
