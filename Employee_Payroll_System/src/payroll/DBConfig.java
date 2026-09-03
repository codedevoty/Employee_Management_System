package payroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/payrollSystem?user=root&password=VS04@kbpsserverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "VS04@kbps"; // set your password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // load driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}