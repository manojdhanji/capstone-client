package com.project.capstone.client.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.capstone.CapstoneServerProperties;
import com.project.capstone.client.ServiceEndPoint;
import com.project.capstone.model.Shift;
@Service
public class ShiftServiceClient {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private CapstoneServerProperties capstoneServerProperties;
	
	public List<Shift> getShifts(){
		List<Shift> shifts = new ArrayList<>();
		ResponseEntity<List<Shift>> response = restTemplate.exchange(
			MessageFormat.format(ServiceEndPoint.GET_SHIFTS.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort()),
    		ServiceEndPoint.GET_SHIFTS.getMethod(),
			null,
			new ParameterizedTypeReference<List<Shift>>(){}
		);
        if(Objects.nonNull(response) && response.hasBody()) {
        	 shifts.addAll(response.getBody());
        }
        return shifts;
	}
	public Optional<Integer> getCurrentShift(){
		Integer shiftId = null;
		ResponseEntity<Integer> response = restTemplate.exchange(
    		MessageFormat.format(ServiceEndPoint.GET_CURRENT_SHIFT.getUrl(), capstoneServerProperties.getServerHost(), capstoneServerProperties.getServerPort()),
    		ServiceEndPoint.GET_CURRENT_SHIFT.getMethod(),
			null,
			new ParameterizedTypeReference<Integer>(){}
    	);
        if(Objects.nonNull(response) && 
        		response.hasBody()) {
        	shiftId = response.getBody();
        }
        return Optional.<Integer>ofNullable(shiftId);
	}
}
