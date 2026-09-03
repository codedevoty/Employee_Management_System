package payroll;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {


    private static final String userName = "root";
    private static final String password = "VS04@kbps";
    // Insert employee parent row
    public void addEmployee(Employee e) throws SQLException {

        String empSql = "INSERT INTO employees (id, name, email, phone, hire_date, department_id, type, designation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(empSql)) {
            ps.setInt(1, e.getId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getPhone());
            if (e.getDepartmentId() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, e.getDepartmentId());
            }
            // hire_date - set null for now or set today's date
            ps.setDate(5, null);
            ps.setString(7, e.getType().name());
            ps.setString(8, e.getDesignation());
            ps.executeUpdate();
        }

        // subtype
        if (e instanceof FullTimeEmployee) {
            insertFullTime((FullTimeEmployee)e);
        } else if (e instanceof PartTimeEmployee) {
            insertPartTime((PartTimeEmployee)e);
        }
    }

    private void insertFullTime(FullTimeEmployee f) throws SQLException {
        String sql = "INSERT INTO full_time_employees (id, monthly_salary, hra, da, pf_percentage, bonus) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, f.getId());
            ps.setDouble(2, f.getMonthlySalary());
            ps.setDouble(3, f.getHra());
            ps.setDouble(4, f.getDa());
            ps.setDouble(5, f.getPfPercentage());
            ps.setDouble(6, f.getBonus());
            ps.executeUpdate();
        }
    }

    private void insertPartTime(PartTimeEmployee p) throws SQLException {
        String sql = "INSERT INTO part_time_employees (id, hours_worked, hourly_rate, overtime_rate) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getId());
            ps.setInt(2, p.getHoursWorked());
            ps.setDouble(3, p.getHourlyRate());
            ps.setDouble(4, p.getOvertimeRate());
            ps.executeUpdate();
        }
    }

    public boolean removeEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public boolean updateEmployeeBasic(int id, String name, String email, String phone, Integer deptId, String designation) throws SQLException {
        String sql = "UPDATE employees SET name=?, email=?, phone=?, department_id=?, designation=? WHERE id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            if (deptId == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, deptId);
            ps.setString(5, designation);
            ps.setInt(6, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public boolean updateFullTimeSalary(int id, double monthlySalary, double hra, double da, double pfPercentage, double bonus) throws SQLException {
        String sql = "UPDATE full_time_employees SET monthly_salary=?, hra=?, da=?, pf_percentage=?, bonus=? WHERE id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, monthlySalary);
            ps.setDouble(2, hra);
            ps.setDouble(3, da);
            ps.setDouble(4, pfPercentage);
            ps.setDouble(5, bonus);
            ps.setInt(6, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public boolean updatePartTimeRates(int id, int hoursWorked, double hourlyRate, double overtimeRate) throws SQLException {
        String sql = "UPDATE part_time_employees SET hours_worked=?, hourly_rate=?, overtime_rate=? WHERE id=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hoursWorked);
            ps.setDouble(2, hourlyRate);
            ps.setDouble(3, overtimeRate);
            ps.setInt(4, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public Employee getEmployeeById(int id) throws SQLException {
        String sql = "SELECT e.id, e.name, e.email, e.phone, e.type, e.designation, f.monthly_salary, f.hra, f.da, f.pf_percentage, f.bonus, p.hours_worked, p.hourly_rate, p.overtime_rate " +
                "FROM employees e " +
                "LEFT JOIN full_time_employees f ON e.id = f.id " +
                "LEFT JOIN part_time_employees p ON e.id = p.id " +
                "WHERE e.id = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String type = rs.getString("type");
                    if ("FULL_TIME".equals(type)) {
                        FullTimeEmployee f = new FullTimeEmployee(id, name, rs.getDouble("monthly_salary"));
                        f.setHra(rs.getDouble("hra"));
                        f.setDa(rs.getDouble("da"));
                        f.setPfPercentage(rs.getDouble("pf_percentage"));
                        f.setBonus(rs.getDouble("bonus"));
                        f.setEmail(rs.getString("email"));
                        f.setPhone(rs.getString("phone"));
                        f.setDesignation(rs.getString("designation"));
                        return f;
                    } else {
                        PartTimeEmployee p = new PartTimeEmployee(id, name, rs.getInt("hours_worked"), rs.getDouble("hourly_rate"));
                        p.setOvertimeRate(rs.getDouble("overtime_rate"));
                        p.setEmail(rs.getString("email"));
                        p.setPhone(rs.getString("phone"));
                        p.setDesignation(rs.getString("designation"));
                        return p;
                    }
                }
            }
        }
        return null;
    }

    public List<Employee> searchByName(String namePattern) throws SQLException {
        String sql = "SELECT e.id FROM employees e WHERE e.name LIKE ?";
        List<Employee> results = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + namePattern + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Employee e = getEmployeeById(id);
                    if (e != null) results.add(e);
                }
            }
        }
        return results;
    }

    public boolean removeEmployeeByName(String name) throws SQLException {
        String sql = "DELETE FROM employees WHERE name = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public List<Employee> listAll() throws SQLException {
        String sql = "SELECT id FROM employees";
        List<Employee> list = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                Employee e = getEmployeeById(id);
                if (e != null) list.add(e);
            }
        }
        return list;
    }

    // payroll insert (history)
    public void insertPayrollRecord(int empId, int month, int year, double gross, double deductions, double net) throws SQLException {
        String sql = "INSERT INTO payroll (emp_id, month, year, gross_salary, total_deductions, net_salary) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ps.setDouble(4, gross);
            ps.setDouble(5, deductions);
            ps.setDouble(6, net);
            ps.executeUpdate();
        }
    }

    public PayrollRecord getPayrollRecord(int empId, int month, int year) throws SQLException {
        String sql = "SELECT gross_salary, total_deductions, net_salary, created_at FROM payroll WHERE emp_id=? AND month=? AND year=?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double gross = rs.getDouble("gross_salary");
                    double deductions = rs.getDouble("total_deductions");
                    double net = rs.getDouble("net_salary");
                    Timestamp created = rs.getTimestamp("created_at");
                    return new PayrollRecord(empId, month, year, gross, deductions, net, created);
                }
            }
        }
        return null;
    }
}

