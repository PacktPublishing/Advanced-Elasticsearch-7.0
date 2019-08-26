package com.example.client.restclient.service.impl;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SqlJdbcClientServiceImpl implements SqlJdbcClientService {
	public static final Logger logger = LoggerFactory.getLogger(SqlJdbcClientServiceImpl.class);
	
	@Autowired
	Connection connection;

	@Override
	public Map<String, Object> executeQuery(String sqlStatement) {
		List<Map<String, Object>> hitList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		Statement statement;
		int colCount=0, total=0;
		
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sqlStatement);
			ResultSetMetaData rsmd = resultSet.getMetaData();
			colCount = rsmd.getColumnCount();
			while (resultSet.next()) {
				total++;
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i=1; i<=colCount; i++) {
					String columnName = rsmd.getColumnName(i);
					map.put(columnName, resultSet.getObject(columnName));
				}
				hitList.add(map);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			result.put("error", e.getMessage());
		}


		result.put("total", total);
		result.put("hits", hitList.toArray(new HashMap[hitList.size()]));
		return result;
	}

}
