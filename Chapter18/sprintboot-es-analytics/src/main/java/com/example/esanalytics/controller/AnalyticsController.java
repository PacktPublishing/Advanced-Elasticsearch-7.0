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

import com.example.esanalytics.service.AnalyticService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
	public static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
	
	@Autowired
	private AnalyticService service;
	
	@ApiOperation("Build Bollinger band model by symbol, fieldNames and period")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="symbol", type="string", required=true, defaultValue="rfem"),
		@ApiImplicitParam(paramType = "query", name="fieldNames", type="array", required=true, allowMultiple = true),
		@ApiImplicitParam(paramType = "query", name="period", type="string", required=true, defaultValue="6m"),
		@ApiImplicitParam(paramType = "query", name="token", type="string", required=true),
	})
	@RequestMapping(value="/build-analytics-model", method=RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> buildAnalyticsModel(
			@RequestParam(value = "symbol", required=true) String symbol,
			@RequestParam(value = "fieldNames") String[] fieldNames,
			@RequestParam(value = "period", required=true) String period,
			@RequestParam(value = "token", required=true) String token) throws Exception {
		Map<String,Object> response = (Map<String, Object>) service.buildAnalyticsModel(symbol, fieldNames, period, token);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@ApiOperation("Daily update symbol")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="symbol", type="string", required=true, defaultValue="rfem"),
		@ApiImplicitParam(paramType = "query", name="token", type="string", required=true),
		@ApiImplicitParam(paramType = "query", name="force", type="boolean", required=false, defaultValue="false")
	})
	@RequestMapping(value="/daily-update-symbol", method=RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> dailyUpdateSymbol(
			@RequestParam(value = "symbol", required=true) String symbol,
			@RequestParam(value = "token", required=true) String token,
			@RequestParam(value = "force", required=false)boolean force) throws Exception {
		Map<String,Object> response = (Map<String, Object>) service.dailyUpdateSymbol(symbol, token, force);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@ApiOperation("Get Registered Symbols")
	@ApiImplicitParams({
	})
	@RequestMapping(value="/get-register-symbols", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getRegisterSymbols() throws Exception {
		Map<String,Object> response = (Map<String, Object>) service.getRegisterSymbols();
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
