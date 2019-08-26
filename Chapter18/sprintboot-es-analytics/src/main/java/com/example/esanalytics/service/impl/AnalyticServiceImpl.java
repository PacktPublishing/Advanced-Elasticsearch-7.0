package com.example.esanalytics.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.metrics.ParsedScriptedMetric;
import org.elasticsearch.search.aggregations.pipeline.ParsedSimpleValue;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import com.example.esanalytics.common.HistoryData;
import com.example.esanalytics.common.RegisterFund;
import com.example.esanalytics.service.AnalyticService;
import com.example.esanalytics.service.EsDataService;
import com.example.esanalytics.service.EsHadoopSparkService;
import com.example.esanalytics.service.IEXDataService;
import org.springframework.http.HttpStatus;

@Service
public class AnalyticServiceImpl implements AnalyticService {
	public static final Logger logger = LoggerFactory.getLogger(AnalyticServiceImpl.class);
	
	@Autowired
	private EsDataService esDataService;
	
	@Autowired
	private IEXDataService iexDataService;
	
	@Autowired
	private EsHadoopSparkService esHadoopSparkService;
	
	@Value("${elasticsearch.data_index}")
	private String dataIndexName;
	
	@Value("${elasticsearch.register_funds_index}")
	private String registerIndexName;
	
	
	@Override
	public Map<String, Object> buildAnalyticsModel(String symbol, String[] fieldNames, String period, String token) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		Map<String, Object> baseData = iexDataService.getBaseData(token, symbol, period);
		
		HistoryData[] historyData = (HistoryData[]) baseData.get("historyData");
		String startDate = historyData[0].getDate();
		String endDate = historyData[historyData.length-1].getDate();			
		RegisterFund registerFund = RegisterFund.build(symbol, startDate, period, fieldNames, token);
		
		response = prepareIndex(symbol, historyData, registerFund);
		HttpStatus status = (HttpStatus) response.get("status");
		if (status != HttpStatus.OK) {
			return response;
		}
		
		getBollingerBand(historyData, symbol, startDate, endDate);
		
		response = esHadoopSparkService.buildAnomalyDetectionModel(historyData, fieldNames, 2);
		
