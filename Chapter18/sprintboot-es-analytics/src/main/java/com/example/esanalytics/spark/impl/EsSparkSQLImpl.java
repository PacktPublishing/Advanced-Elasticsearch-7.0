package com.example.esanalytics.spark.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.esanalytics.common.HistoryData;
import com.example.esanalytics.spark.EsSparkSQL;

@Component
public class EsSparkSQLImpl implements EsSparkSQL {
	public static final Logger logger = LoggerFactory.getLogger(EsSparkSQLImpl.class);
	
	@Autowired
	private SparkSession sparkSession;

	@Override
	public Map<String,Dataset<Row>> readValues(String indexName, String[] fieldNames) {
		String statement = String.format("select %s from view1", StringUtils.join(fieldNames, ","));
		Dataset<Row> dataSetAD = null;
		Dataset<Row> dataSet = null;
		try {
			dataSet = sparkSession.read().format("org.elasticsearch.spark.sql").load(indexName);
			dataSet.createOrReplaceTempView("view1");
			dataSetAD = sparkSession.sql(statement);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		Map<String, Dataset<Row>> dataSetMap = new HashMap<String, Dataset<Row>>();
		dataSetMap.put("dataSet", dataSet);
		dataSetMap.put("dataSetAD", dataSetAD);
		return dataSetMap;
	}

	@Override
	public Map<String, Dataset<Row>> readValues(HistoryData[] historyData, String[] fieldNames) {
		String statement = String.format("select %s from view1", StringUtils.join(fieldNames, ","));
		Dataset<Row> dataSetAD = null;
		Dataset<Row> dataSet = null;
		try {
			dataSet = sparkSession.createDataFrame(Arrays.asList(historyData), HistoryData.class);
			dataSet.createOrReplaceTempView("view1");
			dataSetAD = sparkSession.sql(statement);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		Map<String, Dataset<Row>> dataSetMap = new HashMap<String, Dataset<Row>>();
		dataSetMap.put("dataSet", dataSet);
		dataSetMap.put("dataSetAD", dataSetAD);
		return dataSetMap;
	}
}
