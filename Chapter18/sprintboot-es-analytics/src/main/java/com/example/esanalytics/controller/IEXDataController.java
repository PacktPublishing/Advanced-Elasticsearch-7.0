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

import com.example.esanalytics.service.IEXDataService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/iexdata")
public class IEXDataController {
	public static final Logger logger = LoggerFactory.getLogger(IEXDataController.class);
	
	@Autowired
	IEXDataService service;
	
	@ApiOperation("Get IEX data by symbol and period")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="symbol", type="string", required=true, defaultValue="rfem"),
		@ApiImplicitParam(paramType = "query", name="period", type="string", required=true, defaultValue="1y"),
		@ApiImplicitParam(paramType = "query", name="token", type="string", required=true),
	})
	@RequestMapping(value="/get-base-data", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> geSymbolHistPrice(
			@RequestParam(value = "symbol", required=true) String symbol,
			@RequestParam(value = "period", required=true) String period,
			@RequestParam(value = "token", required=true) String token) throws Exception {
		Map<String,Object> response = service.getBaseData(token, symbol, period);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@ApiOperation("Get IEX daily data by symbol")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="symbol", type="string", required=true, defaultValue="rfem"),
		@ApiImplicitParam(paramType = "query", name="token", type="string", required=true),
	})
	@RequestMapping(value="/get-daily-data", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getSymbolDailySPrice(
			@RequestParam(value = "symbol", required=true) String symbol,
			@RequestParam(value = "token", required=true) String token) throws Exception {
		Map<String,Object> response = service.getDailyData(token, symbol);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
}
