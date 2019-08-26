package com.example.client.restclient.repository;
//TODO: Wait for the support of Elasticsearch 7.0

//import org.springframework.data.domain.Page;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//import com.example.client.restclient.model.Cf_etf;


//public interface Cf_etfRepository extends ElasticsearchRepository<Cf_etf, String>{
//	Page<Cf_etf> findBySymbol(String symbol);
//	
//	@Query("{\"query\":{\"match_phrase\":{\"fund_name\":{\"query\":\"?0\"}}}}")
//	Page<Cf_etf> matchFundNamePhraseQuery(String fund_name);
//	
//}
