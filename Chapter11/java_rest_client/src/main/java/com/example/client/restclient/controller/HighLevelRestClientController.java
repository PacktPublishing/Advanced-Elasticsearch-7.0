package com.example.client.restclient.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

import com.example.client.restclient.service.impl.HighLevelRestClientService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/api/hlrestclient")
public class HighLevelRestClientController {
	public static final Logger logger = LoggerFactory.getLogger(HighLevelRestClientController.class);
	
	@Autowired
	private HighLevelRestClientService hlRestClient;
	
	@ApiOperation("High Level REST Client Search Request Match Phrase Query")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="indexName", type="string", required=true),
		@ApiImplicitParam(paramType = "query", name="fieldName", type="string", required=true),
		@ApiImplicitParam(paramType = "query", name="fieldValue", type="string", required=true),
		@ApiImplicitParam(paramType = "query", name="analyzer", type="string", required=false),
		@ApiImplicitParam(paramType = "query", name="from", type="int", required=false, defaultValue="0"),
		@ApiImplicitParam(paramType = "query", name="size", type="int", required=false, defaultValue="25"),
	})
	@RequestMapping(value="/search/match_phrase_query", method=RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> searchRequest(
			@RequestParam(value = "indexName") String indexName,
			@RequestParam(value = "fieldName") String fieldName,
			@RequestParam(value = "fieldValue") String fieldValue,
			@RequestParam(value = "analyzer", required=false) String analyzer,
			@RequestParam(value = "from", required=false) int from,
			@RequestParam(value = "size", required=false) int size) throws Exception {
				SearchRequest request = new SearchRequest(indexName);
				SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
				sourceBuilder.from(from);
				sourceBuilder.size(size);
				MatchPhraseQueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery(fieldName, fieldValue);
				if (analyzer != null)
					queryBuilder.analyzer(analyzer);
				request.source(sourceBuilder.query(queryBuilder));
				RequestOptions options = RequestOptions.DEFAULT;
				Map<String,Object> response = hlRestClient.search(request, options);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	
	@ApiOperation("High Level REST Client Async Search Request Match Phrase Query")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="indexName", type="string", required=true),
		@ApiImplicitParam(paramType = "query", name="fieldName", type="string", required=true),
		@ApiImplicitParam(paramType = "query", name="fieldValue", type="string", required=true),
		@ApiImplicitParam(paramType = "query", name="analyzer", type="string", required=false),
		@ApiImplicitParam(paramType = "query", name="from", type="int", required=false, defaultValue="0"),
		@ApiImplicitParam(paramType = "query", name="size", type="int", required=false, defaultValue="25"),
	})
	@RequestMapping(value="/async_search/match_phrase_query", method=RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> asyncSearchRequest(
			@RequestParam(value = "indexName") String indexName,
			@RequestParam(value = "fieldName") String fieldName,
			@RequestParam(value = "fieldValue") String fieldValue,
			@RequestParam(value = "analyzer", required=false) String analyzer,
			@RequestParam(value = "from", required=false) int from,
			@RequestParam(value = "size", required=false) int size) throws Exception {
		SearchRequest request = new SearchRequest(indexName);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.from(from);
		sourceBuilder.size(size);
		MatchPhraseQueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery(fieldName, fieldValue);
		if (analyzer != null)
			queryBuilder.analyzer(analyzer);
		request.source(sourceBuilder.query(queryBuilder));
		RequestOptions options = RequestOptions.DEFAULT;
			Map<String,Object> response = hlRestClient.searchAsync(request, options);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
