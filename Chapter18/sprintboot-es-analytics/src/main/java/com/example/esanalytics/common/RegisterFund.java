package com.example.esanalytics.common;

public class RegisterFund extends BaseData{
	String symbol;
	String startDate;
	String period;
	String token;
	String[] fieldNames;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String[] getFieldNames() {
		return fieldNames;
	}
	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}
	
	public static RegisterFund build(String symbol, String startDate, String period, String[] fieldNames, String token) {
		RegisterFund registerFund = new RegisterFund();
		registerFund.setSymbol(symbol);;
		registerFund.setPeriod(period);
		registerFund.setStartDate(startDate);
		registerFund.setFieldNames(fieldNames);
		registerFund.setToken(token);
		return registerFund;
	}
}
