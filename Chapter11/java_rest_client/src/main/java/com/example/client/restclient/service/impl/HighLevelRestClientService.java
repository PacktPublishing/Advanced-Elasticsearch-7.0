package com.example.client.restclient.service.impl;

import java.util.Map;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;

public interface HighLevelRestClientService {
	Map<String, Object> search(SearchRequest request, RequestOptions options);
	Map<String, Object> searchAsync(SearchRequest request, RequestOptions options);
}
