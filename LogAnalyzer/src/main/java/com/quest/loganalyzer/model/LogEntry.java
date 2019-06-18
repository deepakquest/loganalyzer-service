package com.quest.loganalyzer.model;


/*LogEntry is the POJO class having fields component,
 *logName,logLevel,date etc which holds the values from the search response
 */
public class LogEntry {

	private String component;
	private String logName;
	private String logLevel;
	private String date;
	private String time;
	private String message;
	private String fullMessage;
	private String exception;
	private String exceptionStack;
	
	public LogEntry(String component,String logName, String logLevel, String date, String time, String message, String fullMessage,String exception,String exceptionStack) {
		super();
		this.component = component;
		this.logName = logName;
		this.logLevel = logLevel;
		this.date = date;
		this.time = time;
		this.message = message;
		this.fullMessage = fullMessage;
		this.exception =  exception;
		this.exceptionStack = exceptionStack;
	}
	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getExceptionStack() {
		return exceptionStack;
	}

	public void setExceptionStack(String exceptionStack) {
		this.exceptionStack = exceptionStack;
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

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}



	@Override
	public String toString() {
		return String.format("LogEntry [component=%s, logName=%s, logLevel=%s, date=%s, time=%s, message=%s, fullMessage=%s, exception=%s, exceptionStack=%s]", component,logName,
				logLevel, date, time, message,exception,exceptionStack,fullMessage);
	}

}