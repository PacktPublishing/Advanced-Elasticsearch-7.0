package com.example.esanalytics.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.spark.sql.functions;
import com.example.esanalytics.common.HistoryData;
import com.example.esanalytics.service.EsHadoopSparkService;
import com.example.esanalytics.spark.impl.EsSparkSQLImpl;
import com.example.esanalytics.spark.mllib.AnomalyDetection;


@Service
public class EsHadoopSparkServiceImpl implements EsHadoopSparkService {
	@Autowired
	private EsSparkSQLImpl esSparkIO;
	
	@Autowired
	private AnomalyDetection anomalyDetection;
	
	@Value("${elasticsearch.data_index}")
	private String indexName;

	public static final Logger logger = LoggerFactory.getLogger(EsHadoopSparkServiceImpl.class);

	@Override
	public Map<String, Object> buildAnomalyDetectionModel(String indexName, String[] fieldNames, int numOfClass) {
		Map<String, Object> returns= new HashMap<String, Object>();
		
		Map<String, Dataset<Row>> dataSetMap = esSparkIO.readValues(indexName, fieldNames);
		if (dataSetMap.isEmpty())
			return null;
		Dataset<Row> dataSet = dataSetMap.get("dataSet");
		Dataset<Row> dataSetAD = dataSetMap.get("dataSetAD");
  		Dataset<Row> prediction = anomalyDetection.buildKmeansModel(dataSetAD, fieldNames, numOfClass);

		if (prediction != null) {
			String predictionString = prediction.collectAsList().toString();
			returns.put("prediction", predictionString);
			Dataset<Row> data_id = dataSet.selectExpr("_metadata._id as id");
			Dataset<Row> data_row_index = data_id.withColumn("row_index", functions.monotonically_increasing_id());
			Dataset<Row> prediction_row_index = prediction.withColumn("row_index", functions.monotonically_increasing_id());
			Dataset<Row> update = data_row_index.join(prediction_row_index, "row_index").drop("row_index");
			update.write().format("org.elasticsearch.spark.sql").option("es.mapping.id", "id").option("es.mapping.exclude", "id")
			.option("es.write.operation", "update").mode("append").save(indexName);
			
		} else { 
			returns.put("status", 500);
		}
	
		return returns;
	}
	
	@Override
	public Map<String, Object> buildAnomalyDetectionModel(HistoryData[] historyData, String[] fieldNames, int numOfClass) {
		Map<String, Object> returns;
		
		Map<String, Dataset<Row>> dataSetMap = esSparkIO.readValues(historyData, fieldNames);
		if (dataSetMap.isEmpty())
			return null;
		Dataset<Row> dataSet = dataSetMap.get("dataSet");
		Dataset<Row> dataSetAD = dataSetMap.get("dataSetAD");
  		Dataset<Row> prediction = anomalyDetection.buildKmeansModel(dataSetAD, fieldNames, numOfClass);

		if (prediction != null) {
			returns = updateES(prediction, dataSet, historyData);
		} else { 
			returns=new HashMap<String, Object>();
			returns.put("status", 500);
		}
	
		return returns;
	}

	@Override
	public Map<String, Object> anomalyDetection(HistoryData[] historyData, String[] fieldNames) {
		Map<String, Object> returns=new HashMap<String, Object>();
		Map<String, Dataset<Row>> dataSetMap = esSparkIO.readValues(historyData, fieldNames);
		Dataset<Row> dataSet = dataSetMap.get("dataSet");
		Dataset<Row> dataSetAD = dataSetMap.get("dataSetAD");
  		Dataset<Row> prediction = anomalyDetection.predict(dataSetAD, fieldNames);
		if (prediction != null) {
			returns = updateES(prediction, dataSet, historyData);
		} else { 
			returns=new HashMap<String, Object>();
			returns.put("status", 500);
		}
		return returns;
	}
	
	
	public Map<String, Object> updateES(Dataset<Row> prediction, Dataset<Row> dataSet, HistoryData[] historyData) {
		Map<String, Object> returns=new HashMap<String, Object>();
		String predictionString = prediction.collectAsList().toString();
		Dataset<Row> data_id = dataSet.selectExpr("id", "hlc", "tDStDev", "tDMA", "bbl", "bbu");
		Dataset<Row> data_row_index = data_id.withColumn("row_index", functions.monotonically_increasing_id());
		Dataset<Row> prediction_row_index = prediction.withColumn("row_index", functions.monotonically_increasing_id());
		Dataset<Row> update = data_row_index.join(prediction_row_index, "row_index").drop("row_index");
		update.write().format("org.elasticsearch.spark.sql").option("es.mapping.id", "id").option("es.mapping.exclude", "id")
		.option("es.write.operation", "update").mode("append").save(indexName);
		String statistics = dataSet.selectExpr("date", "hlc", "tdStDev", "tDMA", "bbl", "bbu").showString(historyData.length, 0, false);
		returns.put("statistics", statistics);
		returns.put("prediction", predictionString);
		return returns;
	}

}
