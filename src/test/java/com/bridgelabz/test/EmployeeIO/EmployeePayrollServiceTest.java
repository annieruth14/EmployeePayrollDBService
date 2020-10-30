package com.bridgelabz.test.EmployeeIO;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bridgelabz.test.EmployeeIO.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {
	EmployeePayrollService employeePayrollService;
	
	@Before
	public void initialze() {
		EmployeePayroll[] arraysOfEmp = {
				new EmployeePayroll(1, "Jeffy", 500),
				new EmployeePayroll(2, "Bill", 600),
				new EmployeePayroll(3, "Mark", 800)
		};
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arraysOfEmp));
	}
	
	@Test
	public void givenEmployees_whenWrittenToFile_shouldMatchEmployee(){
		employeePayrollService.writeData(EmployeePayrollService.IOService.FILE_IO);
		employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
		long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
		Assert.assertEquals(3, entries);
	}
	@Test
	public void givenEmployeesInFile_whenAddedToList_shouldMatchEntries(){
		long entries = employeePayrollService.fileToList(EmployeePayrollService.IOService.FILE_IO);
		System.out.println(entries);
		Assert.assertEquals(3, entries);
	}
	
	@Test
	public void givenpayrollDB_whenRetrieve_shouldMatchCount() throws EmployeePayrollException {
		List<EmployeePayroll> list = employeePayrollService.readData(EmployeePayrollService.IOService.DB_IO);
		Assert.assertEquals(3, list.size());
		System.out.println(list.size());
	}
	 
	@Test
	public void givenNewSalary_whenUpdated_shouldReturnSynchWithDB() throws EmployeePayrollException {
		 List<EmployeePayroll> list = employeePayrollService.readData(IOService.DB_IO);
		 employeePayrollService.updateSalary("Clare",5000);
		 boolean result = employeePayrollService.checkEmployeePayrollInSync("Clare");
		 Assert.assertTrue(result);
	}
	
	@Test 
	public void givenDateRange_shouldReturnEmployee() throws EmployeePayrollException {
		List<EmployeePayroll> list = employeePayrollService.readData(IOService.DB_IO);
		List<EmployeePayroll> list1 = employeePayrollService.getEmployeeInDateRange("2020-01-13", "2020-06-13");
		Assert.assertEquals(2, list1.size());
	}
}