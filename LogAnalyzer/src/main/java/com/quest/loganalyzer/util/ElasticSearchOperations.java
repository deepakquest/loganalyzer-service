package com.quest.loganalyzer.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.simple.parser.ParseException;

public class ElasticSearchOperations {

	
	
	
	public static SearchHit[] searchCall(String keyword,String loglevel,String fromDate,String toDate,String component) throws ParseException {
		System.out.println("searchCall");
		RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
		RestHighLevelClient client = new RestHighLevelClient(builder);
		SearchRequest searchRequest = new SearchRequest("sample");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		QueryBuilder matchQueryBuilder = null;
		System.out.println("@@@@@@@@@@@@@ "+keyword +"@@@@@ "+loglevel);
		// Checks if keyword is null or empty
		if (keyword.equals(null) && loglevel.equals(null) &&  component.equals(null) &&  fromDate.equals(null) &&  toDate.equals(null))  {
			matchQueryBuilder = QueryBuilders.matchAllQuery();
			System.out.println("String:" + keyword);
		} else {
			String fromDateQuery =null ;
			String toDateQuery  = null;
			System.out.println("Else String:" + keyword);
			String queryString = get(keyword)+ " AND " +
								 get(loglevel)+" AND "+
								 get(component)  ;
			System.out.println("queryString :" + queryString);
			System.out.println("Datequery.FIRSTTT..........."+fromDate +  " ----- "+toDate);
			if(fromDate=="") {
					fromDate = "16200";
					System.out.println("queryString from date null :" + fromDate);
			}
			if (toDate=="") {
				long now = Instant.now().toEpochMilli();
				toDate = String.valueOf(now);
				System.out.println("queryString to date null :" + toDate);
			}
				
			fromDateQuery = fromDate ;
			toDateQuery= toDate ;
			
			System.out.println("Datequery............"+fromDateQuery +  " ----- "+toDateQuery);
			QueryBuilder rangeQuery = QueryBuilders
		                .rangeQuery("@timestamp")
		                .from(fromDateQuery) //"2019-01-01"
			            .to(toDateQuery) //"2019-02-07"
		                //.from("2019-01-01T04:16:50Z") //"2019-01-01"
		               // .to("2019-02-10T04:16:55Z") //"2019-02-07"
		                .includeLower(true)                                      
		                .includeUpper(true);
			QueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders
													  		   .queryStringQuery(queryString)
													  		   .defaultField("message"));
             if( rangeQuery !=null) {
            	 System.out.println("Added............");
             	matchQueryBuilder = QueryBuilders
 		                .boolQuery()
 		               .must(rangeQuery)
 		               .must(boolQuery);
             }
				
			
			System.out.println("Stringin:" + keyword);
		}
		searchSourceBuilder.query(matchQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(1000);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = null;

		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("sr0-----" + searchResponse.toString());
		SearchHits hits = searchResponse.getHits();
		System.out.println("sr1 +++++++++++++++" + hits.toString());
		SearchHit[] searchHits = hits.getHits();
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchHits;
	}
	
	
	public static String[] convertEpochTimetoDateTime(String epochTime) {
		Long longEpochTime = Long.parseLong(epochTime);
		Date date = new Date(longEpochTime);
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
		String formatted = format.format(date);
		String dateOnly = formatted.substring(0, formatted.lastIndexOf("/") + 4);
		dateOnly=dateOnly.replace("/", "-");
		System.out.println(dateOnly);
		String timeOnly = formatted.substring(formatted.indexOf(":") + -2);
		System.out.println(timeOnly);
		String[] dateTime = { dateOnly.trim(), timeOnly.trim() };
		return dateTime;
	}
	 
	
	public static String get(String word) {
		Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>^*()%!-]"); //removed dot from this list
		Pattern regex2 = Pattern.compile("\\s");
		if(regex.matcher(word).find() || regex2.matcher(word).find()) {
			word =  "\"*"+word+"*\"" ;
		}
		else {
			word =  "*"+word+"*" ;
		}
		System.out.println("Formattetd String : "+word);
			return word ;
	}
	
	
}
