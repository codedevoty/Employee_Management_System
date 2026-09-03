package payroll;

import java.sql.SQLException;

public class UpdateService {
    private EmployeeDAO dao;

    public UpdateService() {
        this.dao = new EmployeeDAO();
    }

    public boolean updateBasicInfo(int id, String name, String email, String phone, Integer deptId, String designation) throws SQLException {
        return dao.updateEmployeeBasic(id, name, email, phone, deptId, designation);
    }

    public boolean updateFullTimeSalary(int id, double monthlySalary, double hra, double da, double pfPercent, double bonus) throws SQLException {
        return dao.updateFullTimeSalary(id, monthlySalary, hra, da, pfPercent, bonus);
    }

    public boolean updatePartTimeRates(int id, int hoursWorked, double hourlyRate, double overtimeRate) throws SQLException {
        return dao.updatePartTimeRates(id, hoursWorked, hourlyRate, overtimeRate);
    }
}

