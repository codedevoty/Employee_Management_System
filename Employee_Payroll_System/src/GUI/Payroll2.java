package GUI;

import payroll.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Payroll2 extends JFrame {
    // --- Services/DAOs (Unchanged) ---
    private EmployeeDAO dao = new EmployeeDAO();
    private PayrollService payrollService = new PayrollService();
    private SalarySlipGenerator slipGen = new SalarySlipGenerator();
    private SearchService searchService = new SearchService();
    private UpdateService updateService = new UpdateService();
    private RemoveService removeService = new RemoveService();

    private JTable employeesTable;
    private DefaultTableModel tableModel;

    // Status Labels for the "Cards" (to allow dynamic updates)
    private JLabel totalCountLabel = new JLabel("0");
    private JLabel fullTimeCountLabel = new JLabel("0");
    private JLabel partTimeCountLabel = new JLabel("0");

    public Payroll2() {
        // --- 1. SET FLATLAF LOOK AND FEEL (FOR MODERN LOOK) ---
        try {
            // Attempt to use FlatLaf (if JAR is available)
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            try {
                // Fallback to Nimbus
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex) {
                // Keep default L&F if Nimbus fails
            }
        }

        setTitle("✨ Payroll Management System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadAllEmployees();
    }

    private void initUI() {
        // Set root panel style to match the light background in the image
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(new Color(245, 247, 250)); // Light gray background
        setContentPane(root);

        // --- 1. Header (Top) ---
        root.add(createHeaderPanel(), BorderLayout.NORTH);

        // --- 2. Main Content Area (Cards, Actions, Table) ---
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setOpaque(false); // To let root background show through

        JPanel topSection = new JPanel(new BorderLayout(10, 10));
        topSection.setOpaque(false);
        topSection.add(createStatusCardsPanel(), BorderLayout.NORTH); // Status Cards
        topSection.add(createActionsPanel(), BorderLayout.CENTER); // Action Buttons

        mainContent.add(topSection, BorderLayout.NORTH);
        mainContent.add(createEmployeeTablePanel(), BorderLayout.CENTER);

        root.add(mainContent, BorderLayout.CENTER);
    }

    // --- Component Creation Methods ---

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Payroll Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(60, 120, 255)); // Blue color from image
        titleBar.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        titleBar.add(title);

        header.add(titleBar, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Manage your employees and generate salary slips efficiently", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        header.add(subtitle, BorderLayout.SOUTH);

        return header;
    }

    private JPanel createStatusCardsPanel() {
        JPanel cardPanel = new JPanel(new GridLayout(1, 3, 20, 0)); // 3 columns with gap
        cardPanel.setOpaque(false);

        cardPanel.add(createCard("Total Employees", totalCountLabel, new Color(60, 120, 255)));
        cardPanel.add(createCard("Full-Time", fullTimeCountLabel, new Color(30, 180, 50)));
        cardPanel.add(createCard("Part-Time", partTimeCountLabel, new Color(255, 150, 0)));

        return cardPanel;
    }

    private JPanel createCard(String title, JLabel countLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 0));

        // Simulating the card look with borders, padding, and background
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY.brighter(), 1, true), // Subtle border
                new EmptyBorder(15, 15, 15, 15) // Inner padding
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        countLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        countLabel.setForeground(color.darker());

        // Icon placeholders (using emojis as standard icon files are external)
        String iconText = "";
        if (title.contains("Total")) iconText = "👥";
        else if (title.contains("Full-Time")) iconText = "👔";
        else if (title.contains("Part-Time")) iconText = "⏱";
        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 30));
        iconLabel.setForeground(color);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(countLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionsPanel.setOpaque(false);

        // Define buttons with custom colors and icons
        JButton btnAddFT = createActionButton("Add Full-Time", new Color(60, 120, 255), "➕");
        JButton btnAddPT = createActionButton("Add Part-Time", new Color(120, 180, 255), "➕");
        JButton btnSearch = createActionButton("Search", new Color(150, 150, 150), "🔍");
        JButton btnUpdate = createActionButton("Update", new Color(255, 150, 0), "✏");
        JButton btnRemove = createActionButton("Remove", new Color(200, 50, 50), "❌");
        JButton btnGenerate = createActionButton("Generate Slip", new Color(50, 150, 200), "📄");
        JButton btnRefresh = createActionButton("Refresh List", new Color(100, 100, 100), "🔄");

        actionsPanel.add(btnAddFT);
        actionsPanel.add(btnAddPT);
        actionsPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacer
        actionsPanel.add(btnSearch);
        actionsPanel.add(btnUpdate);
        actionsPanel.add(btnRemove);
        actionsPanel.add(btnGenerate);
        actionsPanel.add(btnRefresh);

        // --- Button Actions (Re-using original logic) ---
        btnAddFT.addActionListener(e -> showAddFullTimeDialog());
        btnAddPT.addActionListener(e -> showAddPartTimeDialog());
        btnSearch.addActionListener(e -> showSearchDialog());
        btnUpdate.addActionListener(e -> showUpdateDialog());
        btnRemove.addActionListener(e -> showRemoveDialog());
        btnGenerate.addActionListener(e -> showGenerateSlipDialog());
        btnRefresh.addActionListener(e -> loadAllEmployees());

        return actionsPanel;
    }

    private JButton createActionButton(String text, Color color, String iconText) {
        JButton button = new JButton(iconText + " " + text);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false); // Flat appearance
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private JScrollPane createEmployeeTablePanel() {
        String[] cols = {"ID", "Name", "Type", "Designation", "Email", "Phone"};
        tableModel = new DefaultTableModel(cols, 0);
        employeesTable = new JTable(tableModel) {
            // Custom renderer to draw the colorful 'Full-Time'/'Part-Time' tags
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                // Apply custom styling only to the 'Type' column
                if (column == 2) {
                    String type = (String) getValueAt(row, column);
                    JLabel label = new JLabel(type, SwingConstants.CENTER);
                    label.setOpaque(true);
                    label.setFont(new Font("SansSerif", Font.BOLD, 11));

                    Color statusColor = type.equals("Full-Time") ? new Color(30, 180, 50) : new Color(255, 150, 0);

                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(statusColor.darker(), 1, true),
                            new EmptyBorder(2, 5, 2, 5)
                    ));
                    label.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 50)); // Light background
                    label.setForeground(statusColor.darker()); // Darker text color
                    return label;
                }

                // Apply row alternating colors for the rest of the table
                c.setBackground(row % 2 == 0 ? new Color(250, 250, 250) : Color.WHITE);
                return c;
            }
        };

        employeesTable.setRowHeight(35);
        employeesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        employeesTable.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(employeesTable);
        // Add a clean border to the table view
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(0, 0, 0, 0)
        ));
        return scrollPane;
    }

    // --- Data Loading and Status Update ---
    private void loadAllEmployees() {
        tableModel.setRowCount(0);
        int total = 0;
        long fullTimeCount = 0;
        long partTimeCount = 0;

        try {
            List<Employee> list = dao.listAll();
            total = list.size();
            for (Employee emp : list) {
                String type = (emp instanceof FullTimeEmployee) ? "Full-Time" : "Part-Time";
                if (type.equals("Full-Time")) fullTimeCount++;
                else partTimeCount++;

                tableModel.addRow(new Object[]{emp.getId(), emp.getName(), type, emp.getDesignation(), emp.getEmail(), emp.getPhone()});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Update the status cards counts
        totalCountLabel.setText(String.valueOf(total));
        fullTimeCountLabel.setText(String.valueOf(fullTimeCount));
        partTimeCountLabel.setText(String.valueOf(partTimeCount));
    }

    // --- Dialog Methods (Re-using original structure, updated for consistency) ---
    // Note: The dialog implementations still use the basic GridLayout, but
    // the use of FlatLaf/Nimbus will make them look better than the default.

    private JDialog createBaseDialog(String title, int width, int height) {
        JDialog d = new JDialog(this, title, true);
        d.setSize(width, height);
        d.setLocationRelativeTo(this);
        d.setResizable(false);
        return d;
    }

    private void showAddFullTimeDialog() {
        JDialog d = createBaseDialog("➕ Add Full-Time Employee", 500, 480);
        d.setLayout(new GridLayout(0, 2, 10, 10)); // Add spacing

        // Input Fields (with initial data for better UX)
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField salaryField = new JTextField("0.0");
        JTextField hraField = new JTextField("0.0");
        JTextField daField = new JTextField("0.0");
        JTextField pfField = new JTextField("0.0");
        JTextField bonusField = new JTextField("0.0");
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField designationField = new JTextField();

        // Adding components
        d.add(new JLabel("Employee ID:")); d.add(idField);
        d.add(new JLabel("Name:")); d.add(nameField);
        d.add(new JLabel("Designation:")); d.add(designationField);
        d.add(new JLabel("Email:")); d.add(emailField);
        d.add(new JLabel("Phone:")); d.add(phoneField);
        d.add(new JSeparator()); d.add(new JSeparator()); // Separator
        d.add(new JLabel("Monthly Salary (Base):")); d.add(salaryField);
        d.add(new JLabel("HRA:")); d.add(hraField);
        d.add(new JLabel("DA:")); d.add(daField);
        d.add(new JLabel("PF % (e.g., 12.0):")); d.add(pfField);
        d.add(new JLabel("Bonus:")); d.add(bonusField);

        JButton addBtn = createActionButton("💾 Save Employee", new Color(0, 150, 0), "");
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
                JOptionPane.showMessageDialog(d, "Full-time employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                d.dispose();
                loadAllEmployees();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Error: Please check numeric fields (ID, Salary, HRA, DA, PF, Bonus).", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.setVisible(true);
    }

    private void showAddPartTimeDialog() {
        JDialog d = createBaseDialog("➕ Add Part-Time Employee", 500, 420);
        d.setLayout(new GridLayout(0, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField hoursField = new JTextField("0");
        JTextField rateField = new JTextField("0.0");
        JTextField overtimeField = new JTextField("0.0");
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField designationField = new JTextField();

        d.add(new JLabel("Employee ID:")); d.add(idField);
        d.add(new JLabel("Name:")); d.add(nameField);
        d.add(new JLabel("Designation:")); d.add(designationField);
        d.add(new JLabel("Email:")); d.add(emailField);
        d.add(new JLabel("Phone:")); d.add(phoneField);
        d.add(new JSeparator()); d.add(new JSeparator());
        d.add(new JLabel("Hours Worked (Monthly):")); d.add(hoursField);
        d.add(new JLabel("Hourly Rate:")); d.add(rateField);
        d.add(new JLabel("Overtime Rate:")); d.add(overtimeField);

        JButton addBtn = createActionButton("💾 Save Employee", new Color(0, 150, 0), "");
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
                JOptionPane.showMessageDialog(d, "Part-time employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                d.dispose();
                loadAllEmployees();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Error: Please check numeric fields (ID, Hours, Rate, Overtime).", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        d.setVisible(true);
    }

    // --- Remaining Dialogs (re-using your original logic but using the base dialog setup) ---

    private void showSearchDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter name (or part) to search:");
        if (name == null || name.trim().isEmpty()) return;
        try {
            List<Employee> results = searchService.searchByName(name.trim());
            tableModel.setRowCount(0);
            for (Employee e : results) {
                String type = (e instanceof FullTimeEmployee) ? "Full-Time" : "Part-Time";
                tableModel.addRow(new Object[]{e.getId(), e.getName(), type, e.getDesignation(), e.getEmail(), e.getPhone()});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(), "Search Error", JOptionPane.ERROR_MESSAGE);
        }
        // Note: Call loadAllEmployees() if no results are found and you want to restore the list
    }

    private void showUpdateDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Employee ID to update:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        try {
            int id = Integer.parseInt(idStr.trim());
            Employee e = dao.getEmployeeById(id);
            if (e == null) {
                JOptionPane.showMessageDialog(this, "Employee not found", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JDialog d = createBaseDialog("✏ Update Employee: " + id, 500, (e instanceof FullTimeEmployee) ? 480 : 420);
            d.setLayout(new GridLayout(0, 2, 10, 10));

            JTextField nameField = new JTextField(e.getName());
            JTextField emailField = new JTextField(e.getEmail());
            JTextField phoneField = new JTextField(e.getPhone());
            JTextField designationField = new JTextField(e.getDesignation());

            d.add(new JLabel("Name:")); d.add(nameField);
            d.add(new JLabel("Email:")); d.add(emailField);
            d.add(new JLabel("Phone:")); d.add(phoneField);
            d.add(new JLabel("Designation:")); d.add(designationField);

            JButton updateBtn = createActionButton("💾 Update Employee", new Color(200, 100, 0), "");

            if (e instanceof FullTimeEmployee) {
                FullTimeEmployee f = (FullTimeEmployee) e;
                JTextField salaryField = new JTextField(String.valueOf(f.getMonthlySalary()));
                JTextField hraField = new JTextField(String.valueOf(f.getHra()));
                JTextField daField = new JTextField(String.valueOf(f.getDa()));
                JTextField pfField = new JTextField(String.valueOf(f.getPfPercentage()));
                JTextField bonusField = new JTextField(String.valueOf(f.getBonus()));

                d.add(new JSeparator()); d.add(new JSeparator());
                d.add(new JLabel("Monthly Salary (Base):")); d.add(salaryField);
                d.add(new JLabel("HRA:")); d.add(hraField);
                d.add(new JLabel("DA:")); d.add(daField);
                d.add(new JLabel("PF % (e.g., 12.0):")); d.add(pfField);
                d.add(new JLabel("Bonus:")); d.add(bonusField);

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
                        JOptionPane.showMessageDialog(d, "Update " + ((ok1 && ok2) ? "successful!" : "failed."));
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

                d.add(new JSeparator()); d.add(new JSeparator());
                d.add(new JLabel("Hours Worked (Monthly):")); d.add(hoursField);
                d.add(new JLabel("Hourly Rate:")); d.add(rateField);
                d.add(new JLabel("Overtime Rate:")); d.add(overtimeField);

                d.add(new JLabel()); d.add(updateBtn);
                updateBtn.addActionListener(ae -> {
                    try {
                        boolean ok1 = updateService.updateBasicInfo(id, nameField.getText(), emailField.getText(), phoneField.getText(), null, designationField.getText());
                        boolean ok2 = updateService.updatePartTimeRates(id,
                                Integer.parseInt(hoursField.getText()),
                                Double.parseDouble(rateField.getText()),
                                Double.parseDouble(overtimeField.getText()));
                        JOptionPane.showMessageDialog(d, "Update " + ((ok1 && ok2) ? "successful!" : "failed."));
                        d.dispose();
                        loadAllEmployees();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(d, "Update failed: " + ex.getMessage());
                    }
                });
            }
            d.setVisible(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Invalid Employee ID entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRemoveDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter Employee Name to Remove:");
        if (name == null || name.trim().isEmpty()) return;

        try {
            boolean removed = removeService.removeEmployeeByName(name.trim());
            if (removed) {
                JOptionPane.showMessageDialog(this, " Employee '" + name + "' removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllEmployees();
            } else {
                JOptionPane.showMessageDialog(this, " No employee found with the name '" + name + "'.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing employee: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showGenerateSlipDialog() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Employee ID:");
        if (idStr == null || idStr.trim().isEmpty()) return;
        String monthStr = JOptionPane.showInputDialog(this, "Enter Month (1-12):");
        if (monthStr == null || monthStr.trim().isEmpty()) return;
        String yearStr = JOptionPane.showInputDialog(this, "Enter Year (e.g., 2025):");
        if (yearStr == null || yearStr.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(idStr.trim());
            int month = Integer.parseInt(monthStr.trim());
            int year = Integer.parseInt(yearStr.trim());
            String path = slipGen.generateSlip(id, month, year);
            JOptionPane.showMessageDialog(this, "Salary slip generated at:\n" + path, "Slip Generated", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Please check numeric fields (ID, Month, Year).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating slip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double parseDoubleOrZero(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PayrollSystem().setVisible(true);
        });
    }
}