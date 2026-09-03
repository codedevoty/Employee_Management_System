package payroll;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

public class SalarySlipGenerator {
    private EmployeeDAO dao;
    private PayrollService payrollService;

    public SalarySlipGenerator() {
        this.dao = new EmployeeDAO();
        this.payrollService = new PayrollService();
    }

    /**
     * Generate or regenerate payroll and create a text slip file in the local folder.
     * Returns path to generated file.
     */
    public String generateSlip(int empId, int month, int year) throws SQLException, IOException {
        // generate payroll (persist)
        PayrollRecord rec = payrollService.generatePayroll(empId, month, year);

        Employee e = dao.getEmployeeById(empId);
        if (e == null) throw new IllegalArgumentException("Employee not found");

        String fileName = "salary_slip_" + empId + "_" + year + "_" + month + ".txt";
        File out = new File(fileName);
        try (FileWriter fw = new FileWriter(out)) {
            fw.write("=== Salary Slip ===\n");
            fw.write("Employee ID: " + e.getId() + "\n");
            fw.write("Name: " + e.getName() + "\n");
            fw.write("Designation: " + e.getDesignation() + "\n");
            fw.write("Month/Year: " + month + "/" + year + "\n\n");
            fw.write(String.format("Gross Salary: %.2f\n", rec.getGross()));
            fw.write(String.format("Total Deductions: %.2f\n", rec.getDeductions()));
            fw.write(String.format("Net Salary: %.2f\n", rec.getNet()));
            fw.write("\nGenerated at: " + rec.getCreatedAt() + "\n");
        }
        return out.getAbsolutePath();
    }
}

