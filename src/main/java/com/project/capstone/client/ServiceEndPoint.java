package com.project.capstone.client;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.project.capstone.RestServiceProperties;
import com.project.capstone.model.ClockState;
import com.project.capstone.model.Employee;
import com.project.capstone.model.Shift;

public enum ServiceEndPoint {
	GET_SHIFTS( "http://{0}:{1}/capstone/shifts", HttpMethod.GET)
	{
		@Override
		public void execute(RestTemplate restTemplate,RestServiceProperties restServiceProperties) {
			ResponseEntity<List<Shift>> response = restTemplate.exchange(
				MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort()),
        		this.getMethod(),
  				null,
  				new ParameterizedTypeReference<List<Shift>>(){}
			);
        	if(Objects.nonNull(response) && response.hasBody()) {
        	   response.getBody().stream().forEach(System.out::println);
        	}
		}
	},
	GET_EMPLOYEES("http://{0}:{1}/capstone/employees/{2}", HttpMethod.GET)
	{
		@Override
		public void execute(RestTemplate restTemplate,RestServiceProperties restServiceProperties) {
			
			String empId = null;
			if(Menu.yesOrNo(
        			Optional.<String>of("Do you wish to enter an employee id (Y|*)? "))){
        		empId=Menu.getEmployeeId();
        	}
			Optional<Object> optEmpId = Optional.<Object>ofNullable(empId);
			if(optEmpId.isPresent()) {
				String url = MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort(),optEmpId.get());
	        	ResponseEntity<Employee> response = restTemplate.exchange(
	        		url,
	        		this.getMethod(),
	  				null,
	  				new ParameterizedTypeReference<Employee>(){}
	        	);
	        	if(Objects.nonNull(response) && response.hasBody()) {
	        	   System.out.println(response.getBody());
	        	}
			}
			else {
				String url = MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort(),StringUtils.EMPTY);
				ResponseEntity<List<Employee>> response = 
						restTemplate.exchange(
							url,
							this.getMethod(),
							null,
							new ParameterizedTypeReference<List<Employee>>(){}
						);
	        	if(Objects.nonNull(response) && response.hasBody()) {
	        	   response.getBody().stream().forEach(System.out::println);
	        	}
			}
		}
	},
	
	ADD_EMPLOYEE("http://{0}:{1}/capstone/employees", HttpMethod.POST)
	{
		@Override
		public void execute(RestTemplate restTemplate,RestServiceProperties restServiceProperties) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("id", Menu.getEmployeeId());
			map.add("firstName", Menu.getStringInput("Please enter first name? "));
			map.add("lastName", Menu.getStringInput("Please enter last name? "));
			map.add("email", Menu.getEmail());
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			String url = MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort());
			ResponseEntity<Integer> response = restTemplate.postForEntity(url, request, Integer.class );
			if(response!=null && response.hasBody()){
			   System.out.printf("%d record inserted %n",response.getBody());
			}
		}
	},
	UPDATE_EMPLOYEE("http://{0}:{1}/capstone/employees/{2}", HttpMethod.PUT)
	{
		@Override
		public void execute(RestTemplate restTemplate,RestServiceProperties restServiceProperties) {
			String empId=Menu.getEmployeeId();
			String url = MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort(),empId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("firstName", Menu.getStringInput("Please enter first name? "));
			map.add("lastName", Menu.getStringInput("Please enter last name? "));
			map.add("email", Menu.getEmail());
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			ResponseEntity<Integer> response =
			restTemplate.exchange(url, this.getMethod(),request, Integer.class);
			
			if(response!=null && response.hasBody()){
			   System.out.printf("%d record updated %n",response.getBody());
			}
		}
	},
	DELETE_EMPLOYEE("http://{0}:{1}/capstone/employees/{2}", HttpMethod.DELETE)
	{
		@Override
		public void execute(RestTemplate restTemplate,RestServiceProperties restServiceProperties) {
			String empId=Menu.getEmployeeId();
			String url = MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort(),empId);			
			ResponseEntity<Integer> response =
			restTemplate.exchange(url, this.getMethod(),null,Integer.class);
			
			if(response!=null && response.hasBody()){
			   System.out.printf("%d record deleted %n",response.getBody());
			}
		}
	}
	,
	CLOCK_IN("http://{0}:{1}/capstone/employees/{2}/clockin", HttpMethod.POST)
	{
		@Override
		public void execute(RestTemplate restTemplate,RestServiceProperties restServiceProperties) {
			String empId=Menu.getEmployeeId();
			String url = MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort(),empId);
        	int shiftId = -1;
        	ResponseEntity<Integer> shiftResponse = restTemplate.exchange(
        		MessageFormat.format("http://{0}:{1}/capstone/shifts/closest", restServiceProperties.getServiceHost(), restServiceProperties.getServicePort()),
        		HttpMethod.GET,
  				null,
  				new ParameterizedTypeReference<Integer>(){}
        	);
        	if(Objects.nonNull(shiftResponse) && 
        			shiftResponse.hasBody()) {
        	   shiftId = shiftResponse.getBody().intValue();
        	   System.out.printf("Shift: %d%n",shiftId);

        	   HttpHeaders headers = new HttpHeaders();
			   headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			   MultiValueMap<String, Integer> map= new LinkedMultiValueMap<String, Integer>();
			   map.add("shiftId", shiftId);
			   HttpEntity<MultiValueMap<String, Integer>> request = new HttpEntity<MultiValueMap<String, Integer>>(map, headers);
	
			   ResponseEntity<ClockState> response = restTemplate.postForEntity(url, request, ClockState.class );
			   if(response!=null && response.hasBody()){
				   System.out.printf("%s%n",response.getBody().name());
			   }
        	}
        	else {
        		System.err.println("Could not fetch the correct shift id");
        	}
		}
	},
	CLOCK_OUT("http://{0}:{1}/capstone/employees/{2}/clockout", HttpMethod.PATCH)
	{
		@Override
		public void execute(RestTemplate restTemplate,RestServiceProperties restServiceProperties) {
			String empId=Menu.getEmployeeId();
			String url = MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort(),empId);
        	ClockState response = restTemplate.patchForObject(url, null, ClockState.class );
        	if(response!=null){
        		System.out.printf("%s%n",response.name());
        	}
		}
	},
	
	GET_EMPLOYEE_SHIFTS("http://{0}:{1}/capstone/employees/{2}", HttpMethod.GET)
	{
	
		@Override
		public void execute(RestTemplate restTemplate,RestServiceProperties restServiceProperties) {
			String url = null;
			String startDate = Menu.getDateInput(Optional.<String>of("Please enter start date (in yyyyMMdd format) "), DateTimeFormatter.BASIC_ISO_DATE);
			String endDate = Menu.getDateInput(Optional.<String>of("Please enter start date (in yyyyMMdd format) "), DateTimeFormatter.BASIC_ISO_DATE);
			String queryString = MessageFormat.format("?startDate={0}&endDate={1}",startDate,endDate);
			if(Menu.yesOrNo(
        			Optional.<String>of("Do you wish to enter an employee id (Y|*)? "))){
				url = MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort(), Menu.getEmployeeId())+ "/shifts" + queryString;
				ResponseEntity<Employee> response = restTemplate.exchange(
	        		url,
	        		this.getMethod(),
	  				null,
	  				new ParameterizedTypeReference<Employee>(){}
		        );
	        	if(Objects.nonNull(response) && response.hasBody()) {
	         	   System.out.println(response.getBody());
	         	}
        	}
			else {
				url = MessageFormat.format(this.getUrl(), restServiceProperties.getServiceHost(), restServiceProperties.getServicePort(), StringUtils.EMPTY)+ "shifts" + queryString;
				ResponseEntity<List<Employee>> response = restTemplate.exchange(
	        		url,
	        		this.getMethod(),
	  				null,
	  				new ParameterizedTypeReference<List<Employee>>(){}
	        	);
	        	if(Objects.nonNull(response) && response.hasBody()) {
	         	  response.getBody().stream().forEach(System.out::println);
	         	}
			}
			/*
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("startDate", startDate);
			map.add("endDate", endDate);
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			*/
		}
	};
	private ServiceEndPoint(String url, HttpMethod method) {
		this.url = url;
		this.method = method;
	}
	private HttpMethod method;
	private String url;
	public HttpMethod getMethod() {
		return this.method;
	}
	public String getUrl() {
		return this.url;
	}
	public abstract void execute(RestTemplate restTemplate, RestServiceProperties props);
}
