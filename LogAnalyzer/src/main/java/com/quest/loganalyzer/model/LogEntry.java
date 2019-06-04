package com.quest.loganalyzer.model;

import java.util.List;

//import com.google.gson.annotations.SerializedName;

public class LogEntry {

	private String logName;
	private String logLevel;
	private String date;
	private String time;
	private String message;

	public LogEntry(String logName, String logLevel, String date, String time, String message, String fullMessage) {
		super();
		this.logName = logName;
		this.logLevel = logLevel;
		this.date = date;
		this.time = time;
		this.message = message;
		this.fullMessage = fullMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}

	private String fullMessage;

	@Override
	public String toString() {
		return String.format("LogEntry [logName=%s, logLevel=%s, date=%s, time=%s, message=%s, fullMessage=%s]", logName,
				logLevel, date, time, message, fullMessage);
	}

}