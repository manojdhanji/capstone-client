package com.project.capstone.client;

import org.springframework.http.HttpMethod;

public enum ServiceEndPoint {
	GET_CURRENT_SHIFT("http://{0}:{1}/capstone/shifts/closest", HttpMethod.GET),
	GET_SHIFTS( "http://{0}:{1}/capstone/shifts", HttpMethod.GET),
	GET_EMPLOYEES("http://{0}:{1}/capstone/employees/{2}", HttpMethod.GET),
	ADD_EMPLOYEE("http://{0}:{1}/capstone/employees", HttpMethod.POST),
	UPDATE_EMPLOYEE("http://{0}:{1}/capstone/employees/{2}", HttpMethod.PUT),
	DELETE_EMPLOYEE("http://{0}:{1}/capstone/employees/{2}", HttpMethod.DELETE),
	CLOCK_IN("http://{0}:{1}/capstone/employees/{2}/clockin", HttpMethod.POST),
	CLOCK_OUT("http://{0}:{1}/capstone/employees/{2}/clockout", HttpMethod.PATCH),
	GET_EMPLOYEE_SHIFTS("http://{0}:{1}/capstone/employees/{2}", HttpMethod.GET),
	UPDATE_EMPLOYEE_SHIFT("http://{0}:{1}/capstone/employees/{2}/shifts/{3}", HttpMethod.PUT);
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
}
