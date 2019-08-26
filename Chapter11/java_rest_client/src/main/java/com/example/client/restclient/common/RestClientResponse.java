package com.example.client.restclient.common;

import org.apache.http.Header;

public class RestClientResponse {
	public Header[] getHeaders() {
		return headers;
	}
	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(String string) {
		this.responseBody = string;
	}
	public String getErrMEssage() {
		return errMessage;
	}
	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	Header[] headers;
	int statusCode;
	String responseBody;
	String errMessage;
}
