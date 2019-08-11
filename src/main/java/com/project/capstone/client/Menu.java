package com.project.capstone.client;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import com.project.capstone.model.Employee;
import com.project.capstone.utils.Constants;

public final class Menu {
	private Menu() {}
	static Scanner scanner = new Scanner(System.in);
	public static void options() {
		System.out.println("*************Capstone*************");
	    Stream.of(ServiceEndPoint.values())
	    		.forEach(e->
	    			{
	    				System.out.format("%d. %s\n",e.ordinal()+1,e.name());
	    			}
	    		);
	    System.out.format("%d. %s\n", ServiceEndPoint.values().length+1, "EXIT");
	    System.out.println("Please choose one of the above options");
	}
	public static Employee getEmployee() {
		Employee emp = new Employee();
    	emp.setEmpId(Menu.getEmployeeId());
    	emp.setFirstName(Menu.getStringInput("Please enter first name? "));
    	emp.setLastName(Menu.getStringInput("Please enter last name? "));
    	emp.setEmail(Menu.getEmail());
    	return emp;
	}
	public static String getEmployeeId() {
		System.out.print("Please enter employee id (EMP-XXXX) ");
    	String empId = scanner.next();
    	if(Constants.EMP_ID_PATTERN_MATCH.negate().test(empId)) {
    		throw new IllegalArgumentException("Employee Id format EMP-XXXX");
    	}
    	return empId;
	}
	public static String getEmail() {
		System.out.print("Please enter email id (email@some.domain) ");
    	String email = scanner.next();
    	if(Constants.EMAIL_PATTERN_MATCH.negate().test(email)) {
    		throw new IllegalArgumentException("Email address format email@some.domain");
    	}
    	return email;
	}
	public static String getStringInput(String message) {
		System.out.print(message);
		return scanner.next();
	}
	
	public static boolean yesOrNo(Optional<String> optMessage) {
		String message = null;
		if(optMessage==null || 
				!optMessage.isPresent())
			message = "Do you wish to continue (Y|*)? ";
		else
			message = optMessage.get();
		System.out.print(message);
	    return (Constants.YES_NO_Y_REGEX.matcher(scanner.next()).matches());
	}
	public static int getIntInput() {
		return scanner.nextInt();
	}
	
	public static void closeMenu() {
    	try {
    		scanner.close();
    	}
    	catch(Exception e) {
    		System.err.print(e.getMessage());
    	}
	}
	public static String getDateInput(Optional<String> optMessage, DateTimeFormatter format) {
		if(optMessage!=null && optMessage.isPresent())
			System.out.print(optMessage.get());
		String date = scanner.next();
		LocalDate.parse(date,format);
		return date;
	}
	public static String getTimeInput(Optional<String> optMessage) {
		if(optMessage!=null && optMessage.isPresent())
			System.out.print(optMessage.get());
		String time = scanner.next();
		LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
		return time;
	}
}
