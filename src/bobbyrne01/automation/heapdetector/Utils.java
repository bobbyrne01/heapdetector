package bobbyrne01.automation.heapdetector;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Utils extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		response.getWriter().write(getFileContents(
				"/usr/local/CustomAppResults/HeapLogger/" + request.getParameter("operation") + ".txt"));
	}
		
	/**
	 * @param file
	 * @return string File contents
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
	    		results += strLine + "<br/>";
	    	}

	    	in.close();
	    }
	    catch (Exception e) {
	    	System.err.println("Error: " + e.getMessage());
	    	return "Error Utils.getFileContents().." + file;
	    }

	    return results;
	}	
}