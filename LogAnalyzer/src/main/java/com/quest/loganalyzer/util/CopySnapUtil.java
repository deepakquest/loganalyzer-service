package com.quest.loganalyzer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class CopySnapUtil {
	public static boolean checkLogStashServerStatus() throws IOException {
		boolean status = false;
		String line;
		String pidInfo = "";
		BufferedReader input =null ;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec("\""+ServiceConstants.JDK_PATH+"/bin/jps\"");
			input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				pidInfo += line;
				System.out.println(pidInfo);
				if (pidInfo.contains("Logstash")) {
					status = true;
					System.out.println(" Logstash is running..........");
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			input.close();
		}
		return status;

	}
	
	public static void setSnapLocEnvVar(String snapFolder) throws IOException {
		Runtime.getRuntime().exec("SETX SPRSNAP_DIR " + snapFolder);
		
		/*try {
			ProcessBuilder pb = new ProcessBuilder("CMD", "/C", "SET");
		    Map<String, String> env = pb.environment();
		    env.put("Test5", snapFolder);
		    Process p;
			p = pb.start();
			 InputStreamReader isr = new InputStreamReader(p.getInputStream());
			    char[] buf = new char[1024];
			    while (!isr.ready()) {
			        ;
			    }
			    while (isr.read(buf) != -1) {
			        System.out.println(buf);
			    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.println(" Succeesfullt set env var with :"+snapFolder);
	}
}
