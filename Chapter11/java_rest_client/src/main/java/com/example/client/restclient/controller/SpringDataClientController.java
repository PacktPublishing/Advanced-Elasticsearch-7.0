package com.example.client.restclient.controller;

//TODO: Wait for the support of Elasticsearch 7.0
//import java.io.IOException;
//import java.util.Map;
//
//import org.apache.http.util.EntityUtils;
//import org.elasticsearch.action.ActionListener;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.client.Request;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.Response;
//import org.elasticsearch.client.ResponseListener;
//import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.client.restclient.service.impl.HighLevelRestClientService;
//import com.example.client.restclient.service.impl.SpringDataClientService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;


//@RestController
//@RequestMapping("/api/springdataclient")
//public class SpringDataClientController {
//	public static final Logger logger = LoggerFactory.getLogger(SpringDataClientController.class);
//	
//	@Autowired
//	private SpringDataClientService springDataClient;
//	
//	@ApiOperation("Spring Data Elasticsearch Client Search Request Match Phrase Query")
//	@ApiImplicitParams({
//		@ApiImplicitParam(paramType = "query", name="indexName", type="string", required=true),
//		@ApiImplicitParam(paramType = "query", name="fieldName", type="string", required=true),
//		@ApiImplicitParam(paramType = "query", name="fieldValue", type="string", required=true),
//		@ApiImplicitParam(paramType = "query", name="analyzer", type="string", required=false),
//		@ApiImplicitParam(paramType = "query", name="from", type="int", required=false, defaultValue="0"),
//		@ApiImplicitParam(paramType = "query", name="size", type="int", required=false, defaultValue="25"),
//	})
//	@RequestMapping(value="/search/match_phrase_query", method=RequestMethod.POST)
//	public ResponseEntity<Map<String, Object>> searchRequest(
//			@RequestParam(value = "indexName") String indexName,
//			@RequestParam(value = "fieldName") String fieldName,
//			@RequestParam(value = "fieldValue") String fieldValue,
//			@RequestParam(value = "analyzer", required=false) String analyzer,
//			@RequestParam(value = "from", required=false) int from,
//			@RequestParam(value = "size", required=false) int size) throws Exception {
//
//			Map<String,Object> response = springDataClient.match_phrase_query(indexName, analyzer, fieldName, fieldValue, from, size);
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
//	}
//	
//	@ApiOperation("Spring Data Elasticsearch Client Search Symbol")
//	@ApiImplicitParams({
//		@ApiImplicitParam(paramType = "query", name="indexName", type="string", required=true),
//		@ApiImplicitParam(paramType = "query", name="symbol", type="string", required=true),
//		@ApiImplicitParam(paramType = "query", name="from", type="int", required=false, defaultValue="0"),
//		@ApiImplicitParam(paramType = "query", name="size", type="int", required=false, defaultValue="25"),
//	})
//	@RequestMapping(value="/search/match_phrase_query", method=RequestMethod.POST)
//	public ResponseEntity<Map<String, Object>> searchRequest(
//			@RequestParam(value = "indexName") String indexName,
//			@RequestParam(value = "symbol") String symbol,
//			@RequestParam(value = "from", required=false) int from,
//			@RequestParam(value = "size", required=false) int size) throws Exception {
//
//			Map<String,Object> response = springDataClient.findSymbol(indexName, symbol);
//			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
//	}
//	
//}
