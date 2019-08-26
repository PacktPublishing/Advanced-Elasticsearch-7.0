package com.example.client.restclient.service.impl;

import java.util.Map;


public interface SqlJdbcClientService {
	Map<String, Object> executeQuery(String sqlStatement);
}
