package payroll;

import java.sql.SQLException;

public class PayrollService {
    private EmployeeDAO dao;

    public PayrollService() {
        this.dao = new EmployeeDAO();
    }

    /**
     * Compute payroll for a single employee for given month/year and persist in payroll table.
     * Returns the created PayrollRecord.
     */
    public PayrollRecord generatePayroll(int empId, int month, int year) throws SQLException {
        Employee e = dao.getEmployeeById(empId);
        if (e == null) throw new IllegalArgumentException("Employee not found: " + empId);

        double gross = 0;
        double deductions = 0;

        if (e instanceof FullTimeEmployee) {
            FullTimeEmployee f = (FullTimeEmployee) e;
            // Business logic:
            // gross = base + hra + da + bonus
            gross = f.getMonthlySalary() + f.getHra() + f.getDa() + f.getBonus();
            // deductions: PF = pfPercentage% of base; tax simple slab (example)
            double pf = f.getMonthlySalary() * f.getPfPercentage() / 100.0;
            double tax = computeTax(gross);
            deductions = pf + tax;
        } else if (e instanceof PartTimeEmployee) {
            PartTimeEmployee p = (PartTimeEmployee) e;
            gross = p.calculateSalary(); // includes overtime logic
            // deductions: simple tax 5% on gross for example
            double tax = gross * 0.05;
            deductions = tax;
        } else {
            throw new IllegalStateException("Unknown employee type");
        }

        double net = gross - deductions;

        // persist payroll record (unique per emp-month-year)
        dao.insertPayrollRecord(empId, month, year, gross, deductions, net);

        return dao.getPayrollRecord(empId, month, year);
    }

    private double computeTax(double gross) {
        // simple progressive example
        if (gross <= 25000) return 0;
        if (gross <= 50000) return (gross - 25000) * 0.05;
        if (gross <= 100000) return (25000 * 0.05) + (gross - 50000) * 0.1;
        return (25000 * 0.05) + (50000 * 0.1) + (gross - 100000) * 0.2;
    }
}

