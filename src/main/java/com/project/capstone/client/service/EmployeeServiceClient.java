package com.project.capstone.client.service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.project.capstone.CapstoneServerProperties;
import com.project.capstone.client.Menu;
import com.project.capstone.client.ServiceEndPoint;
import com.project.capstone.client.ShiftValidator;
import com.project.capstone.model.ClockState;
import com.project.capstone.model.Employee;
import com.project.capstone.model.Shift;

@Service
public class EmployeeServiceClient {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private CapstoneServerProperties capstoneServerProperties;
	@Autowired
	private ShiftServiceClient shiftServiceClient;
	public Optional<Employee> getEmployee(String empId){
		Employee emp = null;
		
		String url = MessageFormat.format(
							ServiceEndPoint.GET_EMPLOYEES.getUrl(), 
								capstoneServerProperties.getServerHost(), 
									capstoneServerProperties.getServerPort(),
										empId
					);
		ResponseEntity<Employee> response = restTemplate.exchange(
    		url,
    		ServiceEndPoint.GET_EMPLOYEES.getMethod(),
			null,
			new ParameterizedTypeReference<Employee>(){}
    	);
    	if(Objects.nonNull(response) && response.hasBody()) {
    		emp = response.getBody();
    	}			
		return Optional.<Employee>ofNullable(emp);
	}
	
	public List<Employee> getEmployees(){
		String url = MessageFormat.format(ServiceEndPoint.GET_EMPLOYEES.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort(),StringUtils.EMPTY);
		List<Employee> employees = new ArrayList<>();
		ResponseEntity<List<Employee>> response = 
			restTemplate.exchange(
			url,
			ServiceEndPoint.GET_EMPLOYEES.getMethod(),
			null,
			new ParameterizedTypeReference<List<Employee>>(){}
		);
    	if(Objects.nonNull(response) && response.hasBody()) {
    		employees.addAll(response.getBody());
    	}
		return employees;
	}
	
