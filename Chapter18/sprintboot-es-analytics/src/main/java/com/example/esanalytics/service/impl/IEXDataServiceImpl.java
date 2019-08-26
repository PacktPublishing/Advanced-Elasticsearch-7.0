package com.example.esanalytics.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.esanalytics.common.HistoryData;
import com.example.esanalytics.service.EsDataService;
import com.example.esanalytics.service.IEXDataService;

@Service
public class IEXDataServiceImpl implements IEXDataService {
	public static final Logger logger = LoggerFactory.getLogger(IEXDataServiceImpl.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${iex.uri.prefix}")
	private String uriPrefix;
	
	@Autowired
	EsDataService esDataService;
	
	@Override
	public Map<String, Object> getBaseData(String token, String symbol, String period) {
		String url = String.format("%s/%s/chart/%s?token=%s", uriPrefix, symbol, period, token);
		ResponseEntity<HistoryData[]> responseEntity = restTemplate.getForEntity(url, HistoryData[].class);
		HistoryData[] historyData = responseEntity.getBody();
		for (HistoryData item : historyData) {
			item.setSymbol(symbol);
		}
			
		HttpStatus statusCode = responseEntity.getStatusCode();
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("statusCode", statusCode);
		result.put("historyData", historyData);
		return result;
	}


	@Override
	public Map<String, Object> getDailyData(String token, String symbol) {
		String url = String.format("%s/%s/chart?token=%s", uriPrefix, symbol, token);
		ResponseEntity<HistoryData[]> responseEntity = restTemplate.getForEntity(url, HistoryData[].class);
		HistoryData[] historyData = responseEntity.getBody();
		HistoryData latestData = historyData[historyData.length-1];
		latestData.setSymbol(symbol);
		HttpStatus statusCode = responseEntity.getStatusCode();
		
		Map<String, Object> earliestDoc = esDataService.getEarliestData(symbol);
		HistoryData earliestData = (HistoryData) earliestDoc.get("doc");
		float close = earliestData.getClose();
		float curClose = latestData.getClose();
		latestData.setChangeOverTime((curClose - close)/close);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("statusCode", statusCode);
		result.put("latestData", latestData);
		return result;
	}

}
