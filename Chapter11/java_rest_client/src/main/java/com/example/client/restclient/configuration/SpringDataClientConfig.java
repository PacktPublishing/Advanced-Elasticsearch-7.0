package com.example.client.restclient.configuration;

//TODO: Wait for the support of Elasticsearch 7.0
//import java.net.InetAddress;
//
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.TransportAddress;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

//@Configuration
//@EnableAutoConfiguration(exclude={ElasticsearchDataAutoConfiguration.class})
//@EnableElasticsearchRepositories(basePackages = "com.example.client.restclient.repository")
//public class SpringDataClientConfig {
//    @Value("${elasticsearch.host}")
//    private String host;
//
//    @Value("${elasticsearch.transport-client-port}")
//    private int port;
//
//
//    @Bean
//    public Client client() throws Exception {
//        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY);
//        client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
//        return client;
//    }
//
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
//        return new ElasticsearchTemplate(client());
//    }
//}
