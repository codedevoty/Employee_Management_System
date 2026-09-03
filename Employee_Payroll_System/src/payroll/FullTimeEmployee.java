package payroll;

public class FullTimeEmployee extends Employee {
    private double monthlySalary;
    private double hra; // house rent allowance
    private double da;  // dearness allowance
    private double pfPercentage; // provident fund percentage
    private double bonus;

    public FullTimeEmployee(int id, String name, double monthlySalary) {
        super(id, name, EmployeeType.FULL_TIME);
        this.monthlySalary = monthlySalary;
    }

    // Getters & Setters
    public double getMonthlySalary() { return monthlySalary; }
    public void setMonthlySalary(double monthlySalary) { this.monthlySalary = monthlySalary; }

    public double getHra() { return hra; }
    public void setHra(double hra) { this.hra = hra; }

    public double getDa() { return da; }
    public void setDa(double da) { this.da = da; }

    public double getPfPercentage() { return pfPercentage; }
    public void setPfPercentage(double pfPercentage) { this.pfPercentage = pfPercentage; }

    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }

    @Override
    public double calculateSalary() {
        // Gross salary = base + hra + da + bonus
        double grossSalary = monthlySalary + hra + da + bonus;

        // PF amount = (pfPercentage / 100) * monthlySalary
        double pfAmount = (pfPercentage / 100) * monthlySalary;

        // Net salary = gross - PF
        double netSalary = grossSalary - pfAmount;

        return netSalary;
    }
}
