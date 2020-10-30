package com.bridgelabz.test.EmployeeIO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
 
public class EmployeePayrollDBService {
	
	private static EmployeePayrollDBService employeePayrollDBService;
	private PreparedStatement employeePayrollDataStatement;
	
	private EmployeePayrollDBService() {
		
	}
	
	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}
	
	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "Admin@123";
		Connection connection;
		System.out.println("Connecting to database: "+ jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password );   //used DriverManager to get the connection
		return connection;
	}

	public List<EmployeePayroll> readData() throws EmployeePayrollException {
		String sql = "Select * from employee_payroll;";
		List<EmployeePayroll> list = new ArrayList<>();
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			list = this.getEmployeePayrollData(result);
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.SQL_EXCEPTION);
		}
		return list;
	}

	public int updateEmployee(String name, double salary) throws EmployeePayrollException {
		return this.updateUsingStatement(name, salary);
	}

	private int updateUsingStatement(String name, double salary) throws EmployeePayrollException {
		String sql = String.format("update employee_payroll set basic_pay = %.2f where name = '%s';", salary, name);
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.SQL_EXCEPTION);
		}
	}

	public List<EmployeePayroll> getEmployeePayrollData(String name) throws EmployeePayrollException {
		List<EmployeePayroll> list = new ArrayList<>();
		if(this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1,  name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			list = getEmployeePayrollData(resultSet);
		}
		catch(SQLException e) {
			throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.SQL_EXCEPTION);
		}
		return list;
	}

	private List<EmployeePayroll> getEmployeePayrollData(ResultSet resultSet) throws EmployeePayrollException {
		List<EmployeePayroll> list = new ArrayList<>();
		try {
			while(resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("basic_pay");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				list.add(new EmployeePayroll(id, name, salary, startDate));
			}
		}
		catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.SQL_EXCEPTION);
		}
		return list;
	}

	private void prepareStatementForEmployeeData() throws EmployeePayrollException {
		try {
			Connection connection = getConnection();
			String sql = "Select * from employee_payroll where name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}
		catch(SQLException e) {
			throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.SQL_EXCEPTION);
		}
	}

	public List<EmployeePayroll> getEmployeeWithinDateRange(Date start, Date end) throws EmployeePayrollException {
		String sql = "select * from employee_payroll where start between ? and ?;";
		List<EmployeePayroll> list = new ArrayList<>();
		try {
			Connection connection = getConnection();
			employeePayrollDataStatement = connection.prepareStatement(sql);
			employeePayrollDataStatement.setDate(1,  start);
			employeePayrollDataStatement.setDate(2, end);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			list = getEmployeePayrollData(resultSet);
		}
		catch(SQLException e) {
			throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.SQL_EXCEPTION);
		}
		return list;
	}
}
