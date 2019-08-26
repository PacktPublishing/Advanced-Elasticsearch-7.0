package com.example.esanalytics.service;

import java.util.Map;

import com.example.esanalytics.common.RegisterFund;

public interface AnalyticService {
	public Map<String, Object> buildAnalyticsModel(String symbol, String[] fieldNames, String period, String token);
	public Map<String, Object> dailyUpdate(String symbol, String token, boolean force);
	public RegisterFund[] getRegisterFunds();
	public Map<String, Object> getRegisterSymbols();
	public Map<String, Object> dailyUpdateSymbol(String symbol, String token, boolean force);
}
