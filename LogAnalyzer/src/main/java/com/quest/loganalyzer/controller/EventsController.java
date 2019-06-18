package com.quest.loganalyzer.controller;

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
/*EventsController is the controller class responsible for creating services 
 *for functionalities like search keyword, upload folder etc
 */
public class EventsController {

	public static final Logger logger = LoggerFactory.getLogger(EventsController.class);

	@Autowired
	EventsService eventsService; // Service which will do all data retrieval/manipulation work

	/*searchES method is the service end point for basie search
	 * @param String keyword
	 * @param String loglevel
	 * @param String fromdate
	 * @param String todate
	 * @param String component
	 */
	@RequestMapping(value = "/events", method = RequestMethod.GET, produces = { "application/json" })
	public ResponseEntity<List<LogEntry>> searchES( @RequestParam(value = "keyword", required=false) String keyword,
													@RequestParam(value = "loglevel", required=false) String logLevel,
													@RequestParam(value = "fromdate", required=false) String fromDate,
													@RequestParam(value = "todate", required=false) String toDate,
													@RequestParam(value = "component", required=false) String component) {

		List<LogEntry> entries = null;
		try {
			entries = eventsService.queryES(keyword, logLevel, fromDate, toDate, component);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<List<LogEntry>>(entries, HttpStatus.OK);
	}
	/* uploadLogs method is the service end point for basie search
	 * @param String snapFolderLoc
	 * @param String modality
	 * @param String project
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<String> uploadLogs(@RequestParam("filename") String snapFolderLoc,
											 @RequestParam("modality") String modality,
											 @RequestParam("project") String project) {
		String copyResponse = null ;
		try {
			copyResponse = eventsService.copyModalityConfFile(snapFolderLoc, modality, project) ;	
		}
		 catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<String>(copyResponse,HttpStatus.CREATED);
	}

}