	public boolean addEmployee(Employee e) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("id", e.getEmpId());
		map.add("firstName", e.getFirstName());
		map.add("lastName", e.getLastName());
		map.add("email", e.getEmail());
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		String url = MessageFormat.format(ServiceEndPoint.ADD_EMPLOYEE.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort());
		ResponseEntity<Integer> response = restTemplate.postForEntity(url, request, Integer.class );
		return Objects.nonNull(response) && response.hasBody() && response.getBody()>0;
	}
	public boolean updateEmployee(Employee e) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("firstName", e.getFirstName());
		map.add("lastName", e.getLastName());
		map.add("email", e.getEmail());
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		String url = MessageFormat.format(ServiceEndPoint.UPDATE_EMPLOYEE.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort(),e.getEmpId());
		ResponseEntity<Integer> response = restTemplate.exchange(url, ServiceEndPoint.UPDATE_EMPLOYEE.getMethod(),request, Integer.class);
		return Objects.nonNull(response) && response.hasBody() && response.getBody()>0;
	}
	public boolean deleteEmployee(String empId) {
		String url = MessageFormat.format(ServiceEndPoint.DELETE_EMPLOYEE.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort(),empId);			
		ResponseEntity<Integer> response =
		restTemplate.exchange(url, ServiceEndPoint.DELETE_EMPLOYEE.getMethod(),null,Integer.class);
		return Objects.nonNull(response) && response.hasBody();
	}
	public ClockState clockInOut(ClockState state, String empId) {
		if(state==ClockState.CLOCK_IN)
			return clockIn(empId);
		else
			return clockOut(empId);
	}
	private ClockState clockIn(String empId) {
		String url = MessageFormat.format(ServiceEndPoint.CLOCK_IN.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort(),empId);
		String message = null;
		Optional<Integer> optShiftId = this.shiftServiceClient.getCurrentShift();
		if(Objects.nonNull(optShiftId) && 
				optShiftId.isPresent()) {
    	   int shiftId = optShiftId.get().intValue();
    	   System.out.printf("Shift: %d%n",shiftId);

    	   HttpHeaders headers = new HttpHeaders();
		   headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		   MultiValueMap<String, Integer> map= new LinkedMultiValueMap<String, Integer>();
		   map.add("shiftId", shiftId);
		   HttpEntity<MultiValueMap<String, Integer>> request = new HttpEntity<MultiValueMap<String, Integer>>(map, headers);

		   ResponseEntity<ClockState> response = restTemplate.postForEntity(url, request, ClockState.class);
		   if(response!=null && response.hasBody()){
			   return response.getBody();
		   }
    	}
    	else {
    		message = "Could not fetch the correct shift.\nEmployee record could not be updated";
    	}
		throw new RuntimeException(message);
	}
	private ClockState clockOut(String empId) {
		String url = MessageFormat.format(ServiceEndPoint.CLOCK_OUT.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort(),empId);
    	ClockState response = restTemplate.patchForObject(url, null, ClockState.class );
    	return response;
	}
	public List<Employee> getAllEmployeesShifts() {
		List<Employee> employees = new ArrayList<>();
		String startDate = Menu.getDateInput(Optional.<String>of("Please enter start date (in yyyyMMdd format) "), DateTimeFormatter.BASIC_ISO_DATE);
		String endDate = Menu.getDateInput(Optional.<String>of("Please enter start date (in yyyyMMdd format) "), DateTimeFormatter.BASIC_ISO_DATE);
		String queryString = MessageFormat.format("?startDate={0}&endDate={1}",startDate,endDate);
		String url = MessageFormat.format(ServiceEndPoint.GET_EMPLOYEE_SHIFTS.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort(), StringUtils.EMPTY)+ "shifts" + queryString;
		ResponseEntity<List<Employee>> response = restTemplate.exchange(
        	url,
        	ServiceEndPoint.GET_EMPLOYEE_SHIFTS.getMethod(),
  			null,
  			new ParameterizedTypeReference<List<Employee>>(){}
        );
        if(Objects.nonNull(response) && response.hasBody()) {
         	  employees.addAll(response.getBody());
        }
		return employees;
	}
	public Optional<Employee> getEmployeeShifts(String empId) {
		Employee emp = null;
		String startDate = Menu.getDateInput(Optional.<String>of("Please enter start date (in yyyyMMdd format) "), DateTimeFormatter.BASIC_ISO_DATE);
		String endDate = Menu.getDateInput(Optional.<String>of("Please enter start date (in yyyyMMdd format) "), DateTimeFormatter.BASIC_ISO_DATE);
		String queryString = MessageFormat.format("?startDate={0}&endDate={1}",startDate,endDate);
		String url = MessageFormat.format(ServiceEndPoint.GET_EMPLOYEE_SHIFTS.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort(), empId)+ "/shifts" + queryString;
		ResponseEntity<Employee> response = restTemplate.exchange(
    		url,
    		ServiceEndPoint.GET_EMPLOYEE_SHIFTS.getMethod(),
			null,
			new ParameterizedTypeReference<Employee>(){}
        );
    	if(Objects.nonNull(response) && response.hasBody()) {
     	  emp = response.getBody();
     	}
    	return Optional.<Employee>ofNullable(emp);
	}
	public boolean updateEmployeeShift(String empId, 
										int shiftId, 
											int newShiftId, 
												String newShiftStartTime, 
													String newShiftEndTime,
														String workingDate) {
		List<Shift> shifts = shiftServiceClient.getShifts();
		if(!ShiftValidator.isValid(newShiftId,shifts) || !ShiftValidator.isValid(shiftId,shifts)) {
			throw new IllegalArgumentException("Shift id must be between 1 and 3");
		}
		try {
			LocalDate.parse(workingDate, DateTimeFormatter.BASIC_ISO_DATE);
		}
		catch(DateTimeParseException e) {
			throw new IllegalArgumentException("Required date format yyyyMMdd");
		}
		String url = MessageFormat.format(ServiceEndPoint.UPDATE_EMPLOYEE_SHIFT.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort(), empId, shiftId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, Object> map= new LinkedMultiValueMap<String, Object>();
		map.add("newShiftId", newShiftId);
		map.add("newShiftStartTime", newShiftStartTime);
		map.add("newShiftEndTime", newShiftEndTime);
		map.add("workingDate", workingDate);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
		ResponseEntity<Integer> response = restTemplate.exchange(url, ServiceEndPoint.UPDATE_EMPLOYEE_SHIFT.getMethod(),request, Integer.class);
		return Objects.nonNull(response) && response.hasBody() && response.getBody()>0;
	}
}
