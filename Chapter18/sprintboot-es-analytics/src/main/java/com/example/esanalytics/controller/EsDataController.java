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

import com.example.esanalytics.service.EsDataService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/esdata")
public class EsDataController {
	public static final Logger logger = LoggerFactory.getLogger(EsDataController.class);
	
	@Autowired
	EsDataService service;
	
	@ApiOperation("Get Bollinger band data by symbol, startdate and enddate")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="symbol", type="string", required=true, defaultValue="rfem"),
		@ApiImplicitParam(paramType = "query", name="startDate", type="string", required=true, defaultValue="2018-12-01"),
		@ApiImplicitParam(paramType = "query", name="endDate", type="string", required=true, defaultValue="2019-06-15"),
		
	})
	@RequestMapping(value="/get-bollinger-band", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getBollingerBand(
			@RequestParam(value = "symbol", required=true) String symbol,
			@RequestParam(value = "startDate", required=true) String startDate,
			@RequestParam(value = "endDate", required=true) String endDate) throws Exception {
		Map<String,Object> response = service.getBollingerBand(symbol, startDate, endDate);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@ApiOperation("Get earliest history data by symbol")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="symbol", type="string", required=true, defaultValue="rfem"),
	})
	@RequestMapping(value="/get-earliest-data", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getEarliestData(
			@RequestParam(value = "symbol", required=true) String symbol) throws Exception {
		Map<String,Object> response = service.getEarliestData(symbol);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@ApiOperation("Get latest history data by symbol")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="symbol", type="string", required=true, defaultValue="rfem"),
	})
	@RequestMapping(value="/get-latest-data", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getLatestData(
			@RequestParam(value = "symbol", required=true) String symbol) throws Exception {
		Map<String,Object> response = service.getLatestData(symbol);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}


