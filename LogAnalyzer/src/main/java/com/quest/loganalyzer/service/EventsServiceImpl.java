package com.quest.loganalyzer.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
				String logName = (String) jsonObject.get("type");
				String logLevel = (String) jsonObject.get("level");
				System.out.println("Date: " + (String) jsonObject.get("logdate"));
				String date = (String) jsonObject.get("logdate");
				String time = "";
				if (date == null) {
					date = "";
				} else {
					Matcher m = p.matcher(date);
					if (m.find()) {
						time = m.group(1);
					}
				}

				String message = (String) jsonObject.get("errormsg");
				String fullMessage = (String) jsonObject.get("message");
				LogEntry logentry = new LogEntry(logName, logLevel, date, time, message, fullMessage);
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
				TimeUnit.SECONDS.sleep(15); // REMOVE THIS IF ANYTHING WE GET TO FIND THE LOGSTASH RELOADIND SATUS
				return ServiceConstants.LOGSTASH_COPYCONFFILE_SUCCESS;
			} else {
				return ServiceConstants.LOGSTASH_NOTSTSARTED_ERROR;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return ServiceConstants.LOGSTASH_COPYCONFFILE_ERROR;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return ServiceConstants.LOGSTASH_COPYCONFFILE_ERROR;
		}
	}

	public boolean checkLogStashReloadStatus() {
		boolean status = false ;
		try {
			JSONParser parser = new JSONParser();
			SearchHit[] searchHits = ElasticSearchOperations.searchCall(null,null,null,null,null);
			for (SearchHit hit : searchHits) {
				JSONObject jsonObject = (JSONObject) parser.parse(hit.getSourceAsString());
				String indexName = (String) jsonObject.get("index");
				System.out.println("LOG statsh RELOAD status : "+indexName);
				if(indexName.equals("sample"))
					status = true ;
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return status ;
	}
}
