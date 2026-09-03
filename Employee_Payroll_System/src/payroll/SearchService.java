package payroll;

import java.sql.SQLException;
import java.util.List;

public class SearchService {
    private EmployeeDAO dao;

    public SearchService() {
        this.dao = new EmployeeDAO();
    }

    public List<Employee> searchByName(String namePart) throws SQLException {
        return dao.searchByName(namePart);
    }

    public Employee findById(int id) throws SQLException {
        return dao.getEmployeeById(id);
    }
}

