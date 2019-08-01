package com.project.capstone.client;

import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import com.project.capstone.utils.Constants;

public final class Menu {
	private Menu() {}
	public static void options() {
		System.out.println("*************Capstone*************");
	    Stream.of(ServiceEndPoint.values())
	    		.forEach(e->
	    			{
	    				System.out.format("%d. %s\n",e.ordinal()+1,e.name());
	    			}
	    		);
	    System.out.println("Please choose one of the above options");
	}
	public static String getEmployeeId(Scanner scanner) {
		
		System.out.print("Please enter employee id (EMP-XXXX) ");
    	String empId = scanner.next();
    	if(!Constants.EMP_ID_REGEX.matcher(empId).matches()) {
    		throw new IllegalArgumentException("Employee Id format EMP-XXXX");
    	}
    	return empId;
	}
	
	public static boolean wishToContinue(Optional<String> optMessage, Scanner scanner) {
		String message = null;
		if(optMessage==null || 
				!optMessage.isPresent())
			message = "Do you wish to continue (Y|*)? ";
		else
			message = optMessage.get();
		System.out.print(message);
	    return (Constants.YES_NO_Y_REGEX.matcher(scanner.next()).matches());
	}
}
