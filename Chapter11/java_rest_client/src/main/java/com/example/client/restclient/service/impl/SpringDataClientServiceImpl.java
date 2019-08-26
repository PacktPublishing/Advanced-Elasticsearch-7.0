package com.example.client.restclient.service.impl;
//TODO: Wait for the support of Elasticsearch 7.0
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Service;
//
//import com.example.client.restclient.model.Cf_etf;
//import com.example.client.restclient.repository.Cf_etfRepository;
//
//@Service
//public class SpringDataClientServiceImpl implements SpringDataClientService {
//	@Autowired
//	private Cf_etfRepository repository;
//	
//	@Override
//	public Map<String, Object> match_phrase_query(String indexName, String analyzer, String fieldName,
//			String fieldValue, int from, int pageSize) {
//		
//		return null;
//	}
//
//	@Override
//	public Map<String, Object> findSymbol(String indexName, String symbol) {
//		Map<String, Object> map = new HashMap<String,Object>();
//		Page<Cf_etf> etfs = repository.findBySymbol(symbol);
//		if (!etfs.isEmpty() && etfs.isLast() ) {
//			map.put("Content", etfs.getContent());
//			map.put("size", etfs.getSize());
//			map.put("total", etfs.getTotalElements());
//			map.put("totalPage", etfs.getTotalPages());
//		}
//		
//		return map;
//	}
//
//}
