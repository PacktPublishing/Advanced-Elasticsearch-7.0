package com.example.esanalytics.service;

import java.util.Map;

import com.example.esanalytics.common.BaseData;
import com.example.esanalytics.common.HistoryData;
import com.example.esanalytics.common.RegisterFund;

public interface EsDataService {
	Map<String, Object> getBollingerBand(String symbol, String startDate, String endDate);
	Map<String, Object> getEarliestData(String symbol);
	boolean bulkIndexHistoryData(HistoryData[] historyData);
	boolean deleteIndex(String indexName);
	boolean createIndex(String indexName, Map<String, Object> mappings);
	HistoryData getLatestHistoryData(String symbol);
	Map<String, Object> getLatestData(String symbol);
	Map<String, Object> readSettingsMappings(String indexName);
	long deleteDoc(String dataIndexName, String symbol);
	<T extends BaseData> boolean upsertData(String indexName, T data);
	RegisterFund[] getRegisterFunds();
	RegisterFund getRegisterFund(String symbol);
}
