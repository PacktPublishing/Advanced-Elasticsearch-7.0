package com.example.client.restclient.controller;

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
import com.example.client.restclient.service.impl.SqlJdbcClientService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/api/sqljdbcclient")
public class SqlJdbcClientController {
	public static final Logger logger = LoggerFactory.getLogger(SqlJdbcClientController.class);
	
	@Autowired
	private SqlJdbcClientService sqlJdbcClient;
	
	@ApiOperation("SQL JDBC Client Request")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", name="sqlStatement", type="String", required=true, defaultValue=""),
	})
	@RequestMapping(value="query", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> executeQuery(
			@RequestParam(value = "sqlStatement") String sqlStatement) throws Exception {
				Map<String,Object> response = sqlJdbcClient.executeQuery(sqlStatement);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
}
