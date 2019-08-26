package com.example.client.restclient.configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.elasticsearch.xpack.sql.jdbc.EsDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqlJdbcClientConfig {
public static final Logger logger = LoggerFactory.getLogger(LowLevelRestClientConfig.class);
	@Value("${elasticsearch.host}")
	private String host;
	
	@Value("${elasticsearch.rest-client-port}")
	private int port;
	
	@Bean
	public Connection SqlJdbcClient() throws SQLException {
		EsDataSource dataSource = new EsDataSource();
		String url = "jdbc:es://" + host + ":" + port;
		dataSource.setUrl(url);
		Properties properties = new Properties();
		dataSource.setProperties(properties);
		return dataSource.getConnection();
	}
	
	@PreDestroy
	public void cleanup() {
		(new Cleanup()).cleanup();
	}
	
}
