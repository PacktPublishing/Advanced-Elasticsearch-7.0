package com.example.client.restclient.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.client.restclient.common.RestClientResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HighLevelRestClientServiceImpl implements HighLevelRestClientService {

	@Autowired
	private RestHighLevelClient restClient;

	@Override
	public Map<String, Object> search(SearchRequest request, RequestOptions options) {
		SearchResponse response;
		Map<String, Object> convertValue;
		 try {
			response = restClient.search(request, options);
			convertValue = new HashMap<String, Object>();
			if (response.getTook() != null)
				convertValue.put("took", response.getTook().seconds());
			convertValue.put("timed_out", response.isTimedOut());	
			if (response.getHits() != null)
				convertValue.put("hits", response.getHits());
		} catch (IOException e) {
			e.printStackTrace();
			RestClientResponse clientResponse = new RestClientResponse();
			clientResponse.setStatusCode(500);
			convertValue = 
					(Map<String, Object>) (new ObjectMapper()).convertValue(clientResponse, RestClientResponse.class);
		}

		return convertValue;
	}

	@Override
	public Map<String, Object> searchAsync(SearchRequest request, RequestOptions options) {
		restClient.searchAsync(request, options, new ActionListener<SearchResponse>() {
			RestClientResponse clientResponse = new RestClientResponse();
			@Override
			public void onResponse(SearchResponse response) {
				Map<String, Object> convertValue = new HashMap<String, Object>();
				if (response.getTook() != null)
					convertValue.put("took", response.getTook().seconds());
				convertValue.put("timed_out", response.isTimedOut());	
				if (response.getHits() != null)
					convertValue.put("hits", response.getHits());
	            String jsonStr;
				try {
					jsonStr = (new ObjectMapper()).writeValueAsString(convertValue);
		            System.out.println(jsonStr); 
				} catch (JsonProcessingException e) {
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
		RestClientResponse clientResponse = new RestClientResponse();
		clientResponse.setStatusCode(200);
		Map<String, Object> convertValue = 
				(Map<String, Object>) (new ObjectMapper()).convertValue(clientResponse, Map.class);
		return convertValue;
	}
}
