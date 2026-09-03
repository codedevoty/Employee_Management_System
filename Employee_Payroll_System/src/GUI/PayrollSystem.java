package GUI;
import payroll.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PayrollSystem extends JFrame {
    private EmployeeDAO dao = new EmployeeDAO();
    private PayrollService payrollService = new PayrollService();
    private SalarySlipGenerator slipGen = new SalarySlipGenerator();
    private SearchService searchService = new SearchService();
    private UpdateService updateService = new UpdateService();
    private RemoveService removeService = new RemoveService();

    private JTable employeesTable;
    private DefaultTableModel tableModel;

    public PayrollSystem() {
        setTitle("Payroll Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadAllEmployees();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // top buttons
        JPanel top = new JPanel();
        JButton btnAddFT = new JButton("Add Full-Time");
        JButton btnAddPT = new JButton("Add Part-Time");
        JButton btnSearch = new JButton("Search");
        JButton btnUpdate = new JButton("Update");
        JButton btnRemove = new JButton("Remove");
        JButton btnGenerate = new JButton("Generate Slip");
        JButton btnRefresh = new JButton("Refresh List");
        top.add(btnAddFT);
        top.add(btnAddPT);
        top.add(btnSearch);
        top.add(btnUpdate);
        top.add(btnRemove);
        top.add(btnGenerate);
        top.add(btnRefresh);

        root.add(top, BorderLayout.NORTH);

        // table for employees
        String[] cols = {"ID", "Name", "Type", "Designation", "Email", "Phone"};
        tableModel = new DefaultTableModel(cols, 0);
        employeesTable = new JTable(tableModel);
        root.add(new JScrollPane(employeesTable), BorderLayout.CENTER);

        // button actions
        btnAddFT.addActionListener(e -> showAddFullTimeDialog());
        btnAddPT.addActionListener(e -> showAddPartTimeDialog());
        btnSearch.addActionListener(e -> showSearchDialog());
        btnUpdate.addActionListener(e -> showUpdateDialog());
        btnRemove.addActionListener(e -> showRemoveDialog());
        btnGenerate.addActionListener(e -> showGenerateSlipDialog());
        btnRefresh.addActionListener(e -> loadAllEmployees());
    }

    private void loadAllEmployees() {
        tableModel.setRowCount(0);
        try {
            List<Employee> list = dao.listAll();
            for (Employee emp : list) {
                tableModel.addRow(new Object[]{emp.getId(), emp.getName(), emp.getType(), emp.getDesignation(), emp.getEmail(), emp.getPhone()});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddFullTimeDialog() {
        JDialog d = new JDialog(this, "Add Full-Time Employee", true);
        d.setSize(400, 400);
        d.setLayout(new GridLayout(0, 2));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField salaryField = new JTextField();
        JTextField hraField = new JTextField();
        JTextField daField = new JTextField();
        JTextField pfField = new JTextField();
        JTextField bonusField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField designationField = new JTextField();

        d.add(new JLabel("ID:")); d.add(idField);
        d.add(new JLabel("Name:")); d.add(nameField);
        d.add(new JLabel("Monthly Salary:")); d.add(salaryField);
        d.add(new JLabel("HRA:")); d.add(hraField);
        d.add(new JLabel("DA:")); d.add(daField);
        d.add(new JLabel("PF %:")); d.add(pfField);
        d.add(new JLabel("Bonus:")); d.add(bonusField);
        d.add(new JLabel("Email:")); d.add(emailField);
        d.add(new JLabel("Phone:")); d.add(phoneField);
        d.add(new JLabel("Designation:")); d.add(designationField);

        JButton addBtn = new JButton("Add");
        d.add(new JLabel()); d.add(addBtn);

        addBtn.addActionListener(ev -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                double salary = Double.parseDouble(salaryField.getText().trim());
                FullTimeEmployee f = new FullTimeEmployee(id, name, salary);
                f.setHra(parseDoubleOrZero(hraField.getText()));
                f.setDa(parseDoubleOrZero(daField.getText()));
                f.setPfPercentage(parseDoubleOrZero(pfField.getText()));
                f.setBonus(parseDoubleOrZero(bonusField.getText()));
                f.setEmail(emailField.getText());
                f.setPhone(phoneField.getText());
                f.setDesignation(designationField.getText());
                dao.addEmployee(f);
                JOptionPane.showMessageDialog(d, "Full-time employee added");
                d.dispose();
                loadAllEmployees();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void showAddPartTimeDialog() {
        JDialog d = new JDialog(this, "Add Part-Time Employee", true);
        d.setSize(400, 350);
        d.setLayout(new GridLayout(0, 2));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField hoursField = new JTextField();
        JTextField rateField = new JTextField();
        JTextField overtimeField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField designationField = new JTextField();

        d.add(new JLabel("ID:")); d.add(idField);
        d.add(new JLabel("Name:")); d.add(nameField);
        d.add(new JLabel("Hours Worked:")); d.add(hoursField);
        d.add(new JLabel("Hourly Rate:")); d.add(rateField);
        d.add(new JLabel("Overtime Rate:")); d.add(overtimeField);
        d.add(new JLabel("Email:")); d.add(emailField);
        d.add(new JLabel("Phone:")); d.add(phoneField);
        d.add(new JLabel("Designation:")); d.add(designationField);

        JButton addBtn = new JButton("Add");
        d.add(new JLabel()); d.add(addBtn);

        addBtn.addActionListener(ev -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                int hours = Integer.parseInt(hoursField.getText().trim());
                double rate = Double.parseDouble(rateField.getText().trim());
                PartTimeEmployee p = new PartTimeEmployee(id, name, hours, rate);
                p.setOvertimeRate(parseDoubleOrZero(overtimeField.getText()));
                p.setEmail(emailField.getText());
                p.setPhone(phoneField.getText());
                p.setDesignation(designationField.getText());
                dao.addEmployee(p);
                JOptionPane.showMessageDialog(d, "Part-time employee added");
                d.dispose();
                loadAllEmployees();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    private void showSearchDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter name (or part):");
        if (name == null || name.trim().isEmpty()) return;
        try {
            List<Employee> results = searchService.searchByName(name.trim());
            tableModel.setRowCount(0);
            for (Employee e : results) {
                tableModel.addRow(new Object[]{e.getId(), e.getName(), e.getType(), e.getDesignation(), e.getEmail(), e.getPhone()});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
        }
    }

    private void showUpdateDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Employee ID to update:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        try {
            int id = Integer.parseInt(idStr.trim());
            Employee e = dao.getEmployeeById(id);
            if (e == null) {
                JOptionPane.showMessageDialog(this, "Employee not found");
                return;
            }

            JDialog d = new JDialog(this, "Update Employee: " + id, true);
            d.setSize(400, 400);
            d.setLayout(new GridLayout(0, 2));

            JTextField nameField = new JTextField(e.getName());
            JTextField emailField = new JTextField(e.getEmail());
            JTextField phoneField = new JTextField(e.getPhone());
            JTextField designationField = new JTextField(e.getDesignation());

            d.add(new JLabel("Name:")); d.add(nameField);
            d.add(new JLabel("Email:")); d.add(emailField);
            d.add(new JLabel("Phone:")); d.add(phoneField);
            d.add(new JLabel("Designation:")); d.add(designationField);

            if (e instanceof FullTimeEmployee) {
                FullTimeEmployee f = (FullTimeEmployee) e;
                JTextField salaryField = new JTextField(String.valueOf(f.getMonthlySalary()));
                JTextField hraField = new JTextField(String.valueOf(f.getHra()));
                JTextField daField = new JTextField(String.valueOf(f.getDa()));
                JTextField pfField = new JTextField(String.valueOf(f.getPfPercentage()));
                JTextField bonusField = new JTextField(String.valueOf(f.getBonus()));
                d.add(new JLabel("Monthly Salary:")); d.add(salaryField);
                d.add(new JLabel("HRA:")); d.add(hraField);
                d.add(new JLabel("DA:")); d.add(daField);
                d.add(new JLabel("PF %:")); d.add(pfField);
                d.add(new JLabel("Bonus:")); d.add(bonusField);

                JButton updateBtn = new JButton("Update FT");
                d.add(new JLabel()); d.add(updateBtn);
                updateBtn.addActionListener(ae -> {
                    try {
                        boolean ok1 = updateService.updateBasicInfo(id, nameField.getText(), emailField.getText(), phoneField.getText(), null, designationField.getText());
                        boolean ok2 = updateService.updateFullTimeSalary(id,
                                Double.parseDouble(salaryField.getText()),
                                Double.parseDouble(hraField.getText()),
                                Double.parseDouble(daField.getText()),
                                Double.parseDouble(pfField.getText()),
                                Double.parseDouble(bonusField.getText()));
                        JOptionPane.showMessageDialog(d, "Updated: " + (ok1 && ok2));
                        d.dispose();
                        loadAllEmployees();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(d, "Update failed: " + ex.getMessage());
                    }
                });

            } else if (e instanceof PartTimeEmployee) {
                PartTimeEmployee p = (PartTimeEmployee) e;
                JTextField hoursField = new JTextField(String.valueOf(p.getHoursWorked()));
                JTextField rateField = new JTextField(String.valueOf(p.getHourlyRate()));
                JTextField overtimeField = new JTextField(String.valueOf(p.getOvertimeRate()));
                d.add(new JLabel("Hours Worked:")); d.add(hoursField);
                d.add(new JLabel("Hourly Rate:")); d.add(rateField);
                d.add(new JLabel("Overtime Rate:")); d.add(overtimeField);

                JButton updateBtn = new JButton("Update PT");
                d.add(new JLabel()); d.add(updateBtn);

                updateBtn.addActionListener(ae -> {
                    try {
                        boolean ok1 = updateService.updateBasicInfo(id, nameField.getText(), emailField.getText(), phoneField.getText(), null, designationField.getText());
                        boolean ok2 = updateService.updatePartTimeRates(id,
                                Integer.parseInt(hoursField.getText()),
                                Double.parseDouble(rateField.getText()),
                                Double.parseDouble(overtimeField.getText()));
                        JOptionPane.showMessageDialog(d, "Updated: " + (ok1 && ok2));
                        d.dispose();
                        loadAllEmployees();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(d, "Update failed: " + ex.getMessage());
                    }
                });
            }

            d.setLocationRelativeTo(this);
            d.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showRemoveDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter Employee Name to Remove:");
        if (name == null || name.trim().isEmpty()) return;

        try {
            boolean removed = removeService.removeEmployeeByName(name.trim());
            if (removed) {
                JOptionPane.showMessageDialog(this, " Employee '" + name + "' removed successfully.");
                loadAllEmployees();
            } else {
                JOptionPane.showMessageDialog(this, " No employee found with the name '" + name + "'.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showGenerateSlipDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Employee ID:");
        if (idStr == null) return;
        String monthStr = JOptionPane.showInputDialog(this, "Month (1-12):");
        if (monthStr == null) return;
        String yearStr = JOptionPane.showInputDialog(this, "Year (e.g., 2025):");
        if (yearStr == null) return;

        try {
            int id = Integer.parseInt(idStr.trim());
            int month = Integer.parseInt(monthStr.trim());
            int year = Integer.parseInt(yearStr.trim());
            String path = slipGen.generateSlip(id, month, year);
            JOptionPane.showMessageDialog(this, "Salary slip generated at:\n" + path);
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating slip: " + ex.getMessage());
        }
    }

    private double parseDoubleOrZero(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }

    public static void main(String[] args) {

        System.out.println("\n\n==============================");
        System.out.println("  EMPLOYEE MANAGEMENT SYSTEM  ");
        System.out.println("==============================");
        System.out.println("  Project Started Successfully!");

        SwingUtilities.invokeLater(() -> {
            new PayrollSystem().setVisible(true);
        });
    }
}

