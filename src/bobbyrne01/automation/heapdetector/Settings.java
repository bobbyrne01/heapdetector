package bobbyrne01.automation.heapdetector;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Settings extends HttpServlet{

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String results = "";
		
		if (request.getParameter("operation").compareTo("targets") == 0){
			
			results = getTargets("/usr/local/CustomAppResults/HeapLogger/targets.txt");
			
		}else if (request.getParameter("operation").compareTo("recipients") == 0){
			
			results = getFileContents("/usr/local/CustomAppResults/HeapLogger/recipients.txt");
			
		}else if (request.getParameter("operation").compareTo("schedule") == 0){
			
			results = getFileContents("/usr/local/CustomAppResults/HeapLogger/schedule.txt");
		}
		
		response.setContentType("text/html");
		response.getWriter().write(results);
		response.getWriter().close();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		boolean result = true;
		
		updateFile("/usr/local/CustomAppResults/HeapLogger/targets.txt", request.getParameter("targets"));
		updateFile("/usr/local/CustomAppResults/HeapLogger/recipients.txt", request.getParameter("recipients"));
		updateFile("/usr/local/CustomAppResults/HeapLogger/schedule.txt", request.getParameter("schedule"));
		
		if (request.getParameter("schedule").compareTo(String.valueOf(0)) == 0){
		
			updateCrontab(request.getParameter("schedule"));
		}else{
			updateCrontab("*/" + request.getParameter("schedule"));
		}
		
		response.setContentType("text/html");
		response.getWriter().write(String.valueOf(result));
		response.getWriter().close();
	}
		
	/**
	 * @param file
	 * @param content
	 * @return result
	 */
	private boolean updateFile(String file, String content) {
		
		boolean result = true;
		
		try{
			//clear current targets
			PrintWriter writer = new PrintWriter(file);
			writer.print("");
			writer.close();
			
			//write updated targets
			writer = new PrintWriter(file);
			writer.print(content);
			writer.close();
		}catch(Exception e){
			System.err.println("Could not update: " + file);
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	/**
	 * @param schedule
	 * @return result
	 */
	private boolean updateCrontab(String schedule){
		
		try{
			
            String updateCrontabCmd = 
            		this.getServletContext().getRealPath("/") + "tools/updateCrontab.sh " + 
            		schedule;  
            
            Process proc = Runtime.getRuntime().exec(updateCrontabCmd);  
            
            BufferedReader is = null;
            BufferedReader es = null;
            String line;
            is = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while((line = is.readLine()) != null)
                System.out.println(line);
            es = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while((line = es.readLine()) != null)
                System.err.println(line);

        } catch (IOException e){  
	        System.err.println("exec error: " + e);  
	        e.printStackTrace();
	    }
		
		return true;
	}

	/**
	 * @param file
	 * @return
	 */
	private String getTargets(String file){
		
	    String path = file;
	    String results = "";
	    
	    try{
	    	FileInputStream fstream = new FileInputStream(path);

	    	DataInputStream in = new DataInputStream(fstream);
	    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    	String strLine;
	      
	    	while ((strLine = br.readLine()) != null){
	    		
	    		if (br.ready()){
	    			results += strLine + "\n";
	    		
	    		}else{
	    			results += strLine;
	    		}
	    	}	    	

	    	in.close();
	    }
	    catch (Exception e) {
	    	System.err.println("Error: " + e.getMessage());
	    	return "Error Settings.getTargets().." + file;
	    }

	    return results;
	}	
	
	/**
	 * @param file
	 * @return
	 */
	private String getFileContents(String file){
		
	    String path = file;
	    String results = "";
	    
	    try{
	    	FileInputStream fstream = new FileInputStream(path);

	    	DataInputStream in = new DataInputStream(fstream);
	    	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    	String strLine;
	      
	    	while ((strLine = br.readLine()) != null){
	    		results += strLine;
	    	}

	    	in.close();
	    }
	    catch (Exception e) {
	    	System.err.println("Error: " + e.getMessage());
	    	return "Error Settings.getFileContents().." + file;
	    }

	    return results;
	}
}