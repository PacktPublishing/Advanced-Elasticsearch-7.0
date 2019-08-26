package com.example.esanalytics.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.pipeline.BucketScriptPipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.MovFnPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import com.example.esanalytics.common.BaseData;
import com.example.esanalytics.common.HistoryData;
import com.example.esanalytics.common.RegisterFund;
import com.example.esanalytics.service.EsDataService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Paths;

@Service
public class EsDataServiceImpl implements EsDataService {
	public static final Logger logger = LoggerFactory.getLogger(EsDataServiceImpl.class);

	@Autowired
	private RestHighLevelClient restClient;
	
	@Autowired
	private IndicesClient indexClient;
	
	@Value("${elasticsearch.data_index}")
	private String dataIndexName;
	
	@Value("${elasticsearch.register_funds_index}")
	private String registerIndexName;

	
	@Override
	public Map<String, Object> getBollingerBand(String symbol, String startDate, String endDate) {
		SearchResponse response;
		Map<String, Object> returns = new HashMap<String, Object>();
		 try {
			SearchRequest request = new SearchRequest(dataIndexName);
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			//sourceBuilder.from(from);
			//sourceBuilder.size(size);
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.termQuery("symbol", symbol));
			boolQueryBuilder.must(QueryBuilders.rangeQuery("date").gte(startDate).lte(endDate));
			request.source(sourceBuilder.query(boolQueryBuilder));
			AggregationBuilder bollingerBuilder = AggregationBuilders.dateHistogram("BollingerBand")
					.field("date").dateHistogramInterval(DateHistogramInterval.days(1)).format("yyyy-MM-dd")
					.minDocCount(1L);
			AggregationBuilder tpBuilder = AggregationBuilders.scriptedMetric("tp")
					.initScript(new Script("state.totals=[]"))
					.mapScript(new Script("state.totals.add((doc.high.value+doc.low.value+doc.close.value)/3)"))
					.combineScript(new Script("double total=0; for (t in state.totals) {total += t} return total"))
					.reduceScript(new Script("return states[0]"));
			MovFnPipelineAggregationBuilder tDMA = 
					PipelineAggregatorBuilders.movingFunction("tdMA", 
							new Script("MovingFunctions.unweightedAvg(values)"), "tp.value", 20);
			MovFnPipelineAggregationBuilder tDStdDev = 
					PipelineAggregatorBuilders.movingFunction("tdStdDev", 
						new Script("MovingFunctions.stdDev(values, MovingFunctions.unweightedAvg(values))"), 
							"tp.value", 20);
			Map<String, String> bucketPath = new HashMap<String, String>();
			bucketPath.put("SMA", "tdMA");
			bucketPath.put("StdDev", "tdStdDev");
			BucketScriptPipelineAggregationBuilder bbu = 
					PipelineAggregatorBuilders.bucketScript("bbu", bucketPath, new Script("params.SMA + 2 * params.StdDev")); 
			BucketScriptPipelineAggregationBuilder bbl = 
					PipelineAggregatorBuilders.bucketScript("bbl", bucketPath, new Script("params.SMA - 2 * params.StdDev")); 
			bollingerBuilder.subAggregation(tpBuilder).subAggregation(tDMA).subAggregation(tDStdDev)
				.subAggregation(bbu).subAggregation(bbl);
			sourceBuilder.aggregation(bollingerBuilder);
			RequestOptions options = RequestOptions.DEFAULT;
			response = restClient.search(request, options);
			returns = new HashMap<String, Object>();
			if (response.getTook() != null)
				returns.put("took", response.getTook().seconds());
			returns.put("timed_out", response.isTimedOut());	
			if (response.getHits() != null)
				returns.put("hits", response.getHits());
			if (response.getAggregations() != null)
				returns.put("aggs", response.getAggregations());
		} catch (IOException e) {
			logger.error(e.getMessage());
			returns.put("message", e.getMessage());
			returns.put("status", 500);
		}

