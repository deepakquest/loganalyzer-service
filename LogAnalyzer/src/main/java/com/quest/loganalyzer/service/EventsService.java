package com.quest.loganalyzer.service;

import java.util.List;

import org.json.simple.parser.ParseException;

import com.quest.loganalyzer.model.LogEntry;

public interface EventsService {

	List<LogEntry> queryES(String keyword,String loglevel,String fromdate,String todate,String component) throws ParseException ;
	String copyModalityConfFile(String snapFolderLoc , String modality , String project ) ;
}
