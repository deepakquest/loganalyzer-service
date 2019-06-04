package com.quest.loganalyzer.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quest.loganalyzer.model.LogEntry;
import com.quest.loganalyzer.service.EventsService;

@RestController
@RequestMapping("/api")
public class EventsController {

	public static final Logger logger = LoggerFactory.getLogger(EventsController.class);

	@Autowired
	EventsService eventsService; // Service which will do all data retrieval/manipulation work

	// ------------------- Elastic Search Call
	@RequestMapping(value = "/events", method = RequestMethod.GET, produces = { "application/json" })
	public ResponseEntity<List<LogEntry>> searchES( @RequestParam(value = "keyword", required=false) String keyword,
													@RequestParam(value = "loglevel", required=false) String logLevel,
													@RequestParam(value = "fromdate", required=false) String fromDate,
													@RequestParam(value = "todate", required=false) String toDate,
													@RequestParam(value = "component", required=false) String component) {

		List<LogEntry> entries = null;
		try {
			entries = eventsService.queryES(keyword, logLevel, fromDate, toDate, component);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<List<LogEntry>>(entries, HttpStatus.OK);
	}
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<String> uploadMultipleFiles(@RequestParam("filename") String snapFolderLoc,
													 @RequestParam("modality") String modality,
													 @RequestParam("project") String project) {
		String copyResponse = null ;
		System.out.println("Upload success");
		try {
			copyResponse = eventsService.copyModalityConfFile(snapFolderLoc, modality, project) ;	
		}
		 catch (Exception e) {
				e.printStackTrace();
		}
		return new ResponseEntity<String>(copyResponse,HttpStatus.CREATED);
	}

}