		return returns;
	}
	
	@Override
	public Map<String, Object> getEarliestData(String symbol) {
		SearchResponse response;
		Map<String, Object> returns = new HashMap<String, Object>();
		HistoryData earliestData = null;
		try {
			SearchRequest request = new SearchRequest(dataIndexName);
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			RequestOptions options = RequestOptions.DEFAULT;
			request.source(sourceBuilder.query(QueryBuilders.termQuery("symbol", symbol)).sort("date", SortOrder.ASC).size(1));
			response = restClient.search(request, options);
			SearchHit[] hits = response.getHits().getHits();
			if (hits.length == 1) {
				Map<String, Object> map = hits[0].getSourceAsMap();
				earliestData = 
						(new ObjectMapper()).convertValue(map, HistoryData.class);
				earliestData.setId(hits[0].getId());
			}
			returns.put("doc", earliestData);
		} catch (IOException e) {
			logger.error(e.getMessage());
			returns.put("message", e.getMessage());
			returns.put("status", 500);
		}

		return returns;
	}
	
	public boolean bulkIndexHistoryData(HistoryData[] historyData) {
		BulkRequest request = new BulkRequest();
		
		for (HistoryData data : historyData) {
			IndexRequest indexRequest = new IndexRequest(dataIndexName);
			Map<String, Object> histDataMap = (Map<String, Object>) (new ObjectMapper()).convertValue(data, new TypeReference<Map<String, Object>>() {});
			histDataMap.remove("id");
			indexRequest.source(histDataMap);
			request.add(indexRequest);
		}
		try {
			BulkResponse bulkResponse = restClient.bulk(request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL), RequestOptions.DEFAULT);
			if (bulkResponse.hasFailures()) {
				for (BulkItemResponse bulkItemResponse : bulkResponse) {
				    if (bulkItemResponse.isFailed()) { 
				        BulkItemResponse.Failure failure =
				                bulkItemResponse.getFailure(); 
				        logger.error(failure.getMessage());
				    }
				}
				return false;
			}
			else {
				int index =0;
				for (BulkItemResponse ir: bulkResponse.getItems()) {
					historyData[index].setId(ir.getId());
					index++;
				}
				return true;
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	
	public boolean deleteIndex(String indexName) {
		GetIndexRequest getRequest = new GetIndexRequest(indexName);
		try {
			boolean exists = indexClient.exists(getRequest, RequestOptions.DEFAULT);
			if (exists) {
				DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
				deleteRequest.timeout(TimeValue.timeValueMinutes(2));
				AcknowledgedResponse response = indexClient.delete(deleteRequest, RequestOptions.DEFAULT);
				return response.isAcknowledged();
			} else
				return true;
		} catch (ElasticsearchException ex) {
			logger.error(ex.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	
	public boolean createIndex(String indexName, Map<String, Object> settingsMappings) {
		CreateIndexRequest request = new CreateIndexRequest(indexName);
		Map<String, Object> map =null;
		if (settingsMappings != null) {
			if (settingsMappings.containsKey("mappings")) 
				request.mapping((Map<String, Object>)settingsMappings.get("mappings"));
			
			if (settingsMappings.containsKey("settings")) 
				request.settings((Map<String, Object>) settingsMappings.get("settings"));
		}
		
		try {
			AcknowledgedResponse response = indexClient.create(request, RequestOptions.DEFAULT);
			return response.isAcknowledged();
		} catch (ElasticsearchException ex) {
			logger.error(ex.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public Map<String, Object> getLatestData(String symbol) {
		Map<String, Object> returns = new HashMap<String, Object>();
		HistoryData historyData = getLatestHistoryData(symbol);
		returns.put("doc", historyData);
		return returns;
	}
	
	public HistoryData getLatestHistoryData(String symbol) {
		SearchResponse response;
		HistoryData latestData=null;
		SearchRequest request = new SearchRequest(dataIndexName);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		RequestOptions options = RequestOptions.DEFAULT;
		request.source(sourceBuilder.query(QueryBuilders.matchAllQuery()).sort("date", SortOrder.DESC).size(1));
		try {
			response = restClient.search(request, options);
			if (response.getHits() != null) {
				SearchHit[] hits = response.getHits().getHits();
				if (hits.length == 1) {
					Map<String, Object> map = hits[0].getSourceAsMap();
					latestData = 
							(new ObjectMapper()).convertValue(map, HistoryData.class);
					latestData.setId(hits[0].getId());
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return latestData;
	}

	@Override
	public <T extends BaseData> boolean upsertData(String indexName, T data) {
		Map<String, Object> histDataMap = (Map<String, Object>) 
				(new ObjectMapper()).convertValue(data, new TypeReference<Map<String, Object>>() {});
		String id = data.getId();
		if (id == null || id.isEmpty()) {
			IndexRequest request = new IndexRequest();
			histDataMap.remove("id");
			request.index(indexName).source(histDataMap);
			request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
			try {
				IndexResponse response = restClient.index(request, RequestOptions.DEFAULT);
				Result result = response.getResult();
				if ( result == Result.CREATED) {
					data.setId(response.getId());
					return true;
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			
		}else {
			UpdateRequest request = new UpdateRequest();
			request.index(indexName).doc(histDataMap);
			request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
			request.id(data.getId());
			histDataMap.remove("id");
			try {
				UpdateResponse response = restClient.update(request, RequestOptions.DEFAULT);
				Result result = response.getResult();
				if (result == Result.UPDATED || result == Result.NOOP) {
					return true;
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
			
		return false;
	}

	@Override
	public Map<String, Object> readSettingsMappings(String indexName) {
		Map<String, Object> jsonMap = null;
		try {
			JsonParser jsonParser = new BasicJsonParser();
			String path = ResourceUtils.getFile("classpath:" + indexName + ".json").getPath();
			jsonMap = jsonParser.parseMap(new String(Files.readAllBytes(Paths.get(path))));			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return jsonMap;
	}

	@Override
	public long deleteDoc(String indexName, String symbol) {
		GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
		boolean exists;
		try {
			exists = indexClient.exists(getIndexRequest, RequestOptions.DEFAULT);
			if (exists) {
				DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName); 
				deleteByQueryRequest.setConflicts("proceed");
				deleteByQueryRequest.setQuery(new TermQueryBuilder("symbol", symbol));
				deleteByQueryRequest.setRefresh(true);
				BulkByScrollResponse bulkResponse =
						restClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
				long deletedDocs = bulkResponse.getDeleted();
				return deletedDocs;
			} else {
				logger.error(String.format("index (%s) not exists", indexName)); 
				return -1L;
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			return -2L;
		}
	}

	@Override
	public RegisterFund[] getRegisterFunds() {
		GetIndexRequest getIndexRequest = new GetIndexRequest(registerIndexName);
		boolean exists;
		RegisterFund [] registerFunds=null;
		SearchResponse response;
		SearchRequest request = new SearchRequest(registerIndexName);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		RequestOptions options = RequestOptions.DEFAULT;
		request.source(sourceBuilder.query(QueryBuilders.matchAllQuery()).sort("symbol", SortOrder.DESC));
		try {
			exists = indexClient.exists(getIndexRequest, RequestOptions.DEFAULT);
			if (!exists) {
				logger.error(String.format("index (%s) not exist", registerIndexName));
				return null;
			}
			
			response = restClient.search(request, options);
			if (response.getHits() != null) {
				SearchHit[] hits = response.getHits().getHits();
				if (hits.length > 0) {
					registerFunds = new RegisterFund [hits.length];
					int index=0;
					for (SearchHit hit : hits) {
						Map<String, Object> map = hit.getSourceAsMap();
						registerFunds[index] = 
								(new ObjectMapper()).convertValue(map, RegisterFund.class);
						registerFunds[index].setId(hit.getId());
						index++;
					}
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return registerFunds;
	}
	
	@Override
	public RegisterFund getRegisterFund(String symbol) {
		GetIndexRequest getIndexRequest = new GetIndexRequest(registerIndexName);
		boolean exists;
		RegisterFund registerFund=null;
		SearchResponse response;
		SearchRequest request = new SearchRequest(registerIndexName);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		RequestOptions options = RequestOptions.DEFAULT;
		request.source(sourceBuilder.query(QueryBuilders.termQuery("symbol", symbol)));
		try {
			exists = indexClient.exists(getIndexRequest, RequestOptions.DEFAULT);
			if (!exists) {
				logger.error(String.format("index (%s) not exist", registerIndexName));
				return null;
			}
			
			response = restClient.search(request, options);
			if (response.getHits() != null) {
				SearchHit[] hits = response.getHits().getHits();
				if (hits.length == 1) {
					for (SearchHit hit : hits) {
						Map<String, Object> map = hit.getSourceAsMap();
						registerFund = 
								(new ObjectMapper()).convertValue(map, RegisterFund.class);
						registerFund.setId(hit.getId());
					}
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return registerFund;
	}

}


