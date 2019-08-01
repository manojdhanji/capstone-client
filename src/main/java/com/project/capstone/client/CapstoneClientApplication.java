package com.project.capstone.client;

import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CapstoneClientApplication implements CommandLineRunner {
	@Autowired
	private RestTemplate restTemplate;
	public static void main(String[] args) {
		SpringApplication.run(CapstoneClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		String empId = "";
		do { 
			
		    Menu.options();
		    int choice = scanner.nextInt();
		    try {
			    switch (choice) {
			        case 1:
			        	ServiceEndPoint.GET_SHIFTS.execute(restTemplate, Optional.<Object[]>empty());
			        	break;
			        case 2:
			        	Object[] pathVariables = null;
			        	
			        	if(Menu.wishToContinue(
			        			Optional.<String>of("Do you wish to enter an employee id (Y|*)?"), scanner)){
			        		empId=Menu.getEmployeeId(scanner);
					    	pathVariables = new Object[] {empId};
			        	}
					    ServiceEndPoint.GET_EMPLOYEES.execute(restTemplate, Optional.<Object[]>ofNullable(pathVariables));
			        	break;
			        case 3:
			        	empId=Menu.getEmployeeId(scanner);
			        	ServiceEndPoint.CLOCK_IN.execute(restTemplate, Optional.<Object[]>of(new Object[] {empId}));
			            break;
			        case 4:
			        	empId=Menu.getEmployeeId(scanner);
			        	ServiceEndPoint.CLOCK_OUT.execute(restTemplate, Optional.<Object[]>of(new Object[] {empId}));
			        	break;
			        default:
			        	throw new IllegalArgumentException("Incorrect option");
			    }
		    }
		    catch(Exception e) {
		    	System.err.println(e.getMessage());
		    }
        	if(!Menu.wishToContinue(
        			Optional.<String>of("Do you wish continue (Y|*)?"), scanner)){
		    	try {
		    		scanner.close();
		    		System.exit(0);
		    	}
		    	catch(Exception e) {
		    		System.err.print(e.getMessage());
		    	}
        	}
		}
		while(true);
	}
}
