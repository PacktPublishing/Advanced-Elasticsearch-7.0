package com.example.client.restclient.controller;

import java.util.Map;

import org.elasticsearch.client.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.client.restclient.service.impl.LowLevelRestClientService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/api/llrestclient")
public class LowLevelRestClientController {
	public static final Logger logger = LoggerFactory.getLogger(LowLevelRestClientController.class);
	
	@Autowired
	private LowLevelRestClientService llRestClient;
	
	@ApiOperation("Low Level REST Client GET Request")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="uRL", type="string", required=true, defaultValue="/")
	})
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> performGetRequest(
			@RequestParam(value = "uRL") String uRL) throws Exception {
				Request request = new Request("GET", uRL);
				//request.addParameter("pretty", "true");
				Map<String,Object> response = llRestClient.performRequest(request);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@ApiOperation("Low Level REST Client POST Request")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="uRL", type="String", required=true, defaultValue="/"),
		@ApiImplicitParam(paramType = "body", name="body", type="String", required=true, defaultValue="{}"),
	})
	@RequestMapping(value="/post", method=RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> performPostRequest(
			@RequestParam(value = "uRL") String uRL,
			@RequestBody String body) throws Exception {
				Request request = new Request("POST", uRL);
				request.setJsonEntity(body);
				//request.addParameter("pretty", "true");
				Map<String,Object> response = llRestClient.performRequest(request);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@ApiOperation("Low Level REST Client PUT Request")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="uRL", type="string", required=true, defaultValue="/"),
		@ApiImplicitParam(paramType = "body", name="requestBody", type="string", required=true, defaultValue="{}")
	})
	@RequestMapping(value="/put", method=RequestMethod.PUT)
	public ResponseEntity<Map<String, Object>> performPutRequest(
			@RequestParam(value = "uRL") String uRL,
			@RequestBody String requestBody) throws Exception {
				Request request = new Request("PUT", uRL);
				request.setJsonEntity(requestBody);
				//request.addParameter("pretty", "true");
				Map<String,Object> response = llRestClient.performRequest(request);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@ApiOperation("Low Level REST Client DELETE Request")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="uRL", type="string", required=true, defaultValue="/"),
	})
	@RequestMapping(value="/delete", method=RequestMethod.DELETE)
	public ResponseEntity<Map<String, Object>> performDeleteRequest(
			@RequestParam(value = "uRL") String uRL) throws Exception {
				Request request = new Request("DELETE", uRL);
				//request.addParameter("pretty", "true");
				Map<String,Object> response = llRestClient.performRequest(request);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@ApiOperation("Low Level REST Client Async POST Request")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="uRL", type="string", required=true, defaultValue="/"),
		@ApiImplicitParam(paramType = "body", name="requestBody", type="string", required=true, defaultValue="{}"),
	})
	@RequestMapping(value="/async_post", method=RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> performAsyncPostRequest(
			@RequestParam(value = "uRL") String uRL,
			@RequestBody String requestBody) throws Exception {
				Request request = new Request("POST", uRL);
				request.setJsonEntity(requestBody);
				//request.addParameter("pretty", "true");
				Map<String,Object> response = llRestClient.performAsyncRequest(request);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
