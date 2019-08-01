package com.project.capstone.client;

import java.text.MessageFormat;
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

import com.project.capstone.model.ClockState;
import com.project.capstone.model.Employee;
import com.project.capstone.model.Shift;

public enum ServiceEndPoint {
	GET_SHIFTS( "http://localhost:8081/capstone/shifts", HttpMethod.GET)
	{
		@Override
		public void execute(RestTemplate restTemplate, Optional<Object[]> optPathVariables) {
			ResponseEntity<List<Shift>> response = restTemplate.exchange(
        			this.getUrl(),
        			this.getMethod(),
  				  null,
  				  new ParameterizedTypeReference<List<Shift>>(){});
        	if(Objects.nonNull(response) && response.hasBody()) {
        	   response.getBody().stream().forEach(System.out::println);
        	}
		}
	},
	GET_EMPLOYEES("http://localhost:8081/capstone/employees/{0}", HttpMethod.GET)
	{
		@Override
		public void execute(RestTemplate restTemplate,Optional<Object[]> optPathVariables) {
			if(optPathVariables!=null && optPathVariables.isPresent()) {
				String url = MessageFormat.format(
	    				this.getUrl(),
	    				optPathVariables.get());
	        	ResponseEntity<Employee> response = restTemplate.exchange(
	        			url,
	        			this.getMethod(),
	  				  null,
	  				  new ParameterizedTypeReference<Employee>(){});
	        	if(Objects.nonNull(response) && response.hasBody()) {
	        	   System.out.println(response.getBody());
	        	}
			}
			else {
				String url = MessageFormat.format(
	    				this.getUrl(),
	    				StringUtils.EMPTY);

				ResponseEntity<List<Employee>> response = restTemplate.exchange(
	        			url,
	        			this.getMethod(),
	  				  null,
	  				  new ParameterizedTypeReference<List<Employee>>(){});
	        	if(Objects.nonNull(response) && response.hasBody()) {
	        	   response.getBody().stream().forEach(System.out::println);
	        	}
			}
		}
	},
	/*
	ADD_EMPLOYEE("http://localhost:8081/capstone/employees", HttpMethod.POST)
	{
		@Override
		public void execute(RestTemplate restTemplate,Optional<Object[]> optPathVariables) {
		}
	},
	UPDATE_EMPLOYEE("http://localhost:8081/capstone/employees/{0}", HttpMethod.PUT),
	{
		@Override
		public void execute(RestTemplate restTemplate,Optional<Object[]> optPathVariables) {
		}
	}
	DELETE_EMPLOYEE("http://localhost:8081/capstone/employees/{0}", HttpMethod.DELETE),
	{
		@Override
		public void execute(RestTemplate restTemplate,Optional<Object[]> optPathVariables) {
		}
	}*/
	
	CLOCK_IN("http://localhost:8081/capstone/employees/{0}/clockin", HttpMethod.POST)
	{
		@Override
		public void execute(RestTemplate restTemplate,Optional<Object[]> optPathVariables) {
			
			if(optPathVariables==null || !optPathVariables.isPresent()) {
				System.err.println("Missing path variables");
				return;
			}
			String url = MessageFormat.format(this.getUrl(), optPathVariables.get());
        	int shiftId = -1;
        	ResponseEntity<Integer> shiftResponse = restTemplate.exchange(
        		"http://localhost:8081/capstone/shifts/closest",
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
	
			   ResponseEntity<ClockState> response = restTemplate.postForEntity( url, request , ClockState.class );
			   if(response!=null && response.hasBody()){
				   System.out.printf("%s%n",response.getBody().name());
			   }
        	}
        	else {
        		System.err.println("Could not fetch the correct shift id");
        	}
		}
	},
	CLOCK_OUT("http://localhost:8081/capstone/employees/{0}/clockout", HttpMethod.PATCH)
	{
		@Override
		public void execute(RestTemplate restTemplate,Optional<Object[]> optPathVariables) {
			if(optPathVariables==null || !optPathVariables.isPresent()) {
				System.err.println("Missing path variables");
				return;
			}
			String url = MessageFormat.format(this.getUrl(), optPathVariables.get());
        	ClockState response = restTemplate.patchForObject( url, null, ClockState.class );
        	if(response!=null){
        		System.out.printf("%s%n",response.name());
        	}
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
	public abstract void execute(RestTemplate restTemplate, Optional<Object[]> optPathVariables);
}
