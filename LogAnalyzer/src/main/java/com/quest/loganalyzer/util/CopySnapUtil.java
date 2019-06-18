package com.quest.loganalyzer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*CopySnapUtil is the util class contains the utility methods for checking loigstash
 * server status,
 * set env var of the folder loc,
 * get reload success count
 * 
 */
public class CopySnapUtil {
	
	/*checkLogStashServerStatus is the method used to check if logstash server is up and running
	 * @param none
	 * @return boolean
	 */
	public static boolean checkLogStashServerStatus() throws IOException {
		boolean status = false;
		String line;
		String pidInfo = "";
		BufferedReader input =null ;
		Process p = null;
		//try {
			System.out.println();
			p = Runtime.getRuntime().exec("\""+ServiceConstants.JDK_PATH+"/bin/jps\"");
			input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				pidInfo += line;
				System.out.println(pidInfo);
				if (pidInfo.contains("Logstash")) {
					status = true;
					break;
				}
			}
			input.close();	
		/*} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			input.close();
		}*/
		return status;

	}
	
	public static void setSnapLocEnvVar(String snapFolder) throws IOException {
		Runtime.getRuntime().exec("SETX SPRSNAP_DIR " + snapFolder);
	}
	/*getLogstashReloadCount is the method which opens the
	 * get request url for node reload status and returns the logstash reload success count
	 * @param none
	 * @return long
	 */
	public static long getLogstashReloadCount() {
		String inline = "";
		long count = 0;
		try {
			URL reloadStatURL = new URL("http://localhost:9600/_node/stats/reloads?pretty");
			HttpURLConnection conn =  (HttpURLConnection) reloadStatURL.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			int responseCode = conn.getResponseCode();
			if(responseCode !=  200) {
				throw new RuntimeException("HttpResponseCode : "+ responseCode);
			}
			else
			{
				Scanner sc = new Scanner(reloadStatURL.openStream());
				while(sc.hasNext()) {
					inline += sc.nextLine();
				}
				sc.close();
			}
			JSONParser parse = new JSONParser();
			JSONObject jobj = (JSONObject) parse.parse(inline);
			Map reloadVal = (Map)jobj.get("reloads");
			count = (long)reloadVal.get("successes");
			conn.disconnect();
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return count ;
	}
}
