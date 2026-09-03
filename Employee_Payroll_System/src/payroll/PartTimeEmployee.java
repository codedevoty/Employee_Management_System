package payroll;

public class PartTimeEmployee extends Employee {
    private int hoursWorked;
    private double hourlyRate;
    private double overtimeRate;

    public PartTimeEmployee(int id, String name, int hoursWorked, double hourlyRate) {
        super(id, name, EmployeeType.PART_TIME);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    // getters & setters
    public int getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(int hoursWorked) { this.hoursWorked = hoursWorked; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public double getOvertimeRate() { return overtimeRate; }
    public void setOvertimeRate(double overtimeRate) { this.overtimeRate = overtimeRate; }

    @Override
    public double calculateSalary() {
        // normal pay + some overtime if hours beyond 160 (example)
        int normalHours = Math.min(hoursWorked, 160);
        int overtimeHours = Math.max(0, hoursWorked - 160);
        return normalHours * hourlyRate + overtimeHours * (overtimeRate > 0 ? overtimeRate : hourlyRate * 1.5);
    }
}

