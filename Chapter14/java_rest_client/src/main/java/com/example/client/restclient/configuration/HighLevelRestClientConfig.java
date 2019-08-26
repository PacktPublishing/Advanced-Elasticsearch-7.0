package com.example.client.restclient.configuration;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HighLevelRestClientConfig {
	public static final Logger logger = LoggerFactory.getLogger(HighLevelRestClientConfig.class);
	
	@Bean
	public RestHighLevelClient HighLevelRestClient() {
		return new RestHighLevelClient(
				RestClient.builder(new HttpHost("localhost", 9200, "http"))
					.setDefaultHeaders(new Header[] {
							new BasicHeader("accept","application/json"), 
							new BasicHeader("content-type","application/json")})
					.setFailureListener(new RestClient.FailureListener() {
						public void onFailure(Node node) {
							logger.error("High level Rest Client Failure on node " + node.getName());
						}
					}));
	}
}