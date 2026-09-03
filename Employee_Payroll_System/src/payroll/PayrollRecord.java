package payroll;

import java.sql.Timestamp;

public class PayrollRecord {
    private int empId;
    private int month;
    private int year;
    private double gross;
    private double deductions;
    private double net;
    private Timestamp createdAt;

    public PayrollRecord(int empId, int month, int year, double gross, double deductions, double net, Timestamp createdAt) {
        this.empId = empId; this.month = month; this.year = year; this.gross = gross; this.deductions = deductions; this.net = net; this.createdAt = createdAt;
    }

    // getters
    public int getEmpId(){ return empId; }
    public int getMonth(){ return month; }
    public int getYear(){ return year; }
    public double getGross(){ return gross; }
    public double getDeductions(){ return deductions; }
    public double getNet(){ return net; }
    public Timestamp getCreatedAt(){ return createdAt; }
}
