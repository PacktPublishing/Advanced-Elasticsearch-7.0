package com.example.client.restclient.service.impl;

import java.util.Map;

import org.elasticsearch.client.Request;

public interface LowLevelRestClientService {
	Map<String, Object> performRequest(Request request);
	Map<String, Object> performAsyncRequest(Request request);
}
