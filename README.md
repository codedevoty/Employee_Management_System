# 💼 Employee Payroll System

A desktop **Payroll Management System** built with **Java Swing** and **MySQL**, designed to manage employees (full-time and part-time), automatically calculate salaries with deductions, and generate downloadable salary slips.

![Java](https://img.shields.io/badge/Java-JDK%2020-orange?logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8%2B-blue?logo=mysql)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blueviolet)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 📖 Overview

The **Employee Payroll System** is a standalone Java desktop application that helps manage employee records and payroll processing. It supports two categories of employees — **Full-Time** and **Part-Time** — each with their own salary structure, and can compute net pay after deductions such as Provident Fund (PF) and tax, then export the result as a text-based salary slip.

The project follows a clean **layered architecture**:
- **Model layer** — `Employee`, `FullTimeEmployee`, `PartTimeEmployee`, `PayrollRecord`
- **Data Access layer (DAO)** — `EmployeeDAO`, `DBConfig`
- **Service layer** — `PayrollService`, `SearchService`, `UpdateService`, `RemoveService`, `SalarySlipGenerator`
- **Presentation layer (GUI)** — `PayrollSystem`, `Payroll2`, `Payroll3` (Swing UIs)

---

## ✨ Features

- 👥 **Employee Management** — Add, search, update, and remove employees
- 🧑‍💼 **Two Employee Types**
  - **Full-Time** — monthly salary + HRA + DA + bonus, with PF deduction
  - **Part-Time** — hourly rate based pay with automatic overtime calculation (beyond 160 hrs/month)
- 💰 **Automated Payroll Calculation**
  - Gross salary computation per employee type
  - Progressive tax slab calculation for full-time employees
  - Flat tax rate for part-time employees
  - Net salary = Gross − Deductions
- 🧾 **Salary Slip Generation** — generates a formatted `.txt` salary slip for any employee/month/year
- 🔍 **Search** — find employees by name (partial match) or ID
- 🖥️ **Modern Dashboard UI** *(Payroll2)* — summary cards (Total / Full-Time / Part-Time counts), color-coded employee table, and styled action buttons
- 🗄️ **Persistent Storage** — all data is stored in a MySQL database via JDBC

---

## 🛠️ Tech Stack

| Layer            | Technology                         |
|-------------------|-------------------------------------|
| Language          | Java (JDK 20)                      |
| GUI Framework     | Java Swing (AWT)                   |
| Database          | MySQL 8+                           |
| DB Connectivity   | JDBC — `mysql-connector-j-9.4.0`   |
| IDE               | IntelliJ IDEA                      |

---

## 📂 Project Structure

```
Employee Payroll System/
├── src/
│   ├── payroll/
│   │   ├── DBConfig.java            # Database connection config
│   │   ├── Employee.java            # Abstract base employee model
│   │   ├── FullTimeEmployee.java    # Full-time employee model + salary logic
│   │   ├── PartTimeEmployee.java    # Part-time employee model + salary logic
│   │   ├── EmployeeDAO.java         # Handles all DB CRUD operations
│   │   ├── PayrollRecord.java       # Payroll record model
│   │   ├── PayrollService.java      # Payroll & tax calculation logic
│   │   ├── SalarySlipGenerator.java # Generates .txt salary slips
│   │   ├── SearchService.java       # Employee search logic
│   │   ├── UpdateService.java       # Employee/salary update logic
│   │   └── RemoveService.java       # Employee removal logic
│   └── GUI/
│       ├── PayrollSystem.java       # Base Swing UI
│       ├── Payroll2.java            # Enhanced dashboard UI (recommended entry point)
│       └── Payroll3.java            # Alternate UI iteration
├── salary_slip_*.txt                # Sample generated salary slips
├── .gitignore
└── Employee Payroll System.iml
```

> 💡 `Payroll2.java` contains the most complete/polished UI (dashboard cards, styled table, color-coded rows) and is the recommended class to run.

---

## 🗄️ Database Setup

The app expects a MySQL database named **`payrollSystem`** with the following tables. Run this SQL script to set it up:

```sql
CREATE DATABASE IF NOT EXISTS payrollSystem;
USE payrollSystem;

CREATE TABLE employees (
    id             INT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(100),
    phone          VARCHAR(20),
    hire_date      DATE,
    department_id  INT,
    type           VARCHAR(20) NOT NULL,   -- 'FULL_TIME' or 'PART_TIME'
    designation    VARCHAR(100)
);

CREATE TABLE full_time_employees (
    id             INT PRIMARY KEY,
    monthly_salary DOUBLE NOT NULL,
    hra            DOUBLE DEFAULT 0,
    da             DOUBLE DEFAULT 0,
    pf_percentage  DOUBLE DEFAULT 0,
    bonus          DOUBLE DEFAULT 0,
    FOREIGN KEY (id) REFERENCES employees(id) ON DELETE CASCADE
);

CREATE TABLE part_time_employees (
    id             INT PRIMARY KEY,
    hours_worked   INT NOT NULL,
    hourly_rate    DOUBLE NOT NULL,
    overtime_rate  DOUBLE DEFAULT 0,
    FOREIGN KEY (id) REFERENCES employees(id) ON DELETE CASCADE
);

CREATE TABLE payroll (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    emp_id            INT NOT NULL,
    month             INT NOT NULL,
    year              INT NOT NULL,
    gross_salary      DOUBLE NOT NULL,
    total_deductions  DOUBLE NOT NULL,
    net_salary        DOUBLE NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (emp_id) REFERENCES employees(id) ON DELETE CASCADE
);
```

---

## ⚙️ Setup & Installation

### Prerequisites
- JDK 20 (or compatible)
- MySQL Server 8+
- [MySQL Connector/J 9.4.0](https://dev.mysql.com/downloads/connector/j/) (JDBC driver)
- IntelliJ IDEA (recommended) or any Java IDE

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/employee-payroll-system.git
   cd employee-payroll-system
   ```

2. **Set up the database**
   Run the SQL script above in MySQL Workbench, `mysql` CLI, or any MySQL client.

3. **Add the MySQL JDBC driver**
   Download `mysql-connector-j-9.4.0.jar` and add it as a library/dependency in your IDE (or your build tool of choice).

4. **Configure your database credentials**
   Open `src/payroll/DBConfig.java` and update:
   ```java
   private static final String URL  = "jdbc:mysql://localhost:3306/payrollSystem";
   private static final String USER = "your_mysql_username";
   private static final String PASS = "your_mysql_password";
   ```
   > ⚠️ **Never commit real database credentials.** Consider loading these from environment variables or a local `.env`/`config.properties` file that's excluded via `.gitignore`.

5. **Run the application**
   Compile and run the main GUI class:
   ```bash
   javac -cp .:mysql-connector-j-9.4.0.jar -d out src/payroll/*.java src/GUI/*.java
   java -cp out:mysql-connector-j-9.4.0.jar GUI.Payroll2
   ```
   Or simply run `GUI.Payroll2` (recommended) / `GUI.PayrollSystem` / `GUI.Payroll3` directly from your IDE.

---

## 🚀 Usage

1. Launch the app — the dashboard shows total, full-time, and part-time employee counts.
2. Click **Add Full-Time** or **Add Part-Time** to register a new employee.
3. Use **Search** to look up employees by name.
4. Use **Update** to edit basic info or salary/rate details.
5. Use **Generate Slip** to calculate payroll for a given month/year and export a `.txt` salary slip.
6. Use **Remove** to delete an employee record.
7. Click **Refresh List** to reload the employee table from the database.

### Sample Salary Slip Output
```
=== Salary Slip ===
Employee ID: 1
Name: Sahil Varma
Designation: Software Engineer
Month/Year: 12/2025

Gross Salary: 90000.00
Total Deductions: 13650.00
Net Salary: 76350.00

Generated at: 2025-10-05 19:45:06.0
```

---

## 🧮 Payroll Calculation Logic

**Full-Time Employees**
- Gross = Monthly Salary + HRA + DA + Bonus
- PF Deduction = PF% × Monthly Salary
- Tax = Progressive slab on gross (0% up to ₹25,000; 5% / 10% / 20% for higher slabs)
- Net = Gross − (PF + Tax)

**Part-Time Employees**
- Normal Pay = min(Hours Worked, 160) × Hourly Rate
- Overtime Pay = max(0, Hours Worked − 160) × (Overtime Rate or 1.5× Hourly Rate)
- Tax = 5% of Gross
- Net = Gross − Tax

---

## 🔒 Security Note

This project currently stores database credentials directly in `DBConfig.java` and `EmployeeDAO.java`. Before deploying or sharing this project publicly, replace these with environment variables or a config file excluded from version control, and rotate any credentials that were previously committed.

---

## 🗺️ Roadmap / Ideas for Improvement

- [ ] Externalize DB credentials via `.env` / `config.properties`
- [ ] Export salary slips as PDF instead of plain text
- [ ] Add authentication/login for HR users
- [ ] Add department management module
- [ ] Migrate persistence layer to Spring Boot + JPA
- [ ] Add unit tests for salary/tax calculation logic

---

## 🤝 Contributing

Contributions are welcome! Feel free to fork this repo, create a feature branch, and submit a pull request.

```bash
git checkout -b feature/your-feature-name
git commit -m "Add: your feature"
git push origin feature/your-feature-name
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 👤 Author

**Your Name**
Feel free to connect or raise issues if you find bugs or have suggestions!
