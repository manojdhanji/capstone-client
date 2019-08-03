package com.project.capstone.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.client.RestTemplate;

import com.project.capstone.RestServiceProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = RestServiceProperties.class)
public class CapstoneClientApplication implements CommandLineRunner {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RestServiceProperties restServiceProperties;
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
			        	ServiceEndPoint.GET_SHIFTS.execute(restTemplate, restServiceProperties);
			        	break;
			        case 2:
					    ServiceEndPoint.GET_EMPLOYEES.execute(restTemplate, restServiceProperties);
			        	break;
			        case 3:
			        	ServiceEndPoint.ADD_EMPLOYEE.execute(restTemplate, restServiceProperties);
			        	break;
			        case 4:
			        	ServiceEndPoint.UPDATE_EMPLOYEE.execute(restTemplate, restServiceProperties);
			        	break;
			        case 5:
			        	ServiceEndPoint.DELETE_EMPLOYEE.execute(restTemplate, restServiceProperties);
			        	break;
			        case 6:
			        	ServiceEndPoint.CLOCK_IN.execute(restTemplate, restServiceProperties);
			            break;
			        case 7:
			        	ServiceEndPoint.CLOCK_OUT.execute(restTemplate, restServiceProperties);
			        	break;
			        case 8:
			        	ServiceEndPoint.GET_EMPLOYEE_SHIFTS.execute(restTemplate, restServiceProperties);
			        	break;
			        case 9:
			        	Menu.closeMenu();
			    		System.exit(0);
			        	break;
			        default:
			        	throw new IllegalArgumentException("Incorrect option");
			    }
		    }
		    catch(Exception e) {
		    	System.err.println(e.getMessage());
		    }
		}
		while(true);
	}
}
