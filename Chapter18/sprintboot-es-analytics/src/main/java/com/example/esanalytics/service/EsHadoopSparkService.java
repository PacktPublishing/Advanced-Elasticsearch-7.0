package com.example.esanalytics.service;

import java.util.Map;

import com.example.esanalytics.common.HistoryData;


public interface EsHadoopSparkService {
	Map<String, Object> buildAnomalyDetectionModel(String indexName, String[] fieldName, int numOfClass);
	Map<String, Object> buildAnomalyDetectionModel(HistoryData[] historyData, String[] fieldNames, int numOfClass);
	Map<String, Object> anomalyDetection(HistoryData[] historyData, String[] fieldNames);
}
