package com.example.esanalytics.configuration;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SparkSession.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkSessionConfig {

	@Value("${spark.es.nodes}")
	private String nodes;
	
	@Value("${spark.es.port}")
	private String port;
	
	@Value("${spark.es.resource}")
	private String resource;
	
	@Value("${spark.es.index_auto_create}")
	private String indexAutoCreate;
	
	@Value("${spark.es.scroll.size}")
	private String size;
	
	public static final Logger logger = LoggerFactory.getLogger(SparkSessionConfig.class);
	
	
	@Bean
	public SparkSession SparkSessionClient() {
		logger.info("SpringBootSpark To Elasticsearch Application: {}, {}, {}, {}", nodes, port, resource, indexAutoCreate);

        SparkConf sparkConf = new SparkConf().setAppName("RealtimeAnalytics").setMaster("local")
        		.set("es.index.auto.create", indexAutoCreate)
                .set("es.nodes", nodes).set("es.port", port)
                .set("es.scroll.size", "1000")
                .set("es.http.timeout", "10m")
                .set("es.read.metadata", "true")
                .set("es.scroll.limit", "-1");
        
        SparkSession sparkSession = new Builder().config(sparkConf).getOrCreate();
        return sparkSession;
	}
	
	
}