		return response;
	}

	@Override
	public Map<String, Object> dailyUpdate(String symbol, String token, boolean force) {
		Map<String, Object> response = new HashMap<String, Object>();
		
		Map<String, Object> latestIEXData = iexDataService.getDailyData(token, symbol);
		HistoryData latestData = (HistoryData) latestIEXData.get("latestData");
		
		HistoryData latestHistoryData = esDataService.getLatestHistoryData(symbol);
		if (latestHistoryData.getDate().compareTo(latestData.getDate()) == 0)
			latestData.setId(latestHistoryData.getId());

		if (force || latestHistoryData.getDate().compareTo(latestData.getDate()) < 0) {
			boolean success = esDataService.upsertData(dataIndexName, latestData);
			if (!success) {
				response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
				response.put("message", "upsertData failure");
				return response;
			}
			
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
			DateTime latestDataDatetime = formatter.parseDateTime(latestData.getDate());
			String startDate = latestDataDatetime.minusMonths(1).toString(formatter);
			getLatestBollingerBand(latestData, symbol, startDate, latestData.getDate());
			
			RegisterFund registerFund = esDataService.getRegisterFund(symbol);
			HistoryData[] historyData = new HistoryData[1];
			historyData[0] = latestData;
			response = esHadoopSparkService.anomalyDetection(historyData, registerFund.getFieldNames());
		} else {
			response.put("status", HttpStatus.OK);
			response.put("message", "no need to update");
		}

		return response;
	}
	
	void getBollingerBand(HistoryData[] historyData, String symbol, String startDate, String endDate) {
		Map<String, Object> bollingerData = esDataService.getBollingerBand(symbol, startDate, endDate);
		
		Aggregations aggs = (Aggregations) bollingerData.get("aggs");
		
		int index = 0;
		for (Aggregation level0Agg : aggs) {
			if (level0Agg.getType().equals(DateHistogramAggregationBuilder.NAME)) {
				ParsedDateHistogram dateHistogramAgg = aggs.get(level0Agg.getName());
				for (Bucket bucket : dateHistogramAgg.getBuckets()) {
					parseBBBucketData(bucket, historyData[index]);
					index++;
				}
			}
		}
	}
	
	void getLatestBollingerBand(HistoryData latestData, String symbol, String startDate, String endDate) {
		Map<String, Object> bollingerData = esDataService.getBollingerBand(symbol, startDate, endDate);
		Aggregations aggs = (Aggregations) bollingerData.get("aggs");
		for (Aggregation level0Agg : aggs) {
			if (level0Agg.getType().equals(DateHistogramAggregationBuilder.NAME)) {
				ParsedDateHistogram dateHistogramAgg = aggs.get(level0Agg.getName());
				List<? extends Bucket> buckets = dateHistogramAgg.getBuckets();
				Bucket bucket = buckets.get(buckets.size()-1);
				parseBBBucketData(bucket, latestData);
			}
		}
	}
	

	private Map<String, Object> prepareIndex(String symbol, HistoryData[] historyData, RegisterFund registerFund) {
		Map<String, Object> response = new HashMap<String, Object>();
		boolean success;
		
		Map<String, Object> settingsMappings = esDataService.readSettingsMappings(dataIndexName);
		long number = esDataService.deleteDoc(dataIndexName, symbol);
		if (number == -1L) {
			success = esDataService.createIndex(dataIndexName, settingsMappings);
			if (!success) {
				response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
				response.put("message", String.format("Index (%s) creation failure", dataIndexName));
				return response;
			}
		}
		
		success = esDataService.bulkIndexHistoryData(historyData);
		if (!success) {
			response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
			response.put("message", String.format("Bulk Indexing (%s) documents failure", dataIndexName));
			return response;
		}
		
		settingsMappings = esDataService.readSettingsMappings(registerIndexName);
		number = esDataService.deleteDoc(registerIndexName, symbol);
		if (number == -1L) {		
			success = esDataService.createIndex(registerIndexName, settingsMappings);
			if (!success) {
				response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
				response.put("messages", String.format("Index (%s) creation failure", registerIndexName));
				return response;
			}
		}
				
		success = esDataService.upsertData(registerIndexName, registerFund);
		if (success) {
			response.put("status", HttpStatus.OK);
		} else {
			response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
			response.put("message", String.format("Indexing (%s) document failure", registerIndexName));
		}
		return response;
	}
	
	private void parseBBBucketData(Bucket bucket, HistoryData data) {
		Aggregations bucketAggs = bucket.getAggregations();
		
		Aggregation tpAgg = bucketAggs.get("tp");
		ParsedScriptedMetric tp = bucketAggs.get(tpAgg.getName());
		Object scriptedResult = tp.aggregation();
		
		data.setHlc(Float.valueOf(scriptedResult.toString()));
		
		Aggregation tdMAAgg = bucketAggs.get("tdMA");
		ParsedSimpleValue tdMA = bucketAggs.get(tdMAAgg.getName());
		if (tdMA.getValueAsString().equals("NaN"))
			data.settDMA(0.0f);
		else
			data.settDMA(Float.valueOf(tdMA.getValueAsString()));
		
		Aggregation tdStdDevAgg = bucketAggs.get("tdStdDev");
		ParsedSimpleValue tdStdDev = bucketAggs.get(tdStdDevAgg.getName());
		if (tdStdDev.getValueAsString().equals("NaN"))
			data.settDStDev(0.0f);
		else
			data.settDStDev(Float.valueOf(tdStdDev.getValueAsString()));
		
		
		data.setBbu(data.gettDMA()+2*data.gettDStDev());
		data.setBbl(data.gettDMA()-2*data.gettDStDev());
	}

	@Override
	public RegisterFund[] getRegisterFunds() {
		RegisterFund[] registerFunds = esDataService.getRegisterFunds();
		return registerFunds;
	}

	@Override
	public Map<String, Object> getRegisterSymbols() {
		Map<String, Object> response = new HashMap<String, Object>();
		RegisterFund[] registerFunds = getRegisterFunds();
		response.put("status", HttpStatus.OK);
		response.put("symbols", registerFunds);
		return response;
	}

	@Override
	public Map<String, Object> dailyUpdateSymbol(String symbol, String token, boolean force) {
		Map<String, Object> response = new HashMap<String, Object>();

		RegisterFund[] funds = getRegisterFunds();
		boolean matched =false;
		if (funds != null && funds.length > 0) {
			for (RegisterFund fund : funds) {
				if (fund.getSymbol().equals(symbol)) {
					logger.info(String.format("Daily update fund (%s)", fund));
					response = dailyUpdate(fund.getSymbol(), fund.getToken(), true);
					response.put("status", HttpStatus.OK);
					matched = true;
				}
			}
		} 
		
		if (!matched) {
			logger.info("No fund registers daily update.");
			response.put("status", HttpStatus.NOT_FOUND);
			response.put("message", "No fund registers daily update.");
		}
		
		return response;
	}

}
