package com.example.esanalytics.common;

public class HistoryData extends BaseData {

	String symbol;
	String date;
    float open;
    float close;
    float high;
    float low;
    float volume;
    float change;
    float changePercent;
    String label;
    float changeOverTime;
    float hlc;
	float tDMA;
    float tDStDev;
	float bbu;
	float bbl;
	int prediction;

	public void setDate(String date) {
		this.date = date;
	}

	public void setOpen(float open) {
		this.open = open;
	}

	public void setClose(float close) {
		this.close = close;
	}

	public void setHigh(float high) {
		this.high = high;
	}

	public void setLow(float low) {
		this.low = low;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public void setChange(float change) {
		this.change = change;
	}

	public void setChangePercent(float changePercent) {
		this.changePercent = changePercent;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setChangeOverTime(float changeOverTime) {
		this.changeOverTime = changeOverTime;
	}

	public void setHlc(float hlc) {
		this.hlc = hlc;
	}


	public void setPrediction(int prediction) {
		this.prediction = prediction;
	}
	
	public String getDate() {
		return date;
	}

	public float getOpen() {
		return open;
	}

	public float getClose() {
		return close;
	}

	public float getHigh() {
		return high;
	}

	public float getLow() {
		return low;
	}

	public float getVolume() {
		return volume;
	}

	public float getChange() {
		return change;
	}

	public float getChangePercent() {
		return changePercent;
	}

	public String getLabel() {
		return label;
	}

	public float getChangeOverTime() {
		return changeOverTime;
	}

	public float getHlc() {
		return hlc;
	}

	public float getPrediction() {
		return prediction;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
    public float gettDMA() {
		return tDMA;
	}

	public void settDMA(float tDMA) {
		this.tDMA = tDMA;
	}

	public float gettDStDev() {
		return tDStDev;
	}

	public void settDStDev(float tDStDev) {
		this.tDStDev = tDStDev;
	}
	
    public float getBbu() {
		return bbu;
	}

	public void setBbu(float bbu) {
		this.bbu = bbu;
	}

	public float getBbl() {
		return bbl;
	}

	public void setBbl(float bbl) {
		this.bbl = bbl;
	}

}
