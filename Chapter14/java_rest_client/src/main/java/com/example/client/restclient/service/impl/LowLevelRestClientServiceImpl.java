package com.example.client.restclient.service.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.client.restclient.common.RestClientResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LowLevelRestClientServiceImpl implements LowLevelRestClientService {
	public static final Logger logger = LoggerFactory.getLogger(LowLevelRestClientServiceImpl.class);
	
	@Autowired
	private RestClient restClient;
	
	@Override
	public Map<String, Object> performRequest(Request request) {
		RestClientResponse clientResponse = new RestClientResponse();
		try {
			Response response = restClient.performRequest(request);
			clientResponse.setStatusCode(response.getStatusLine().getStatusCode());
			clientResponse.setResponseBody(EntityUtils.toString(response.getEntity()));
			clientResponse.setHeaders(response.getHeaders());
		} catch (Exception ex) {
			clientResponse.setStatusCode(500);
			if (!ex.getMessage().isEmpty()) {
				clientResponse.setErrMessage(ex.getMessage());
			}
		}
		
		Map<String, Object> convertValue = 
				(Map<String, Object>) (new ObjectMapper()).convertValue(clientResponse, Map.class);
		return convertValue;
	}

	@Override
	public Map<String, Object> performAsyncRequest(Request request) {
		RestClientResponse clientResponse = new RestClientResponse();
		restClient.performRequestAsync(request, new ResponseListener() {
			RestClientResponse clientResponse = new RestClientResponse();
			@Override
			public void onSuccess(Response response) {
				clientResponse.setStatusCode(response.getStatusLine().getStatusCode());
				try {
					clientResponse.setResponseBody(EntityUtils.toString(response.getEntity()));
				} catch (Exception ex) {
					clientResponse.setStatusCode(500);
					if (!ex.getMessage().isEmpty()) {
						clientResponse.setErrMessage(ex.getMessage());
					}
				}
				clientResponse.setHeaders(response.getHeaders());
		        try {  
		            String jsonStr = (new ObjectMapper()).writeValueAsString(clientResponse);  
		            System.out.println(jsonStr); 
		        } 
		        catch (IOException e) { 
		            e.printStackTrace(); 
		        }			
			}

			@Override
			public void onFailure(Exception exception) {
				clientResponse.setStatusCode(500);
				if (!exception.getMessage().isEmpty()) {
					clientResponse.setErrMessage(exception.getMessage());
				}
		        try { 
		            String jsonStr = (new ObjectMapper()).writeValueAsString(clientResponse); 
		            System.out.println(jsonStr); 
		        } 
		        catch (IOException e) { 
		            e.printStackTrace(); 
		        }	
			}
		});

		clientResponse.setStatusCode(200);
		Map<String, Object> convertValue = 
				(Map<String, Object>) (new ObjectMapper()).convertValue(clientResponse, Map.class);
		return convertValue;
	}

}
