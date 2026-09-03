package payroll;


public abstract class Employee {
    public enum EmployeeType {
        FULL_TIME, PART_TIME
    }

    private int id;
    private String name;
    private String email;
    private String phone;
    private String designation;
    private Integer departmentId;
    private EmployeeType type;

    public Employee(int id, String name, EmployeeType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public EmployeeType getType() { return type; }
    public void setType(EmployeeType type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public abstract double calculateSalary(); // gross or computed as appropriate

    @Override
    public String toString() {
        return "Employee[id=" + id + ", name=" + name + ", type=" + type + "]";
    }
}


