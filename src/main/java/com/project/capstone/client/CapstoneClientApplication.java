package com.project.capstone.client;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import com.project.capstone.CapstoneServerProperties;
import com.project.capstone.client.service.EmployeeServiceClient;
import com.project.capstone.client.service.ShiftServiceClient;
import com.project.capstone.model.ClockState;
import com.project.capstone.model.Employee;

@SpringBootApplication
@EnableConfigurationProperties(value = CapstoneServerProperties.class)
public class CapstoneClientApplication implements CommandLineRunner {
	@Autowired
	private ShiftServiceClient shiftServiceClient;
	@Autowired
	private EmployeeServiceClient employeeServiceClient;
	public static void main(String[] args) {
		SpringApplication.run(CapstoneClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		do { 
		    Menu.options();
		    int choice = Menu.getIntInput();
		    try {
			    switch (choice) {
			    	case 1:
			    		Optional<Integer> optShiftId = shiftServiceClient.getCurrentShift();
			    		if(optShiftId.isPresent())
			    			System.out.format("Current shift: %d%n", optShiftId.get().intValue());
			    		else
			    			throw new IllegalArgumentException("Could not get current shift");
			    		break;
			        case 2:
			        	shiftServiceClient.getShifts().stream().forEach(System.out::println);
			        	break;
			        case 3:
						if(Menu.yesOrNo(
			        			Optional.<String>of("Do you wish to enter an employee id (Y|*)? "))){
			        		Optional<Employee> optEmployee = employeeServiceClient.getEmployee(Menu.getEmployeeId());
			        		if(optEmployee.isPresent())
			        			System.out.println(optEmployee.get());
			        	}
						else {
							employeeServiceClient.getEmployees().stream().forEach(System.out::println);;
						}
			        	break;
			        case 4:
			        	Employee newEmp = Menu.getEmployee();
			        	boolean empCreated = employeeServiceClient.addEmployee(newEmp);
			        	if(empCreated) {
			        		System.out.format("Employee with emp id %s created%n",newEmp.getEmpId());
			        	}
			        	break;
			        case 5:
			        	{
				        	Employee emp = new Employee();
				        	emp.setEmpId(Menu.getEmployeeId());
				        	emp.setFirstName(Menu.getStringInput("Please enter first name? "));
				        	emp.setLastName(Menu.getStringInput("Please enter last name? "));
				        	emp.setEmail(Menu.getEmail());
				        	boolean empUpdated = employeeServiceClient.updateEmployee(emp);
				        	if(empUpdated) {
				        		System.out.format("Employee with emp id %s updated%n",emp.getEmpId());
				        	}
			        	}
			        	break;
			        case 6:
			        	{
				        	String empId = Menu.getEmployeeId();
				        	boolean empDeleted = employeeServiceClient.deleteEmployee(empId);
				        	if(empDeleted) {
				        		System.out.format("Employee with emp id %s deleted%n",empId);
				        	}
			        	}
			        	break;
			        case 7:
			        	System.out.println(employeeServiceClient.clockInOut(ClockState.CLOCK_IN, Menu.getEmployeeId()));
			            break;
			        case 8:
			        	System.out.println(employeeServiceClient.clockInOut(ClockState.CLOCK_OUT, Menu.getEmployeeId()));
			        	break;
			        case 9:
			        	if(Menu.yesOrNo(
			        			Optional.<String>of("Do you wish to enter an employee id (Y|*)? "))){
			        		Optional<Employee> optEmployee = employeeServiceClient.getEmployeeShifts(Menu.getEmployeeId());
			        		if(optEmployee.isPresent())
			        			System.out.println(optEmployee.get());
			        	}
			        	else {
			        		employeeServiceClient.getAllEmployeesShifts().stream().forEach(System.out::println);;
			        	}
			        	break;
			        case 10:
			        	{
				        	String empId = Menu.getEmployeeId();
				        	System.out.print("Please enter the existing shift id (1 - 3) ");
				        	int shiftId = Menu.getIntInput();
				        	System.out.print("Please enter the new shift id (1 - 3) ");
				        	int newShiftId = Menu.getIntInput();
							String newShiftStartTime = Menu.getTimeInput(Optional.<String>of("Please enter start time (in HHmm format) "));
							String newShiftEndTime = Menu.getTimeInput(Optional.<String>of("Please enter end time (in HHmm format) "));
							String workingDate = Menu.getDateInput(Optional.<String>of("Please enter working date (in format yyyyMMdd) "), DateTimeFormatter.BASIC_ISO_DATE);
							boolean empUpdated = employeeServiceClient.updateEmployeeShift(empId, shiftId, newShiftId, newShiftStartTime, newShiftEndTime, workingDate);
							if(empUpdated) {
								System.out.format("Shift(s) for employee with emp id %s updated%n",empId);
							}
			        	}
						break;
			        case 11:
			        	Menu.closeMenu();
			    		System.exit(0);
			        	break;
			        default:
			        	throw new IllegalArgumentException("Incorrect option");
			    }
		    }
		    catch(HttpStatusCodeException e) {
		    	HttpStatus status = e.getStatusCode();
		    	JSONObject myObject = new JSONObject(e.getResponseBodyAsString());
		    	if(status.is4xxClientError()){
		    		System.err.printf("Client Error: HttpStatus: %d Text: %s%n",status.value(),myObject.get("message"));
		    	}
		    	else if(status.is5xxServerError()){
		    		System.err.printf("Server Error: HttpStatus: %d Text: %s%n",status.value(),myObject.get("message"));
		    	}
		    }
		    catch(Exception e) {
		    	System.err.printf("%s%n",e.getMessage());
		    }
		}
		while(true);
	}
}
