package com.example.esanalytics.spark;

import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.example.esanalytics.common.HistoryData;

public interface EsSparkSQL {
	Map<String,Dataset<Row>> readValues(String indexName, String[] fieldNames);
	Map<String, Dataset<Row>> readValues(HistoryData[] historyData, String[] fieldNames);
}
