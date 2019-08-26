package com.example.esanalytics.service;

import java.util.Map;

public interface IEXDataService {
	Map<String, Object> getBaseData(String token, String symbol, String period);
	Map<String, Object> getDailyData(String token, String symbol);
}
