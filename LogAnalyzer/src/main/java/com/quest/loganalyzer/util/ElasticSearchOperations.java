package com.quest.loganalyzer.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
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

/*ElasticSearchOperations is the class which has the methods for search using
 * elastic search rest apis for keyword and loglevel search as well as date query search.
 * 
 */
public class ElasticSearchOperations {
	
	/*searchCall is the method which searches the index in elasticsearch server with the given inputs
	 *@param String keyword
	 *@param String loglevel
	 *@param String fromDate
	 *@param String toDate
	 *@param String component
	 *
	 * 
	 */
	public static SearchHit[] searchCall(String keyword,String loglevel,String fromDate,String toDate,String component) throws ParseException {
		System.out.println("searchCall");
		RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
		RestHighLevelClient client = new RestHighLevelClient(builder);
		SearchRequest searchRequest = new SearchRequest("sample");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		QueryBuilder matchQueryBuilder = null;
		// Query to return all
		if (keyword.equals(null) && loglevel.equals(null) &&  component.equals(null) &&  fromDate.equals(null) &&  toDate.equals(null))  {
			matchQueryBuilder = QueryBuilders.matchAllQuery();
			System.out.println("String:" + keyword);
		} else {
			System.out.println("Else String:" + keyword);
			String queryString = get(keyword)+ " AND " +
								 get(loglevel)+" AND "+
								 get(component)  ;
			System.out.println("queryString :" + queryString);
			if(fromDate=="") {
					fromDate = "16200";
					System.out.println("queryString from date null :" + fromDate);
			}
			if (toDate=="") {
				long now = Instant.now().toEpochMilli();
				toDate = String.valueOf(now);
				System.out.println("queryString to date null :" + toDate);
			}
			fromDate = dateToEpochTime(fromDate) ;
			toDate = dateToEpochTime(toDate) ;
			System.out.println("Datequery............"+fromDate +  " ----- "+toDate);
			QueryBuilder rangeQuery = QueryBuilders
		                .rangeQuery("@timestamp")
		                .from(fromDate) 
			            .to(toDate) 
		                .includeLower(true)                                      
		                .includeUpper(true);
			QueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders
													  		   .queryStringQuery(queryString)
													  		   .defaultField("message"));
            matchQueryBuilder = QueryBuilders
 		                .boolQuery()
 		               .must(rangeQuery)
 		               .must(boolQuery);
		}
		searchSourceBuilder.query(matchQueryBuilder);
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(10000);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = null;

		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchHits;
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
	
	public static String dateToEpochTime(String date) {
		if(date.length() >13 ) {
			try {	
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dateIn = dateFormat.parse(date);
				long epochTime = dateIn.getTime();
				date = String.valueOf(epochTime);		
			}
			catch(ParseException ex) {
				ex.printStackTrace();
			}	
			return date ;
		}
		else
			return date ;
	}

}
