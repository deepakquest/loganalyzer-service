package com.quest.loganalyzer.service;

import java.util.List;

import com.quest.loganalyzer.model.LogEntry;
/*EventsService is the interface having the key
 *methods for search and upload service
 */
public interface EventsService {

	List<LogEntry> queryES(String keyword,String loglevel,String fromdate,String todate,String component);
	String copyModalityConfFile(String snapFolderLoc , String modality , String project ) ;
}
