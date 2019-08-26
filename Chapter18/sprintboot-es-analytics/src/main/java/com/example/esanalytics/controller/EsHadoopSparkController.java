package com.example.esanalytics.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.esanalytics.service.EsHadoopSparkService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/api/eshadoop/spark")
public class EsHadoopSparkController {
	public static final Logger logger = LoggerFactory.getLogger(EsHadoopSparkController.class);
	
	@Autowired
	private EsHadoopSparkService service;
	
	@ApiOperation("ES-Hadoop Spark K-means anomaly detection")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="indexName", type="string", required=true),
		@ApiImplicitParam(paramType = "query", name="fieldNames", type="array", required=true, allowMultiple = true),
		@ApiImplicitParam(paramType = "query", name="numOfClass", type="int", required=true)
	})
	@RequestMapping(value="/anomaly_detection", method=RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> buildAnomalyDectionModel(
			@RequestParam(value = "indexName") String indexName,
			@RequestParam(value = "fieldNames") String[] fieldNames,
			@RequestParam(value = "numOfClass") int numOfClass) throws Exception {
		Map<String,Object> response = service.buildAnomalyDetectionModel(indexName, fieldNames, numOfClass);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
}
