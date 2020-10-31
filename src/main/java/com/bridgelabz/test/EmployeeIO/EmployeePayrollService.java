package com.bridgelabz.test.EmployeeIO;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
	EmployeePayrollFileIOService empPayrollFileIOService = new EmployeePayrollFileIOService();
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}
	Scanner userInput = new Scanner(System.in);
	private List<EmployeePayroll> employeePayrollList;

	private EmployeePayrollDBService employeePayrollDBService;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public EmployeePayrollService(List<EmployeePayroll> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public static void main(String[] args) {
		ArrayList<EmployeePayroll> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
		employeePayrollService.readData();
		employeePayrollService.writeData(IOService.CONSOLE_IO);
	}

	private void readData() {
		System.out.println("Enter employee Id");
		int id = userInput.nextInt();
		userInput.nextLine();
		System.out.println("Enter employee name");
		String name = userInput.nextLine();
		System.out.println("Enter employee salary");
		double salary = userInput.nextDouble();
		employeePayrollList.add(new EmployeePayroll(id, name, salary));
	}

	public void writeData(IOService ioService) {
		if (ioService.equals(EmployeePayrollService.IOService.CONSOLE_IO))
			System.out.println("\nWriting Employee Payroll Roaster to Console\n" + employeePayrollList);
		else if (ioService.equals(IOService.FILE_IO))
			empPayrollFileIOService.writeData(employeePayrollList);
	}
 
	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			empPayrollFileIOService.printData();
	}

	public long countEntries(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO))
			return empPayrollFileIOService.countEntries();
		return 0;
	}

	public long fileToList(IOService ioService) {
		List<EmployeePayroll> list ;
		list = empPayrollFileIOService.readFromFile();
		return list.size();
	}
	 
	// DB_ _IO
	
	public List<EmployeePayroll> readData(IOService ioService) throws EmployeePayrollException {
		if(ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData();
		return this.employeePayrollList; 
	}

	public void updateSalary(String name, double salary) throws EmployeePayrollException {
		int result = employeePayrollDBService.updateEmployee(name, salary);
		if(result == 0)
			return;
		EmployeePayroll data = this.getEmployeePayrollData(name);
		if(data != null)
			data.setSalary(salary);
	}

	private EmployeePayroll getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				   .filter(employeeDataItem -> employeeDataItem.getName().equals(name))
				   .findFirst()
				   .orElse(null);
	}

	public boolean checkEmployeePayrollInSync(String name) throws EmployeePayrollException {
		List<EmployeePayroll> list = employeePayrollDBService.getEmployeePayrollData(name);
		return list.get(0).equals(getEmployeePayrollData(name)); 
	}

	public List<EmployeePayroll> getEmployeeInDateRange(String startDate, String endDate) throws EmployeePayrollException {
		Date start=Date.valueOf(startDate);
		Date end = Date.valueOf(endDate);
		employeePayrollList = employeePayrollDBService.getEmployeeWithinDateRange(start, end);
		return employeePayrollList;
	}

	public double getSumByGender(String gender) throws EmployeePayrollException {
		return employeePayrollDBService.getSumByGender(gender);
	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) throws SQLException {
		employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, salary, startDate, gender));
	}

}