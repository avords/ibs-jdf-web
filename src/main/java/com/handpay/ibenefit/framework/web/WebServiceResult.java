package com.handpay.ibenefit.framework.web;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WebServiceResult implements Serializable {

	private static final long serialVersionUID = 2206209013810600982L;

	public enum Status {
		OK, PARAMETER_ERROR, DATA_NOT_FOUND, UNKNOW_ERROR, TOKEN_ERROR
	}

	private Status status = Status.OK;
	private String message = "";
	private Long timestamp;

	private String result;
	
	private Integer count;

	public WebServiceResult() {
		timestamp = System.currentTimeMillis();
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	
	public String toJson() {
		Gson gson = new GsonBuilder().create();
		String out = null;
		if (result != null) {
			out = "{\"status\":" + gson.toJson(status) + ",\"message\":" + gson.toJson(message) + ",\"timestamp\":" + timestamp
					+ ",\"result\":" + result + "}";
		} else {
			out = "{\"status\":" + gson.toJson(status) + ",\"message\":" + gson.toJson(message) + ",\"timestamp\":" + timestamp
					+ "}";
		}
		return out;
	}
}
