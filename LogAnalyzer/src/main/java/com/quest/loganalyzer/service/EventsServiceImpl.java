package com.quest.loganalyzer.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.quest.loganalyzer.model.LogEntry;
import com.quest.loganalyzer.util.CopySnapUtil;
import com.quest.loganalyzer.util.ElasticSearchOperations;
import com.quest.loganalyzer.util.ServiceConstants;


@Service("eventsService")
public class EventsServiceImpl implements EventsService {

@Value("${modalityConfFile.MR}")
private String modConfFileMR;

@Value("${modalityConfFile.CT}")
private String modConfFileCT;

@Value("${logStashConfFile}")
private String logStashConfFile;

	public List<LogEntry> queryES(String keyword,String loglevel,String fromDate,String toDate,String component) {
		JSONObject jsonObject = new JSONObject();
		List<LogEntry> logEntries = new ArrayList<LogEntry>();
		JSONParser parser = new JSONParser();
		SearchHit[] searchHits;
		Pattern p = Pattern.compile("^(?:\\w+\\s+){3}([^\\n\\r]+)$");
		try {
			searchHits = ElasticSearchOperations.searchCall(keyword,loglevel,fromDate,toDate,component);
			for (SearchHit hit : searchHits) {
				jsonObject = (JSONObject) parser.parse(hit.getSourceAsString());
				System.out.println("After search -----------" + hit.getSourceAsString());
				String componentName = (String) jsonObject.get("component");
				String logName = (String) jsonObject.get("type");
				String logLevel = (String) jsonObject.get("level");
				System.out.println("Date: " + (String) jsonObject.get("logdate"));
				String date = (String) jsonObject.get("logdate");
				String time = "";
				String exception = "";
				String exceptionStack = "";
				if (date == null) {
					date = "";
				} else {
					Matcher m = p.matcher(date);
					if (m.find()) {
						time = m.group(1);
					}
				}

				String message = (String) jsonObject.get("message");
				String fullMessage = (String) jsonObject.get("message");
				if(jsonObject.get("exception") !="" ) {
					exception = (String) jsonObject.get("exception");
				}
				if(jsonObject.get("excepStack") !="" ) {
					exceptionStack = (String) jsonObject.get("excepStack");
				}
				LogEntry logentry = new LogEntry(componentName,logName, logLevel, date, time, message, fullMessage,exception, exceptionStack);
				logEntries.add(logentry);
			} 
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return logEntries;
	}
	
	@Override
	public String copyModalityConfFile(String snapFolderLoc, String modality, String project) {
		Path srcDir = null;
		Path destDir = null;
		try {
			long countBeforeReload = CopySnapUtil.getLogstashReloadCount();
			if (CopySnapUtil.checkLogStashServerStatus()) {
				CopySnapUtil.setSnapLocEnvVar(snapFolderLoc);
				if (modality.equalsIgnoreCase("MR")) {
					srcDir = Paths.get(modConfFileMR);
					destDir = Paths.get(logStashConfFile);
					Files.copy(srcDir, destDir, StandardCopyOption.REPLACE_EXISTING);
				} else if (modality.equalsIgnoreCase("CT")) {
					srcDir = Paths.get(modConfFileCT);
					destDir = Paths.get(logStashConfFile);
					Files.copy(srcDir, destDir, StandardCopyOption.REPLACE_EXISTING);
				}
				//TimeUnit.SECONDS.sleep(15); // REMOVE THIS IF ANYTHING WE GET TO FIND THE LOGSTASH RELOADIND SATUS
				long countFlag = countBeforeReload+1;
				while(countFlag > countBeforeReload) {
					long countAfterReload =  CopySnapUtil.getLogstashReloadCount();
					System.out.println("------------------REALOAD Success count------------- "+countAfterReload);
					if(countAfterReload > countBeforeReload)
						break ;
					countFlag++;
				}
				return ServiceConstants.LOGSTASH_COPYCONFFILE_SUCCESS;
			} else {
				return ServiceConstants.LOGSTASH_NOTSTARTED_ERROR;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return ServiceConstants.LOGSTASH_COPYCONFFILE_ERROR;
		} 
	}
}
